import java.io.*;
import java.net.Socket;

//Client side Socket network
//(created when JobOwner calls thread String response = connection.send(message);)
//Acts as bridge between JobOwner(Client) and VCControllerServer
public class ClientConnection {

	//Encapsulate messages that travel between client and server over sockets
	//Needs to be serializable so it can be sent over to method ObjectOutput/InputStream
	public static class MessageServer implements Serializable {
		private static final long serialVersionUID = 1L; //safe declaration

		//Enum acts as data packet for socket communication (modifiable) 
	    public enum Type {
	        JOB_REQUEST, //User 1
	        VEHICLE_REQUEST, //User 2
	        RESPONSE, //Client Response 
	        ACK //Server acknowledgement
	    }

	    private Type type; //nested enums type of message
	    private Object data; //actual job or vehicle object
	    private String senderId; //identifier user who sent message
   
	    public MessageServer(Type type, Object data, String senderId) {
	        this.type = type;
	        this.data = data;
	        this.senderId = senderId;
	    }

	    public Type getType() {
	        return type;
	    }
	    public Object getData() {
	        return data;
	    }
	    public String getSenderId() {
	        return senderId;
	    }
	}
	
	//connects to server using host/port
    private String host; 
    private int port;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String send(MessageServer message) { //sends message using MessageServer class
        try (
            Socket socket = new Socket(host, port);  //<- client socket created here
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {

            //Send request to server by opening socket to server's host and port
            out.writeObject(message);
            out.flush();
            
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