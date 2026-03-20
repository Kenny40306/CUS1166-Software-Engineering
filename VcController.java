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

    // Methods

    public void distributeJob(Job j) {
        if (j == null) {
            System.out.println("[VCController] Cannot distribute a null job.");
            return;
        }

        activeJobs.add(j);
        j.setProgressStatus(JobStatus.IN_PROGRESS);
        System.out.println("[VCController] Job " + j.getJobID() + " added to active queue.");
        assignVehicles(j);
    }

   
    public void assignVehicles(Job j) {
        if (j == null) {
            System.out.println("[VCController] Cannot assign vehicles to a null job.");
            return;
        }

        List<Vehicle> selected = new ArrayList<>();
        int needed = j.getRedundancyLevel();

        for (Vehicle v : connectedVehicles) {
            if (selected.size() >= needed)
                break;
            if (v.isAvailable() && v.getStatus() == VehicleStatus.AVAILABLE) {
                selected.add(v);
                v.acceptJob(j);
                v.updateStatus();
            }
        }

        if (!selected.isEmpty()) {
            j.assignVehicles(selected);
            System.out.println("[VCController] Assigned " + selected.size()
                    + " vehicle(s) to job " + j.getJobID());
        } else {
            System.out.println("[VCController] No available vehicles for job " + j.getJobID());
        }
    }

  
    public void monitorVehicles() {
        System.out.println("[VCController] Monitoring " + connectedVehicles.size() + " vehicle(s)...");
        for (Vehicle v : new ArrayList<>(connectedVehicles)) {
            if (v.getStatus() == VehicleStatus.DEPARTING
                    || v.getStatus() == VehicleStatus.OFFLINE) {
                System.out.println("[VCController] Vehicle " + v.getVehicleID()
                        + " is " + v.getStatus() + ". Initiating departure handling.");
                handleVehicleDeparture(v);
            }
        }
    }

  
    public void getResults() {
        System.out.println("[VCController] Collecting results for active jobs...");
        for (Job j : new ArrayList<>(activeJobs)) {
            if (j.getProgressStatus() == JobStatus.COMPLETED) {
                serverConnection.storeResults(j);
                activeJobs.remove(j);
                System.out.println("[VCController] Results for job " + j.getJobID()
                        + " sent to server.");
            }
        }
    }

  
    public void manageCheckpoints(Job j) {
        if (j == null) {
            System.out.println("[VCController] Cannot manage checkpoints for a null job.");
            return;
        }

        if (j.getProgressStatus() == JobStatus.FAILED) {
            System.out.println("[VCController] Job " + j.getJobID()
                    + " failed. Attempting checkpoint restore...");
            j.restoreFromCheckpoint(null); // passes to Job to handle internally
        } else {
            j.createCheckpoint();
            System.out.println("[VCController] Checkpoint created for job " + j.getJobID());
        }
    }

   
    public void recruitVehicle(Vehicle v) {
        if (v == null) {
            System.out.println("[VCController] Cannot recruit a null vehicle.");
            return;
        }

        if (!connectedVehicles.contains(v)) {
            connectedVehicles.add(v);
            v.updateStatus();
            System.out.println("[VCController] Vehicle " + v.getVehicleID() + " recruited.");
        } else {
            System.out.println("[VCController] Vehicle " + v.getVehicleID()
                    + " is already registered.");
        }
    }

   
    public void handleVehicleDeparture(Vehicle v) {
        if (v == null) {
            System.out.println("[VCController] Cannot handle departure of a null vehicle.");
            return;
        }

        System.out.println("[VCController] Handling departure of vehicle " + v.getVehicleID());

        // Save checkpoint for any active job before the vehicle leaves
        Job currentJob = v.getCurrentJob();
        if (currentJob != null
                && currentJob.getProgressStatus() == JobStatus.IN_PROGRESS) {
            manageCheckpoints(currentJob);
            // Reassign the job to another vehicle
            assignVehicles(currentJob);
        }

        v.eraseData();
        serverConnection.eraseData(v);
        connectedVehicles.remove(v);

        System.out.println("[VCController] Vehicle " + v.getVehicleID()
                + " removed from cloud.");
    }

    
    public void setRedundancyLevel(Job j, int level) {
        if (j == null) {
            System.out.println("[VCController] Cannot set redundancy on a null job.");
            return;
        }
        if (level < 1) {
            System.out.println("[VCController] Redundancy level must be at least 1.");
            return;
        }

        j.setRedundancyLevel(level);
        System.out.println("[VCController] Redundancy level for job " + j.getJobID()
                + " set to " + level);
    }

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
