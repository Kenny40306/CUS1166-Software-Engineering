import java.util.ArrayList;
import java.util.List;

/*=====================
 Class Server - Manages and tracks job processing within the system.
======================*/

public class Server {

    // Nested enum representing the possible states of the server.
   
    public enum ServerStatus {
    ONLINE,
    OFFLINE,
    FULL
}

    // A unique identifier used to distinguish this server from other servers.
    private String serverID;

    // A list that stores jobs currently assigned to the server and waiting to be processed.
    private List<Job> storage = new ArrayList<>();

    // A list that stores jobs that have finished processing and whose results are saved.
    private List<Job> completedJobs = new ArrayList<>();

    // Represents the current state of the server (e.g., ONLINE, OFFLINE, FULL).
    private ServerStatus status;

    // Initializes the server with a unique server ID and sets the default status to ONLINE.
    public Server(String serverID) {
        this.serverID = serverID;
        this.status = ServerStatus.ONLINE;
    }
}

//METHODS   