import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


/*=====================
Class VC Controller Logic (aka the Brain)- Ryan
======================*/

//----M4 Implementation: 
public class VCController {
	
	//Enums to identify user upon login 
	public enum UserRole {
        ADMIN,
        USER
    }
	
	// Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles; //declared field:
    //M4 Main Attributes
    private List<Job> activeJobs; ////declared field: for all job submission history called in FIFO method and reciveJob method
    private List<Job> currentBatch; ////declared field: for FIFO calculation method
    private Set<String> jobIDs; ////declared field: prevent duplicate job submissions
    
    
    
    //---M5 Attributes for Job and Vehicle> --------------------------------------------------------------------------------------------
    private List<JobRequest> pendingJobRequests;//declared field: pending jobs for admin to view (accept or reject)
    private List<VehicleRequest> pendingVehicleRequests;
    private Map<String, String> decisions = new HashMap<>(); //admin's decisions to approve or reject for each client request
    //need one for vehicle
    
    private Server serverConnection;
   
    
    //Role Tracking
    private String currentUserId; // Tracks logged-in user either admin of regular user
    private UserRole currentRole;
    private RoleSelectionFrame roleFrame; //user dash board frame reference
    
    //Notify System
    private Map<String, List<String>> notifications = new HashMap<>(); ////declared field: stores notification in memory per user
    private final String NOTIF_FILE = "notifications.txt"; // persistent file storage for user
  
    //Server Frame & persistent server data
    private ServerFrame serverFrame;
    private JTextArea outputArea;
    private List<String> serverLogs = new ArrayList<>();
    private List<String> jobDisplay = new ArrayList<>();
    private List<String> userDisplay = new ArrayList<>();
    //------------------------------------------------------------------------------------------------------------------------------------
    
    
    
    // Constructor
    public VCController(String controllerID) {
    	
    	System.out.println("VCController instance created");
        
    	this.controllerID    = controllerID;
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs = new ArrayList<>();
        this.currentBatch = new ArrayList<>();
        this.jobIDs = new HashSet<>();
        
        //----------------------------------------------------------
        this.pendingJobRequests = new ArrayList<>();
        this.pendingVehicleRequests = new ArrayList<>();
        
        
        resetNotificationFile(); //clear old notification files
        loadNotificationsFromFile();   // Load persisted notifications is now empty at program startup
        //-----------------------------------------------------------
    }
       
   
    
    //(M5 Implementation) : =========== Core Job Methods ==================================
       //Kendra Wrote This -
       // wrapper static inner class for controller to know which job is being worked on and which client to notify
       	public static class JobRequest { 
       	public Job job;  
       	String client; 
       	
       	//(Method: attributes passed to approveJob() and rejectJob()
       	public JobRequest(Job job, String client){
       		this.job=job; 
       		this.client=client;} 
       	}
       	
       	public static class VehicleRequest {
       		public Vehicle vehicle;
       		public String client;
       		
       		public VehicleRequest(Vehicle vehicle, String client) {
       			this.vehicle = vehicle;
       			this.client = client;
       		}
       	}
       	
       	//--- Job Request Handling ---
       	public synchronized void receiveJobRequest(Job job, String client) { //input validation
       
       		// Prevent duplicate job submissions
       	    if (jobIDs.contains(job.getJobID())) {
       	        addNotification(client, "Duplicate job submission rejected: " + job.getJobName());
       	        return;
       	    }
       	    System.out.println("Conttroller received job from " + client);

       	    jobIDs.add(job.getJobID()); // Track unique job
       		pendingJobRequests.add(new JobRequest(job, client));
       	    addNotification("ADMIN", "Job request from " + client);
       	    
       	    //UPDATE SERVER FRAME
       	    logServerMessage("New job request from " + client);
       	    updateJobDisplay(job.getJobID(),client,job.getJobName(),"PENDING");
       	}
       	
       	public synchronized void approveJob(Job job) {
            for (JobRequest req : pendingJobRequests) {
                if (req.job.equals(job)) {
                    activeJobs.add(job);
                    saveJobToFile(job);
                    
                    job.setProgressStatus(Job.JobStatus.IN_PROGRESS);
                    
                    decisions.put(req.client, "APPROVED");
                    notifyAll();
                    addNotification(req.client, "Your job \"" + job.getJobName() + "\" was APPROVED");
                    
                    //UPDATE SERVER FRAME
                    logServerMessage("Job " + job.getJobID() + " APPROVED");
                    updateJobDisplay(job.getJobID(),req.client,job.getJobName(),"APPROVED");
                   
                    
                 // Assign vehicles and start processing
                    assignVehicles(job);

                    if (job.getAssignedVehicles() != null) {
                        for (Vehicle v : job.getAssignedVehicles()) {
                            logServerMessage("Starting vehicle thread: " + v.getVehicleID());
                            v.processJob();
                        }
                    }
                    
                    updateSystemTab();
                    break;
                }
            }
        }
       	
       	public synchronized void rejectJob(Job job) {
            for (JobRequest req : pendingJobRequests) {
                if (req.job.equals(job)) {
                    decisions.put(req.client, "REJECTED");
                    
                    jobIDs.remove(job.getJobID()); // allow resubmission
                    
                    notifyAll();
                    addNotification(req.client, "Your job \"" + job.getJobName() + "\" was REJECTED");
                    
                    //UPDATE SERVER FRAME
                    logServerMessage("Job " + job.getJobID() + " REJECTED");
                    updateJobDisplay(job.getJobID(),req.client,job.getJobName(),"REJECTED");                    
                    updateSystemTab();
                    break;
                }
            }
        }       	
       	
       	public synchronized String waitForDecision(String client) {
       	    while (!decisions.containsKey(client)) {
       	        try {
       	            wait();
       	        } catch (InterruptedException e) {
       	            e.printStackTrace();
       	        }
       	    }
       	    return decisions.remove(client);
       	}
       	
       	//Save file only on Approval
       	private void saveJobToFile(Job job) {
       	    try (FileWriter fw = new FileWriter("jobsApproved.txt", true)) {
       	        fw.write(job.toString() + "\n");
       	    } catch (IOException e) {
       	        e.printStackTrace();
       	    }
       	}
   
       //===========================================================================================
       

       	
    //(M4 Implementation: =========== Core FIFO Method ===================================
    //FIFO uses active jobs for current batch that's passed to approvedJob() method that's in -- Core Job Methods -- : JobRequest
    
    public List<Long> calculateCompletionTimes() { //ArrayList store job submissions
    	
        if (currentBatch == null) currentBatch = new ArrayList<>(); //avoids FIFO time being backed up on each other (not a continued queue)
        else currentBatch.clear(); //reset for every next batch

        for (Job j : activeJobs) { //Adds only active jobs that haven’t been calculated yet
            if (!j.isCompletionTimeCalculated()) { //gets in Job class public boolean isCompletionTimeCalculated()
                currentBatch.add(j); //adds current batch
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
            long durationMinutes = j.getDuration().toMinutes();  //called form Job Class and set to minutes
            cumulativeTime += durationMinutes;
            j.setCompletionTime(cumulativeTime); // gets in Job Class public void setCompletionTime (long completionTime)
            j.setCompletionTimeCalculated(true); // mark as processed true for completion, gets in Job Class 
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
    
    
    
    
  //Methods
    //(M5 Implementation: =========== Core Vehicle Methods ===================================
 // Receive vehicle request from socket (ClientHandler)
 //avneet
    
    public synchronized void receiveVehicleRequest(Vehicle v, String client) {
        logServerMessage("Vehicle request received: " + v.getVehicleID());

        pendingVehicleRequests.add (new VehicleRequest(v, client));

        if (!connectedVehicles.contains(v)) {
            connectedVehicles.add(v);
            v.updateStatus(Vehicle.VehicleStatus.AVAILABLE);
            
            System.out.println("Vehicle added to system: " + v.getVehicleID());
            System.out.println("Total vehicles: " + connectedVehicles.size());
            logServerMessage("Vehicle registered: " + v.getVehicleID());
            
            updateSystemTab();
        } else {
            logServerMessage("Vehicle already registered: " + v.getVehicleID());
        
        }
    }
    
    //private List<VehicleRequest> pendingVehicleRequests; ////declared field: pending vehicles for admin to view (accept or reject)
    //this.pendingVehicleRequests = new ArrayList<>();

    	// Vehicle request wrapper
    	/*private static class VehicleRequest {
            Vehicle vehicle;
            public ClientInterface client; 
            
            public VehicleRequest(Vehicle vehicle, ClientInterface client) { 
            	this.vehicle = vehicle; 
            	this.client = client; 
            	}
            
            // Client sends vehicle
            public void receiveVehicleRequest(Vehicle v, ClientInterface client) {
            	
            }
            // Approve vehicle
            public void approveVehicle(Vehicle v) {
            	
            }
            // Reject vehicle
            public void rejectVehicle(Vehicle v) {
        }*/
    //---------------------------------------------------------------------------------------------------------------------
   
    
    
    
    
    //-----------------Set Current User Context-----------------------------
    public void setCurrentUserId(String userId, UserRole role) {
        this.currentUserId = normalize(userId);
        // Refresh notifications for this user if role frame exist

        //Prevent fake admin login
        if (role == UserRole.USER && userId.equals("admin")) {
            throw new IllegalArgumentException("Username 'admin' is reserved.");
        }

        this.currentUserId = userId;
        this.currentRole = role;
      
        // Open server frame if admin   
        if (roleFrame != null) {
            roleFrame.refreshNotifications();
        }
        //Update Server
        updateUserDisplay(userId, role.toString(), "ONLINE");
        System.out.println("LOGIN -> " + userId + " (" + role + ")"); // DEBUG (keep this while testing) 
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    //Set frame for notifications on RoleselectionFrame 
    public void setRoleFrame(RoleSelectionFrame roleFrame) {
        this.roleFrame = roleFrame;
    }
    
    //--------------------------------------------------------------------------------------------------------------
    
    
    
 //============= M5: Notifications for Admin and User ==============================================================
    //Still need notifications for message history, GUI updates, offline message tracking and dash board visibility
    //Socket is short lived where connection opens, sends message, then closes
    
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
        	if (currentRole == UserRole.ADMIN && userId.equals("admin")) {
                roleFrame.appendNotification(message);
            } 
            // If current user is a regular user and notification is for them, show it
        	else if (currentRole == UserRole.USER && userId.equals(currentUserId)) {
                roleFrame.appendNotification(message);
            }
            // Otherwise, do not show notifications in notifications box
        }
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
    
    //refresh notification from file
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
    
    private void resetNotificationFile() {
        try (FileWriter fw = new FileWriter(NOTIF_FILE, false)) {
            // overwrite file (clear contents)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-----------------------------------------------------------------------------------------------------------------
   

    
    
    //----------------- Server Management-----------------------------
    // Store ServerFrame
    public ServerFrame openServerFrame (JFrame referenceFrame) {
    	if (serverFrame == null || !serverFrame.isDisplayable()) {
            serverFrame = new ServerFrame(referenceFrame);
            
            //RESTORE DATA WHEN REOPENED
            serverFrame.updateLogs(serverLogs);
            serverFrame.updateJobs(jobDisplay);
            serverFrame.updateUsers(userDisplay);
            
         // Immediately populate SYSTEM tab
            updateSystemTab();
        } else {
            // bring existing window to front
            serverFrame.toFront();
            serverFrame.requestFocus();
        }
        return serverFrame;
    }

    // Close ServerFrame on logout
    public void closeServerFrame() {
        if (serverFrame != null) {
            serverFrame.dispose(); //data isn't cleared
            serverFrame = null;
        }
    }
    
    public void logServerMessage(String message) {
        String timestamp = "[" + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ "] ";
        
        String fullMessage = timestamp + message;
        // Add to internal log
        serverLogs.add(fullMessage);
        
        if (serverFrame != null) {
            serverFrame.updateLogs(serverLogs); // append instead of reset
        }
        //update outputArea 
        if(outputArea != null) {
        	outputArea.append(fullMessage + "\n");
        }
        // Print to console as well
        System.out.println(fullMessage);

    }

    // --- Append log in ServerFrame ---
    public void appendServerLog(String log) {
        if (serverFrame != null) {
            serverFrame.appendLog(log);
        }
    }

    public void updateJobDisplay(String jobId, String owner, String type, String status) {
        String entry = jobId + " | " + owner + " | " + type + " | " + status;

        jobDisplay.removeIf(j -> j.startsWith(jobId + " "));
        jobDisplay.add(entry);

        if (serverFrame != null) {
            serverFrame.updateJobs(jobDisplay);
        }
    }

    
    public void updateUserDisplay(String userId, String role, String status) {
        String entry = userId + " | " + role + " | " + status;

        userDisplay.removeIf(u -> u.startsWith(userId + " "));
        userDisplay.add(entry);

        if (serverFrame != null) {
            serverFrame.updateUsers(userDisplay);
        }
    }

    
    
  //--------------------- Data For Server System Panel -----------------------
    
    public void updateSystemTab() {
        if (serverFrame == null) return;

        // Server info
        List<String> systemData = new ArrayList<>();

        // Server info
        if (serverConnection != null) {
            systemData.addAll(serverConnection.getServerStatusData());
        } else {
            systemData.add("No server connected");
        }
        
        // --- Vehicles ---
        systemData.add("----- Vehicles -----");
        List<Vehicle> vehicles = getConnectedVehicles();
        if (vehicles == null || vehicles.isEmpty()) {
            systemData.add("No vehicles registered.");
        } else {
            for (Vehicle v : vehicles) {
                systemData.add(v.getVehicleStatusInfo());
            }
            long available = vehicles.stream().filter(Vehicle::isAvailable).count();
            systemData.add("Available: " + available + " / " + vehicles.size() + " vehicles");
        }
        systemData.add("-----------------------------");

        // --- Checkpoints ---
        systemData.add("----- Checkpoints -----");
        List<Job> batch = getCurrentBatch(); // ensure we get the active jobs
        if (batch != null && !batch.isEmpty()) {
            for (Job j : batch) {
                systemData.addAll(j.getCheckpointInfo());
            }
        } else {
            systemData.add("No active jobs.");
        }
        systemData.add("-----------------------------");
   
        //return systemData;
        serverFrame.updateSystem(systemData);
    }
 //=================================================================================================================
 //=================================================================================================================
    
    
    
    
  //THESE ARE REFRENCE METHODS PAST THIS POINT!!!------------------------
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
    	int vehicleCount = (connectedVehicles == null) ? 0 : connectedVehicles.size();
    	int jobCount = (activeJobs == null) ? 0 : activeJobs.size();;
        return "VCController{" +
    			"vehicles=" + vehicleCount +
    			", jobs=" + jobCount +
    			'}';
                
    }
}
