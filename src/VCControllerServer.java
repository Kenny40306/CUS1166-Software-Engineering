import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Server Socket that handles network connections I/O
//Server listens for incoming client request from the port.
//Then causes ClientHandler thread to be created 
public class VCControllerServer {

    private int port; 
    private VCController controller; //reference VCController for job/decision logic
    private ExecutorService pool;

    public VCControllerServer(int port, VCController controller) {
        this.port = port;
        this.controller = controller;
        this.pool = Executors.newCachedThreadPool();
    }

    //For each client, accept the connection and start ClientHandler class using thread ExectorService
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //<- server socket created here
        	
        	// Log server start including port to Global Notifs
            if (controller != null) {
                controller.logServerMessage("Server started on port " + port);
            } else {
                System.out.println("Server started on port " + port);
            }
            
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); //<- waits for client to connect (implicit call to ClientConnection via network message)
                if (controller != null) {
                    controller.logServerMessage("Client connected: " + clientSocket.getInetAddress()); //socket class java
                }
                // Handle client in a separate thread using ClientHandler 
                pool.execute(new ClientHandler(clientSocket, controller));
            }
             
        } catch (IOException e) {
            e.printStackTrace();
            
            //Log error to ServerFrame
            if (controller != null) {
            	controller.openServerFrame(null); // null if no reference JFrame
                controller.logServerMessage("Server ERROR: " + e.getMessage());
            }
        }
    }
}