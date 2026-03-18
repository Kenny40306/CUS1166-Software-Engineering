import java.util.ArrayList;
import java.util.List;

/*=====================
Class VC Controller Logic - Ryan
======================*/

public class VCController {
	 // Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles;
    private List<Job> activeJobs;
    private Server serverConnection;
 
    // Constructors
 
    public VCController(String controllerID, Server serverConnection) {
        this.controllerID    = controllerID;
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs        = new ArrayList<>();
        this.serverConnection = serverConnection;
    }

    //Methods

    //Core FIFO Method
    public List<Long> calculateCompletionTimes() {
        List<Long> completionTimes = new ArrayList<>();
 
        if (activeJobs.isEmpty()) {
            System.out.println("[VCController] No active jobs to calculate completion times for.");
            return completionTimes;
        }
 
        long cumulativeTime = 0;
        
        for (Job j : activeJobs) {
            long durationMinutes = j.getDuration().toMinutes();
            cumulativeTime += durationMinutes;
            completionTimes.add(cumulativeTime);
 
            System.out.println("[VCController] Job " + j.getJobID()
                    + " | Duration: " + durationMinutes
                    + " min | Completion Time: " + cumulativeTime + " min");
        }
 
        return completionTimes;
    }
    //(Method uses (Job j) and (Jobowner client) that calls from Job and JobOwner Classes)
    
    //Receive active job from Client to send to server
    public void receiveJob(Job j, JobOwner client) {
    	activeJobs.add(j);
    	serverConnection.receiveJob(j);
    	
    	System.out.println("[VCController] Job received from client " + client.getClientID()+ ": "+ j.getJobName());
    }
    //Manage checkpoints
    public void manageCheckpoints(Job j) {
    	System.out.println("[VCController] Managing checkpoints for job: " + j.getJobName());
    }
    
    //Set Redundancy Level
    public void setRedundancyLevel(Job j, int level) {
    	System.out.println("[VCController] Setting redundancy level for job: " + j.getJobName() + "to " + level);
    }
    
    

    // Getters & Setters
 
    public String getControllerID() {
        return controllerID;
    }
 
    public void setControllerID(String controllerID) {
        this.controllerID = controllerID;
    }
 
    public List<Vehicle> getConnectedVehicles() {
        return connectedVehicles;
    }
 
    public List<Job> getActiveJobs() {
        return activeJobs;
    }
 
    public Server getServerConnection() {
        return serverConnection;
    }
 
    public void setServerConnection(Server serverConnection) {
        this.serverConnection = serverConnection;
    }
 
    @Override
    public String toString() {
        return "VCController{"
                + "controllerID='" + controllerID + '\''
                + ", connectedVehicles=" + connectedVehicles.size()
                + ", activeJobs=" + activeJobs.size()
                + '}';
    }
}
