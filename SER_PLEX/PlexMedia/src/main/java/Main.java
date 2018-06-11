import ch.heigvd.iict.ser.rmi.IClientApi;

import java.io.Console;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args){
        System.out.println("start PlexMedia");

    }

    private class RmiClient extends UnicastRemoteObject implements IClientApi {


        /**
         * Creates and exports a new UnicastRemoteObject object using an
         * anonymous port.
         * <p>
         * <p>The object is exported with a server socket
         * created using the {@link RMISocketFactory} class.
         *
         * @throws RemoteException if failed to export object
         * @since JDK1.1
         */
        protected RmiClient() throws RemoteException {
        }

        public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {
            
        }
    }
}
