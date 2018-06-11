import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class Main extends Application {

    public static void main(String[] args){
        System.out.println("start PlexMedia");
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("GUIPlexMedia");
        Button button= new Button("getProjections");

        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("click btn_getProjections");
                RmiClient rmiClient = null;
                try {
                    rmiClient = new RmiClient((IServerApi) Naming.lookup("//localhost:4321/RmiService"));
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (NotBoundException e1) {
                    e1.printStackTrace();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                try {
                    rmiClient.getProjectionsFromPlexAdmin();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

            }
        });

        StackPane layout= new StackPane();
        layout.getChildren().add(button);

        Scene scene1= new Scene(layout, 300, 250);
        primaryStage.setScene(scene1);

        primaryStage.show();

    }



    private class RmiClient extends UnicastRemoteObject implements IClientApi {
        private IServerApi serverApi = null;
        private Data data = null;

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
        protected RmiClient(IServerApi serverApi) throws RemoteException {
            try {
                this.serverApi = serverApi;
                this.serverApi.addObserver(this);
            }  catch (Exception e) {
                e.printStackTrace();
            }

        }

        public Data getProjections() throws RemoteException {
            try {
            data = serverApi.getData();
            }  catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        public void getProjectionsFromPlexAdmin() throws RemoteException {
            try {
                System.out.println(getProjections().getJsonData());
            }  catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {

        }
    }
}
