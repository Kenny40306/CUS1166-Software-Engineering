import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Server Socket that handles network connections I/O
//Server listens for incoming client request from the port.
//Then causes ClientHandler thread to be created 
public class VCControllerServerConnection {

    private int port; 
    private VCController controller; //reference VCController for job/decision logic
    private ExecutorService pool;

    public VCControllerServerConnection(int port, VCController controller) {
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
               
                // Handle client in a separate thread using ClientHandler Method
                pool.execute(() -> clientHandler(clientSocket));
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
    
    //Uses VCControllerServer Socket 
    //Handles one client connection (bridge between network and business logic)
    //Receives and Sends jobs to VCController
    private void clientHandler(Socket socket) {
    	try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
    			ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); //receive job from client
            ) {

    			ClientConnection.MessageServer msg = (ClientConnection.MessageServer) in.readObject();
   
                //Send ACK immediately
                out.writeObject(new ClientConnection.MessageServer(ClientConnection.MessageServer.Type.ACK, "Request received", "SERVER"));
                out.flush();
                
                //Route For Request
                String requestID = null;
                String result = null;

                switch (msg.getType()) {

                	//Handle User 1 (JobOwner)
                    case JOB_REQUEST: //First user type
                    	requestID = controller.receiveJobRequest((Job) msg.getData(), msg.getSenderId());//pass to VCController
                        break;
                  
                    //Handle User 2 (VehicleOwner)
                    case VEHICLE_REQUEST: //Second user type
                    	requestID = controller.receiveVehicleRequest((Vehicle) msg.getData(), msg.getSenderId());
                        break;
                    
                    //Error message
                    default: controller.logServerMessage("Unknown message type received");     
                    
                    return;  
                }
          
                //---IMPORTANT Wait for admin decision (CALLED IN VCCONTROLLER CLASS) 
                // SAFETY CHECK 
                if (requestID == null) {
                    controller.logServerMessage("Request rejected before queue (duplicate or error)");
                    return;
                }
                result = controller.waitForDecision(requestID);
                //-------------------------------------------------------------
                
                
                // Send final result back to client
                out.writeObject(new ClientConnection.MessageServer(ClientConnection.MessageServer.Type.RESPONSE, result, "SERVER"));
                out.flush();
                
            } catch (Exception e) {
                e.printStackTrace();
                controller.logServerMessage("ClientHandler ERROR: " + e.getMessage());
           
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }