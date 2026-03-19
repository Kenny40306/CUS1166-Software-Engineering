import java.util.ArrayList;
import java.util.List;
/*=====================
Class Server Logic - Moontarin
======================*/

// Manages and tracks job processing within the system.
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
    private List<Job> storage;

    // A list that stores jobs that have finished processing and whose results are saved.
    private List<Job> completedJobs;

    // Represents the current state of the server (e.g., ONLINE, OFFLINE, FULL).
    private ServerStatus status;

    // Initializes the server with a unique server ID and sets the default status to ONLINE.
    public Server(String serverID) {
        this.serverID = serverID;
        this.storage = new ArrayList<>();
        this.completedJobs = new ArrayList<>();
        this.status = ServerStatus.ONLINE;
    }

    // Accepts a job ID and prints a message indicating the job has been received by the server.
    public void receiveJob(Job job) {
    	storage.add(job);
        System.out.println("Job " + job.getJobID() + " accepted into server " + serverID);
    }

    // Adds a completed job to the completedJobs list and stores its results on the server.
    public void storeResults(Job j) {
        completedJobs.add(j);
        storage.remove(j); // removes from active storage
        System.out.println("Job " + j.getJobName() + " results stored on server " + serverID);
    }

    //Move job to completed (simulate processing)
    public void completedJob(Job j) {
    	if (storage.contains(j)) {
    		j.markCompleted();
    		storeResults(j);
    	}else {
    		System.out.println("Job not found in storage");
    	}
    }
    
    // Removes or deletes stored data related to a specific vehicle from the server.
    public void eraseData(Vehicle v) {
        System.out.println("Vehicle data for " + v.getVehicleID() + " erased from server " + serverID);
    }

    // Updates the server status and prints the new status.
    public void updateStatus(ServerStatus s) {
        this.status = s;
        System.out.println("Server " + serverID + " status updated to: " + s);
    }

    // Returns the list of jobs that have been completed or stored on the server.
    public List<Job> getCompletedJobs(List<Job> j) {
        return completedJobs;
    }

    // Returns the current status of the server.
    public ServerStatus getStatus(ServerStatus s) {
        return status;
    }

    // Returns the unique identifier of the server.
    public String getServerID() { 
    	return serverID; 
    }

    // Returns the list of jobs currently stored and waiting to be processed.
    public List<Job> getStorage() { 
    	return storage; 
    }
    
    // toString() - Returns a formatted string describing the server object including:
    // serverID, status, number of stored jobs, and number of completed jobs.
    @Override
    public String toString() {
        return "Server{" +
                "serverID='" + serverID + '\'' +
                ", status=" + status +
                ", storageSize=" + storage.size() +
                ", completedJobs=" + completedJobs.size() +
                '}';
    }
}
