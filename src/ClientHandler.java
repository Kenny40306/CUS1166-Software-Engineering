import java.io.*;
import java.net.Socket;

//Handles once client connection (bridge between network and business logic)
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
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {

            MessageServer msg = (MessageServer) in.readObject();

            //Send ACK immediately
            out.writeObject(new MessageServer(MessageServer.Type.ACK, "Request received", "SERVER"));

            switch (msg.getType()) {

                case JOB_REQUEST: //First user type
                    controller.receiveJobRequest((Job) msg.getData(), msg.getSenderId());
                    break;

                /*case VEHICLE_REQUEST: //Second user type
                    controller.receiveVehicleRequest((Vehicle) msg.getData(), msg.getSenderId());
                    break;*/
            }

            // Wait for admin decision
            String result = controller.waitForDecision(msg.getSenderId());

            // Send final result
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