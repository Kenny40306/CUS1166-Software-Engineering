import java.util.ArrayList;
import java.util.List;

public class VcController {
    
    // Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles;
    private List<Job> activeJobs;
    private Server serverConnection;
 
    // Constructors
 
    public VCController(String controllerID, Server serverConnection) {
        this.controllerID    = controllerID;
        this.serverConnection = serverConnection;
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs        = new ArrayList<>();
    }

    //Methods

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
 

