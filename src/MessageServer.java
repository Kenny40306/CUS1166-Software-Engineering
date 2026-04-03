import java.io.Serializable;

//Encapsulate messages that travel between client and server over sockets
//Needs to be serializable so it can be sent over to method ObjectOutput/InputStream

public class MessageServer implements Serializable {
	private static final long serialVersionUID =1L;

	//Enum acts as data packet for socket communication
    public enum Type {
        JOB_REQUEST,
        VEHICLE_REQUEST,
        APPROVE_JOB,
        REJECT_JOB,
        RESPONSE,
        ACK
    }

    private Type type; //nested enums type of message
    private Object data; //actual job or vehicle object
    private String sender; //identifier user who sent message

    
    public MessageServer(Type type, Object data, String senderId) {
        this.type = type;
        this.data = data;
        this.sender = senderId;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getSenderId() {
        return sender;
    }
}