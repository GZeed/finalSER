
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Wrapper;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import db.MySQLAccess;
import ch.heigvd.iict.ser.imdb.models.Data;

public class Main extends Observable implements IServerApi {

	private IServerApi serverApi = null;
	static {
		// this will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("MySQL drivers not found !");
			System.exit(1);
		}

		//database configuration
		MySQLAccess.MYSQL_URL 		= "docr.iict.ch";
		MySQLAccess.MYSQL_DBNAME 	= "imdb";
		MySQLAccess.MYSQL_USER 		= "imdb";
		MySQLAccess.MYSQL_PASSWORD 	= "imdb";
	}

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}

	private Data lastData = null;

	private void run() {

		boolean continuer = true;		
		while(continuer) {
			System.out.print("Select the data version to download [1/2/3/0=quit]: ");
			int choice = -1;
			try {
				choice = scanner.nextInt();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(choice == 0) continuer = false;
			else if(choice >= 1 && choice <= 3) {
				Worker worker = new Worker(choice);
				this.lastData = worker.run();

				//TODO notify client
				if(lastData != null){
					setChanged();
					notifyObservers(lastData);
					System.out.println("new observers updated");
				}
				
			}
		}
	}

	/**
	 * Method used by clients to register on the server
	 *
	 * @param client The client
	 * @throws RemoteException
	 */
	@Override
	public void addObserver(IClientApi client) throws RemoteException {
		WrappedObserver wrappedObserver = new WrappedObserver(client);
		addObserver(wrappedObserver);
		System.out.println("wrappedObserver added");
	}

	/**
	 * Method used by clients to check the connection with the server
	 *
	 * @return true is the server is reachable
	 * @throws RemoteException
	 */
	@Override
	public boolean isStillConnected() throws RemoteException {
		return serverApi != null;
	}

	/**
	 * Method used by clients to get all the data
	 *
	 * @return The data
	 * @throws RemoteException
	 */
	@Override
	public Data getData() throws RemoteException {
		return lastData;
	}

	private class WrappedObserver implements Observer, Serializable{
		private static final long serialVersionUID = -2067345842536415833L;

		IClientApi clientApi = null;

		public WrappedObserver(IClientApi clientApi) {
			this.clientApi = clientApi;
		}

		/**
		 * This method is called whenever the observed object is changed. An
		 * application calls an <tt>Observable</tt> object's
		 * <code>notifyObservers</code> method to have all the object's
		 * observers notified of the change.
		 *
		 * @param o   the observable object.
		 * @param arg an argument passed to the <code>notifyObservers</code>
		 */
		@Override
		public void update(Observable o, Object arg) {
			try{
				clientApi.update(o.toString(), IClientApi.Signal.UPDATE_REQUESTED, arg.toString());
			}catch(RemoteException e){
				System.out.println("Remote exception removing observer : " + this);
				o.deleteObserver(this);
			}
		}
	}
}
