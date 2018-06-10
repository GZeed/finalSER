package ch.heigvd.iict.ser.rmi.server;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import ch.heigvd.iict.ser.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IClientApi.Signal;
import ch.heigvd.iict.ser.rmi.IServerApi;

/**
 * @author https://sites.google.com/site/jamespandavan/Home/java/sample-remote-observer-based-on-rmi
 */
public class RmiServer extends Observable implements IServerApi {

	private static final Random RAND = new Random();
	
    public static void main(String[] args) {
        try {
            Registry rmiRegistry = LocateRegistry.createRegistry(9999);
            IServerApi rmiService = (IServerApi) UnicastRemoteObject.exportObject(new RmiServer(), 9999);
            rmiRegistry.bind("RmiService", rmiService);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
    private List<Data> dataBase = null;
    
    public RmiServer() {
    	//we will access the list by different threads
    	this.dataBase = Collections.synchronizedList(new LinkedList<Data>());
    	this.thread.start();
    }
    
    /*
     * API - RmiService implementation
     */
	
    @Override
    public void addObserver(IClientApi client) throws RemoteException {
        WrappedObserver wo = new WrappedObserver(client);
        addObserver(wo);
        System.out.println("Added observer: " + wo);
    }

    @Override
    public boolean isStillConnected() throws RemoteException {
    	return true;
    }
    
	@Override
	public List<Data> getData() throws RemoteException {
		return this.dataBase;
	}

	@Override
	public List<Data> getDataFrom(Date date) throws RemoteException {
		//if there is no date set, we return all data
		if(date == null) return this.getData();
		
		List<Data> dataToSend = new ArrayList<Data>();
		
		synchronized (this.dataBase) {
			for(Data d : this.dataBase) {
				if(d.getDate().after(date)) {
					dataToSend.add(d);
				}
			}
		}
		
		return dataToSend;
	}
	
	/*
	 * Main server thread
	 */
	private Thread thread = new Thread() {
        @Override
        public void run() {
        	System.out.println("Server started");
            while (true) {
            	
                try {
                    Thread.sleep(RAND.nextInt(30) * 1000);
                } catch (InterruptedException e) {}
                
                //we determine how many data we need to generate
                int nbrData = RAND.nextInt(5);
                long time = System.currentTimeMillis();
                
                synchronized(dataBase) {
                	 for(int i = 0; i < nbrData; ++i) {
                     	Data d = new Data();
                     	d.setDate(new Date(time+i));
                     	d.setValue(RAND.nextInt(10000));
                     	
                     	//we add the new element at the first position of the list to keep it sorted
                     	dataBase.add(0, d);
                     }
				}
                
               if(nbrData > 0) {
            	   //we notify observers (clients) that new data are available
            	   setChanged();
                   notifyObservers(new Date());
               }
               
            }
        };
    };
	
    /*
     *  Observer
     */
	private class WrappedObserver implements Observer, Serializable {

		private static final long serialVersionUID = -2067345842536415833L;
		
		private IClientApi ro = null;

        public WrappedObserver(IClientApi ro) {
            this.ro = ro;
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                ro.update(o.toString(), Signal.UPDATE_REQUESTED, arg.toString());
            } catch (RemoteException e) {
                System.out.println("Remote exception removing observer: " + this);
                o.deleteObserver(this);
            }
        }
    }
	
}
