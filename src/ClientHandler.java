import java.io.*;
import java.net.Socket;

//Uses VCControllerServer Socket 
//Handles one client connection (bridge between network and business logic)
//Receives and Sends jobs to VCController
public class ClientHandler implements Runnable {

    private Socket socket;
    private VCController controller;

    public ClientHandler(Socket socket, VCController controller) {
        this.socket = socket;
        this.controller = controller;
    }

    @Override
    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); //receive job from client
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {

            MessageServer msg = (MessageServer) in.readObject(); //recieve

            //Send ACK immediately
            out.writeObject(new MessageServer(MessageServer.Type.ACK, "Request received", "SERVER"));
            out.flush();
            
            switch (msg.getType()) {

            	//Handle User 1 (JobOwner)
                case JOB_REQUEST: //First user type
                    controller.receiveJobRequest((Job) msg.getData(), msg.getSenderId());//pass to VCController
                    break;
              
                //Handle User 2 (VehicleOwner)
                case VEHICLE_REQUEST: //Second user type
                    controller.receiveVehicleRequest((Vehicle) msg.getData(), msg.getSenderId());
                    break;
                
                //Error message
                default:
                    controller.logServerMessage("Unknown message type received");
            }

            // Wait for admin decision
            String result = controller.waitForDecision(msg.getSenderId());

            // Send final result back to client
            out.writeObject(new MessageServer(MessageServer.Type.RESPONSE, result, "SERVER"));
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