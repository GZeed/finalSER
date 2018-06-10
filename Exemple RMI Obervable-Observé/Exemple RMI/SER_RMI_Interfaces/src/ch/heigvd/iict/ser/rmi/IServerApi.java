package ch.heigvd.iict.ser.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import ch.heigvd.iict.ser.models.Data;

/**
 * @author https://sites.google.com/site/jamespandavan/Home/java/sample-remote-observer-based-on-rmi
 */
public interface IServerApi extends Remote {

	/**
	 * Method used by clients to register on the server
	 * @param client The client
	 * @throws RemoteException
	 */
	void addObserver(IClientApi client) throws RemoteException;
	
	/**
	 * Method used by clients to check the connection with the server
	 * @return true is the server is reachable
	 * @throws RemoteException
	 */
	boolean isStillConnected() throws RemoteException;
	
	/**
	 * Method used by clients to get all the available data
	 * @return The list of data
	 * @throws RemoteException
	 */
	List<Data> getData() throws RemoteException;
	
	/**
	 * Method used by clients to get all the available data more recent than the date
	 * @param date The date
	 * @return The list of data
	 * @throws RemoteException
	 */
	List<Data> getDataFrom(Date date) throws RemoteException;

}
