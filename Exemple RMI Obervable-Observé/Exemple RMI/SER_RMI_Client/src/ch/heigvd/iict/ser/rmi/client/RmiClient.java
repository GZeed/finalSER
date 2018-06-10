package ch.heigvd.iict.ser.rmi.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.heigvd.iict.ser.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;

/**
 * @author https://sites.google.com/site/jamespandavan/Home/java/sample-remote-observer-based-on-rmi
 */
public class RmiClient extends UnicastRemoteObject implements IClientApi {

	private static final long serialVersionUID = -8478788162368553187L;

	public static void main(String[] args) {
		try {
			//we connect to server
			IServerApi remoteService = (IServerApi) Naming.lookup("//localhost:9999/RmiService");

			//we start client
			RmiClient client = new RmiClient(remoteService);
			client.initialConnection();
			client.startCheckingThread();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private IServerApi remoteService = null;
	private List<Data> dataBase = null;

	protected RmiClient(IServerApi remoteService) throws RemoteException {
		super();
		this.remoteService = remoteService;
		this.dataBase = new ArrayList<Data>();
	}
	
	protected void initialConnection() throws RemoteException {
		//we register to server
		remoteService.addObserver(this);
		
		//initial request, we download all existing data
		this.dataBase.addAll(remoteService.getData());
		
		//we sort them
		Collections.sort(this.dataBase);
		
		//display
		System.out.println("Initial request: ");
		System.out.println(this.dataBase);
	}
	
	protected void startCheckingThread() {
		//we start a thread to periodically check if the server is available
		Thread thread = new Thread(){
			@Override
			public void run() {
				boolean isStillConnectedToServer = true;
				while(isStillConnectedToServer) {
					//every 10 seconds
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) { }
					try {
						isStillConnectedToServer = remoteService.isStillConnected();
					} catch (RemoteException e) {
						isStillConnectedToServer = false;
					}
				}
				System.err.println("Server is not avalaible anymore, we stop client");
				System.exit(1);
			};
		};
		thread.start();
	}
	
	/*
	 * API - RemoteObserver implementation
	 */
	@Override
	public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {
		//we log the received signal
		System.out.println("got signal [" + signalType.name() + "]: " + updateMsg);

		//we request for new data
		//local data in dataBase are sorted, we pick the most recent at the first position
		Date lastDataDate = null;
		if(!this.dataBase.isEmpty()) {
			lastDataDate = this.dataBase.get(0).getDate();
		}
		
		List<Data> newDataDownloaded = remoteService.getDataFrom(lastDataDate);
		
		//we add them to the local database
		if(newDataDownloaded != null) {
			this.dataBase.addAll(newDataDownloaded);
			//we sort them
			Collections.sort(this.dataBase);
		}
		
		System.out.println("Update request: ");
		System.out.println(this.dataBase);
		
	}

}
