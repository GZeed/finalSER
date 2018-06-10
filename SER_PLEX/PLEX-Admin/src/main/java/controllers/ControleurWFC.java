package controllers;

import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import views.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ControleurWFC extends UnicastRemoteObject implements IClientApi {

	private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;

	private IServerApi serverApi = null;
	Data database = null;
	private static final long serialVersionUID = -8478788162368553187L;

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
	public ControleurWFC(ControleurGeneral ctrGeneral, MainGUI mainGUI) throws RemoteException{
		this.ctrGeneral=ctrGeneral;
		ControleurWFC.mainGUI=mainGUI;
		try {
			IServerApi remoteServiceApi = (IServerApi) Naming.lookup("//localhost:9999/RmiService");
			this.serverApi.addObserver(this);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {
		database = serverApi.getData();
		ctrGeneral.initBaseDeDonneesAvecNouvelleVersion(database);
	}
}