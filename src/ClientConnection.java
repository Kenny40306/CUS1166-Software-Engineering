import java.io.*;
import java.net.Socket;

//Handles Socket communication from client side
//Acts as bridge between JobOwner(Client) and VCControllerServer
public class ClientConnection {

	//connects to server using host/port
    private String host; 
    private int port;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String send(MessageServer message) { //sends message using MessageServer class
        try (
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
        	System.out.println("Connected to server...");
            //Send request to server
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent: " + message.getType());
            
            //Receive ACK
            MessageServer ack = (MessageServer) in.readObject();
            System.out.println("ACK from server: " + ack.getData());

            //Receive final response (Approved/Rejected)
            MessageServer response = (MessageServer) in.readObject();
            return (String) response.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}