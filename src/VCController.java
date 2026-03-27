import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/*=====================
Class VC Controller Logic (aka the Brain)- Ryan
======================*/

//----M4 Implementation: 
public class VCController {
	
	
	 // Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles;
    private Server serverConnection;
    private JTextArea outputArea;
    //M4
    private List<Job> activeJobs; //for all job submission history called in FIFO method and reciveJob method
    private List<Job> currentBatch; //for FIFO calculation method
    private Set<String> jobIDs; //prevent duplicate job submissions
   
    //Notify System
    private String currentUserId; // Track logged-in user
    private RoleSelectionFrame roleFrame; //user dash board frame reference
    private Map<String, List<String>> notifications = new HashMap<>(); //stores notification in memory per user
    private final String NOTIF_FILE = "notifications.txt"; // persistent storage for user
    
    //---M5 maybe queue here and threadPool attributes
    private List<JobRequest> pendingJobRequests; //pending jobs for admin
    //need one for VehicleRequest
    
    // Constructor
    public VCController(String controllerID, Server serverConnection) {
        this.controllerID    = controllerID;
        this.serverConnection = serverConnection;
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs = new ArrayList<>();
        this.currentBatch = new ArrayList<>();
        this.jobIDs = new HashSet<>();
        
        this.pendingJobRequests = new ArrayList<>();

        loadNotificationsFromFile();   // Load persisted notifications

    }

   //Methods
    
  //M5: Notifications for Admin and User ==============================================================
    
    //note: Admin is able to see jobs in notification when logging in and out 
    //Users don't see approve or reject in notification!!! Needs a way to fix this
    
    //--------------Normalize IDs-----------------
    private String normalize(String id) {
        return id == null ? null : id.trim().toLowerCase();
    }

    //adds notification inside VC COntroller
    public void addNotification(String userId, String message) {
    	userId = normalize(userId);
    	notifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
        
        // Append to file for persistent notification
        try (FileWriter fw = new FileWriter(NOTIF_FILE, true)) {
            fw.write(userId + "|" + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //update if same user is active for real time admin update only        
        if (roleFrame != null && currentUserId != null) {
            // If current user is admin and notification is for admin, show it
            if (currentUserId.equals("admin") && userId.equals("admin")) {
                roleFrame.appendNotification(message);
            } 
            // If current user is a regular user and notification is for them, show it
            else if (!currentUserId.equals("admin") && userId.equals(currentUserId)) {
                roleFrame.appendNotification(message);
            }
            // Otherwise, do not show notification in this frame
        }
        
        //Optional: Admin can see all notifications for both user and other admins
        /*if (roleFrame != null && currentUserId != null) {
            if (userId.equals(currentUserId) || currentUserId.equals("admin")) {
                roleFrame.appendNotification(message);
            }
        }*/ 
    }
    
    //loads notifications from file writer
    public void loadNotificationsFromFile() {
        File file = new File(NOTIF_FILE);
        if (!file.exists()) return;

        notifications.clear(); // prevent duplicates

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                	String user = normalize(parts[0]); 
                    String msg = parts[1];
                	notifications
                        .computeIfAbsent(user, k -> new ArrayList<>())
                        .add(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void clearNotifications(String userId) {
    	userId = normalize(userId);
        notifications.remove(userId); //explicit clear
        
     // Rewrite file WITHOUT this user's notifications
        File inputFile = new File(NOTIF_FILE);
        File tempFile = new File("temp_notifications.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.toLowerCase().startsWith(userId + "|")) {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace original file
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
    
  //refresh
    public void refreshNotificationsFromFile() {
        loadNotificationsFromFile();
        if (roleFrame != null) {
            roleFrame.refreshNotifications();
        }
    }
    
    //This method remove clearing inside of refresh and provide explicit clear method
    public List<String> getNotifications(String userId) {
    	userId = normalize(userId);
    	loadNotificationsFromFile();
        return notifications.getOrDefault(userId, new ArrayList<>());
    }
    
    //-----------------Set Current User-----------------------------
    public void setCurrentUserId(String userId) {
        this.currentUserId = normalize(userId);
        // Refresh notifications for this user if role frame exist

        if (roleFrame != null) {
            roleFrame.refreshNotifications();
        }
        // DEBUG (keep this while testing)
        System.out.println("\nLOGIN USER -> " + this.currentUserId);
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    //Set frame for notifications on RoleselectionFrame 
    public void setRoleFrame(RoleSelectionFrame roleFrame) {
        this.roleFrame = roleFrame;
    }

//========================================================================================
    
      
    
    
    
 //(M5 Implementation: =========== Core Job Methods ==================================
    
    // Request wrappers static inner class
    public static class JobRequest { 
    	public Job job;  
    	public ClientInterface client; 
    	
    	//(Method: passed to 
    	public JobRequest(Job job, ClientInterface client){
    		this.job=job; 
    		this.client=client;} 
    	}

    	// Receive job request from Client (Method: passed to JobOwner Class (submitJobToController() and sendDataToServer() ) ) 
    	public synchronized void receiveJobRequest(Job j, ClientInterface client) {
    		System.out.println("[VCController] Received job request: " + j.getJobName());
    		client.notify("Request received for job: " + j.getJobName());
    		pendingJobRequests.add(new JobRequest(j, client));
    		
    		//Admin view update only
            addNotification("ADMIN","New Job Submitted By Client " + j.getClientID() + ": " + j.getJobName());
    	}
    
    	// Approve jobs moves it to active jobs (Method: passed to MainControllerFrame (acceptButton.addActionListener) )
    	public synchronized void approveJob(Job j) {
    		JobRequest req = pendingJobRequests.stream().filter(r -> r.job.equals(j)).findFirst().orElse(null);
    		if (req != null) {
    			pendingJobRequests.remove(req);
    			activeJobs.add(req.job); //active job gets passed here
    			jobIDs.add(req.job.getJobID()); 
    			serverConnection.receiveJob(req.job);
    			req.client.notify("Job approved: " + req.job.getJobName());
    			System.out.println("[VCController] Job approved: " + req.job.getJobName());
    			
    			// Notify Client Job Accepted
    			addNotification(j.getClientID(),"Your job \"" + j.getJobName() + "\" was APPROVED");
    		}
    	}
    
    	//reject jobs (Method passed to (Method: passed to MainControllerFrame (rejectButton.addActionListener) )
    	public synchronized void rejectJob(Job j) {
    		JobRequest req = pendingJobRequests.stream().filter(r -> r.job.equals(j)).findFirst().orElse(null);
    		if (req != null) {
    			pendingJobRequests.remove(req);
    			req.client.notify("Job rejected: " + req.job.getJobName());
    			System.out.println("[VCController] Job rejected: " + req.job.getJobName());
    			
    			// Notify Client Job Rejected
    	        addNotification(j.getClientID(),"Your job \"" + j.getJobName() + "\" was REJECTED");
    		}
    	}
    //===========================================================================================
    
    
    //(M5 Implementation: =========== Core Vehicle Methods ===================================
    	//...
    	//...
    //===================================================================================
    
    
    //(M4 Implementation: =========== Core FIFO Method ===================================
    //FIFO uses active jobs, and jobIDs that's passed to approved Job method that's called in MainControllerFrame
    //ArrayList store job submissions
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
        
    	//Loops FIFO order
        for (Job j : currentBatch) { 
            long durationMinutes = j.getDuration().toMinutes();
            cumulativeTime += durationMinutes;
            j.setCompletionTime(cumulativeTime);
            j.setCompletionTimeCalculated(true); // mark as processed true for completion
            completionTimes.add(cumulativeTime);
 
            System.out.println("[\nVCController] -> Calculations");
            System.out.println("[VCController] Job " + j.getJobID()
                    + " | Duration: " + durationMinutes
                    + " min | Completion Time: " + cumulativeTime + " min");
        }
        System.out.println("============================");
        return completionTimes;
    }
    //=======================================================================================================
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Might Not Need This (Only Here For Reference Now)
     
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
    
    public synchronized List<JobRequest> getPendingJobRequests() {
        return pendingJobRequests;
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
