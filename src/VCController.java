import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JTextArea;

/*=====================
Class VC Controller Logic - Ryan
======================*/

//----M4 Implementation: 
public class VCController {
	 // Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles;
    private List<Job> activeJobs; //for all job submission history
    private List<Job> currentBatch; //for FIFO calculation
    private Set<String> jobIDs; //for FIFO calculation
    private Server serverConnection;
    private JTextArea outputArea;
    
    // Constructor
    public VCController(String controllerID, Server serverConnection) {
        this.controllerID    = controllerID;
        this.serverConnection = serverConnection;
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs        = new ArrayList<>();
        this.currentBatch = new ArrayList<>();
        this.jobIDs = new HashSet<>();
    }

    //Methods
    
    //(M4 Implementation: =========== Core FIFO Method ===================================
    public List<Long> calculateCompletionTimes() {
    	
        if (currentBatch == null) currentBatch = new ArrayList<>(); //avoids FIFO time being backed up on each other (not a continued queue)
        else currentBatch.clear(); //reset for every next batch

        for (Job j : activeJobs) { //Add only active jobs that haven’t been calculated yet
            if (!j.isCompletionTimeCalculated()) {
                currentBatch.add(j);
            }
        }

    	
    	List<Long> completionTimes = new ArrayList<>();
    	
    	if (currentBatch.isEmpty()) {
            System.out.println("[VCController] No active jobs to calculate completion times for.");
            return new ArrayList<>();
        }
    	
    	long cumulativeTime = 0;
        
        for (Job j : currentBatch) {
            long durationMinutes = j.getDuration().toMinutes();
            cumulativeTime += durationMinutes;
            j.setCompletionTime(cumulativeTime);
            j.setCompletionTimeCalculated(true); // mark as processed true for completion
            completionTimes.add(cumulativeTime);
 
            System.out.println("[VCController] Job " + j.getJobID()
                    + " | Duration: " + durationMinutes
                    + " min | Completion Time: " + cumulativeTime + " min");
        }
        System.out.println("============================");
        return completionTimes;
    }
    //=========================================================================================
        
    //(Method uses (Job j) and (Jobowner client) that calls from Job and JobOwner Classes)
    
    //Receive active job from Client to send to server
    public void receiveJob(Job j, JobOwner client) {
    	if(j == null || client == null) {
    		 System.out.println("[VCController] Cannot receive a null job or null client.");
    	        return;
    	    }
    	 if (!jobIDs.contains(j.getJobID())) { // prevent duplicate submissions
    		 activeJobs.add(j);	//shows history of active jobs
    		 currentBatch.add(j); //what FIFO uses to track current batch
             jobIDs.add(j.getJobID());
    		 serverConnection.receiveJob(j); //sends job to server
    		 System.out.println("[VCController] Job received from client " + client.getClientID()+ ": "+ j.getJobName());
    	 }else {
    		 System.out.println("[VCController] Job " + j.getJobName() + " already submitted.");
    	}
    }

    
    //Give jobs completed to server 
    public void distributeJob(Job j) {
        if (j == null) {
            System.out.println("[VCController] Cannot distribute a null job.");
            return;
        }

        activeJobs.add(j);
        j.setProgressStatus(Job.JobStatus.IN_PROGRESS);
        System.out.println("[VCController] Job " + j.getJobID() + " added to active queue.");
        assignVehicles(j);
    }
    
    //Recruit vehicles from Vehicle Owner if they are available
    public void recruitVehicle(Vehicle v) {
        if (v == null) {
            System.out.println("[VCController] Cannot recruit a null vehicle.");
            return;
        }

        if (!connectedVehicles.contains(v)) {
            connectedVehicles.add(v);
            v.updateStatus(Vehicle.VehicleStatus.AVAILABLE);
            System.out.println("[VCController] Vehicle " + v.getVehicleID() + " recruited.");
        } else {
            System.out.println("[VCController] Vehicle " + v.getVehicleID()
                    + " is already registered.");
        }
    }
    
    //Assign Vehicle to server
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
            if (v.isAvailable() && v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                selected.add(v);
                v.acceptJob(j);
                v.updateStatus(Vehicle.VehicleStatus.BUSY);
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
    
    //monitor vehicles
    public void monitorVehicles() {
        System.out.println("[VCController] Monitoring " + connectedVehicles.size() + " vehicle(s)...");
        for (Vehicle v : new ArrayList<>(connectedVehicles)) {
            if (v.getStatus() == Vehicle.VehicleStatus.DEPARTING
                    || v.getStatus() == Vehicle.VehicleStatus.OFFLINE) {
                System.out.println("[VCController] Vehicle " + v.getVehicleID()
                        + " is " + v.getStatus() + ". Initiating departure handling.");
                handleVehicleDeparture(v);
            }
        }
    }
    
    
    public void getResults() {
        System.out.println("[VCController] Collecting results for active jobs...");
        for (Job j : new ArrayList<>(activeJobs)) {
            if (j.getProgressStatus() == Job.JobStatus.COMPLETED) {
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

        if (j.getProgressStatus() == Job.JobStatus.FAILED) {
            System.out.println("[VCController] Job " + j.getJobID()
            	+ " failed. Attempting checkpoint restore...");
            
            List<Vehicle> assigned = j.getAssignedVehicles(); // passes to Job to handle internally
            if (!assigned.isEmpty()) {
                String vehicleID = assigned.get(0).getVehicleID();
                Checkpoint cp = new Checkpoint("CHK-" + j.getJobID() + "-" + vehicleID, j.getJobID(), vehicleID, "PENDING");
                j.restoreFromCheckpoint(cp);
            }
         } else {
        	 for (Vehicle v : j.getAssignedVehicles()) {
                 j.createCheckpoint("CHK-" + j.getJobID() + "-" + v.getVehicleID(), v.getVehicleID());            
                 System.out.println("[VCController] Checkpoint created for job " + j.getJobID());
        	 }
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
                && currentJob.getProgressStatus() == Job.JobStatus.IN_PROGRESS) {
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
    
    
    //Attach GUI output
    public void setOutputArea(JTextArea outputArea) {
        this.outputArea = outputArea;
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
    public List<Job> getCurrentBatch() { 
    	return currentBatch; 
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
