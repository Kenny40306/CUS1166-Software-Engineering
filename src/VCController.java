import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	
	//M5 Enums to identify user upon login --------
	public enum UserRole {
        ADMIN,
        USER
    }
	//---------------------------------------------
		
	//M4 Attributes
    private String controllerID;
    private List<Vehicle> connectedVehicles; //declared field:
    private Server serverConnection;
    private JTextArea outputArea;
    //M4 Main Attributes
    private List<Job> activeJobs; ////declared field: for all job submission history called in FIFO method and reciveJob method
    private List<Job> currentBatch; ////declared field: for FIFO calculation method
    private Set<String> jobIDs; ////declared field: prevent duplicate job submissions
    
    //---M5 Attributes for Job and Vehicle> --------------------------------------------------------------------------------------------
    private List<JobRequest> pendingJobRequests; //declared field: pending jobs for admin to view (accept or reject)
    private List<VehicleRequest> pendingVehicleRequests; //declared field: pending vehicles for admin to view (accept or reject)
    private Map<String, String> decisions; //admin's decisions to approve or reject for each client request
    //New Approved File For Both Job and Vehicle:
    private final String APPROVED_FILE = "approved_data.txt";
    //Role Tracking User Context
    private String currentUserID; // Tracks logged-in user either admin of regular user
    private UserRole currentRole;
    private RoleSelectionFrame roleFrame; //user and admin dash board frame reference
    //Notify System
    private Map<String, List<String>> notifications = new HashMap<>(); ////declared field: stores notification in memory per user
    //Server Frame & persistent server data
    private ServerFrame serverFrame;
    private List<String> serverLogs = new ArrayList<>();
    private List<String> jobDisplay = new ArrayList<>();
    private List<String> vehicleDisplay = new ArrayList<>();
    private List<String> userDisplay = new ArrayList<>();
    //------------------------------------------------------------------------------------------------------------------------------------
    
    
    
    //---M6 Attributes for Job and Vehicle> --------------------------------------------------------------------------------------------
    //Kendra Worked On This:
    private SQLDatabaseManager dbManager; //Links Database CLass
   
    // persistent ownership tracking for job and vehicle so admin can notify user of updates
    private Map<String, String> jobOwnerMap = new HashMap<>();
    private Map<String, String> vehicleOwnerMap = new HashMap<>();
    
    //Restores admin changes upon startup base on SQL
    private boolean systemRestoredFromDB = false; //restore in-memory system form database
    //----------------------------------------------------------------------------------------------------------------------------------
    
    
    
    // Constructor
    public VCController(String controllerID, Server serverConnection) {
        this.controllerID    = controllerID;
        this.serverConnection = serverConnection;
                
        this.connectedVehicles = new ArrayList<>();
        this.activeJobs = new ArrayList<>();
        
        this.currentBatch = new ArrayList<>();
        this.jobIDs = new HashSet<>();
        //----------------------------------------------------------
        this.pendingJobRequests = new ArrayList<>();
        this.pendingVehicleRequests = new ArrayList<>();
        this.decisions= new HashMap<>();
        //-----------------------------------------------------------
        
        
        
        //-----M6------------
        this.dbManager = new SQLDatabaseManager(); //connects to database
    }  

       
   
    
    //(M5 Implementation) : =========== Core Job Methods ==================================
       //Kendra Wrote This -
       	
    	//Wrapper static inner class for controller to know which job object is being worked on and which client to notify based on submission
    	//Used internally in VCController to track pending jobs for admin
       	public static class JobRequest { 
       	public Job job;  
       	String client; 
       	String requestID;
       	
       	//(Method: attributes passed to recieveJobRequest(), approveJob() and rejectJob() )
       	
       	public JobRequest(Job job, String client, String requestID){
       		this.job=job;  //holds job object data submitted by client
       		this.client=client; //identify who submitted it by client's name
            this.requestID = requestID; //unique ID for this specific request connects client thread to admin decision
       		} 
       	}
       	
       	//--- Job Request Handling ---
       	//For admin validation upon client submission and synchronized to prevent race conditions when multiple client submits jobs simultaneously
       	public synchronized String receiveJobRequest(Job job, String client) { 
       
       	    if (jobIDs.contains(job.getJobID())) { // Prevent duplicate job submissions (stop same job being submitted twice) 
       	        addNotification(client, "Duplicate job submission rejected: " + job.getJobName()); //immediate feed back to admin 
       	        return null;
       	    }

       	    jobIDs.add(job.getJobID()); // Tracks and marks job as viewed then add job and store unique id
       	    String requestID = client + "_" + System.nanoTime(); //Create unique request id to avoid client over writing each other upon request
       		pendingJobRequests.add(new JobRequest(job, client, requestID)); //Add pending jobs to list so admin can review it later
       		decisions.put(requestID, null); //Track decisions not decided yet so client thread will wait until decision is complete

       		//New M6 Change:
       		jobOwnerMap.put(job.getJobID(), client); //store owner
       		
       		addNotification("ADMIN", "Job request from " + client); //Notify admin for GUI on RoleSelectionFrame
       	    
       	    //UPDATE SERVER FRAME GUI!!!
       	    logServerMessage("New job request from " + client); //For global notifs tab
       	    updateJobDisplay(job.getJobID(),client,job.getJobName(),"PENDING"); //for job tab
       	    
       	    refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database
       	 
       	    return requestID; //Returns request ID used to wait for client's approval
       	}
       	
        //---- Approve / Reject Jobs ---- Called in MainControllerFrame
        public synchronized void approveJob(String requestID) {
            
        	 decisions.put(requestID, "APPROVED");  //stores admin decision hash map that's used for client to wait for response waitForDecision() unblocks          
             notifyAll(); //wakes and notify client threads that are waiting in waitForDecision()!!!
             
        	for(Iterator<JobRequest> it = pendingJobRequests.iterator(); 
        		it.hasNext();) {	//Iterator allows safe removal during iteration while looping through requests
        		
        		JobRequest req = it.next();
               
                if (req.requestID.equals(requestID)) { //Find correct match and push pending jobs to active jobs
                    activeJobs.add(req.job); //active jobs is now used here for FIFO calculations
                  
                    //---M6 Kendra Worked On This:
                    dbManager.insertJob(req.job, req.client); //save to database upon admin approval called from SQL Manager Class
                    //-----------------------------------
                    
                    saveApprovedData("JOB", req.job.getJobID(), req.client, req.job.getJobName()); //Save job to file jobsApproved 
                     
                    // Notifications
                    addNotification(req.client, "Your job \"" + req.job.getJobName() + "\" was APPROVED");  //updates GUI notifications
                  
                    //UPDATE SERVER FRAME GUI!!!
                    logServerMessage("Job " + req.job.getJobID() + " APPROVED");  //For global notifs tab
                    updateJobDisplay(req.job.getJobID(), req.client, req.job.getJobName(), "APPROVED");  //for job tab
					           
                    refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database
                    
                    it.remove(); // remove from pending AFTER notifications to avoid re-processing same request 

                    break;
                }
            }
        }

        public synchronized void rejectJob(String requestID) {
           
        	 decisions.put(requestID, "REJECTED"); //updates decision map and notify the client waitForDecision() unblocks
        	 notifyAll(); //notify client of rejection
        	
        	for (Iterator<JobRequest> it = pendingJobRequests.iterator(); //Iterator allows safe removal during iteration 
        		it.hasNext();) {
               
        		JobRequest req = it.next();
               
        		if (req.requestID.equals(requestID)) { //finds correct match request
                   
                	
                    jobIDs.remove(req.job.getJobID()); // removes jobID and allow client re-submission if needed
                   
                    // Notifications
                    addNotification(req.client, "Your job \"" + req.job.getJobName() + "\" was REJECTED");  //updates GUI notifications
                    
                    //UPDATE SERVER FRAME
                    logServerMessage("Job " + req.job.getJobID() + " REJECTED");
                    updateJobDisplay(req.job.getJobID(), req.client, req.job.getJobName(), "REJECTED");  //for job tab

                    refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database
                    
                    it.remove(); // remove AFTER notifications
                    break;
                }
            }
        }
        
        //===========================================================================================
      
       	
    //(M4 Implementation: =========== Core FIFO Method ===================================
    //FIFO uses active jobs for current batch that's passed to approvedJob() method that's in -- Core Job Methods -- : JobRequest
    
    public List<Long> calculateCompletionTimes() { //ArrayList store job submissions
    	
        if (currentBatch == null) currentBatch = new ArrayList<>(); //avoids FIFO time being backed up on each other (not a continued queue)
        else currentBatch.clear(); //reset for every next batch

        for (Job j : activeJobs) { //Adds only active jobs that haven’t been calculated yet
            if (!j.isCompletionTimeCalculated()) { //get only unprocessed jobs in Job class public boolean isCompletionTimeCalculated()
                currentBatch.add(j); //adds current batch
            }
        }
        
    	List<Long> completionTimes = new ArrayList<>();
    	
    	if (currentBatch.isEmpty()) { //edge case 
            System.out.println("[VCController] No active jobs to calculate completion times for.");
            return completionTimes;
        }
    	
    	//New M6 change here Ryan Worked On This ----------------------------------------------------------------------------	
    	long cumulativeTime = 0; //Tracks total elapsed time
    	
    	int order = 1; //New: FIFO ORDER COUNTER

    	//Loops FIFO order
        for (Job j : currentBatch) { 
            long durationMinutes = j.getDuration().toMinutes();  //called form Job Class and set to minutes
            
            long startTime = cumulativeTime;//New: start time before adding duration
            
            cumulativeTime += durationMinutes; //FIFO Calculation
            j.setCompletionTime(cumulativeTime); // gets in Job Class public void setCompletionTime (long completionTime)
            j.setCompletionTimeCalculated(true); // mark as processed true for completion, gets in Job Class 
           
            
            //!!!New: LOCK JOB AFTER FIFO!!! ------------------------------------------- 
            //Without this, after fifo runs job becomes editable 
            dbManager.lockJobs(j.getJobID()); // DB lock Called from SQLDatabaseManagement Class
            
            String owner = jobOwnerMap.get(j.getJobID());
            //Update job display to LOCKED
            updateJobDisplay(j.getJobID(), owner,j.getJobName(), "APPROVED (LOCKED)");
            
            //notify admin and logs to server frame
            logServerMessage("Job " + j.getJobID() + " LOCKED after FIFO calculation"); 
            completionTimes.add(cumulativeTime);
            
            //New: store fifo in database from SQLDatabase Class
            dbManager.updateJobFIFO(j, order, startTime);
          //------------------------------------------------------------------------------
            
            
            System.out.println("[\nVCController] -> Calculations");
            System.out.println("[VCController] Job " + j.getJobID()
            + " | Order: " + order
            + " | Start: " + startTime
            + " | Duration: " + durationMinutes
            + " | Completion: " + cumulativeTime);

            order++;
        }   
        
        System.out.println("============================");
        return completionTimes;
    }
    
    //========== ADMIN FIFO DATABASE LOCK DECISION =========================
    //Allows Edit Button To check if approved submissions in database is locked or not to edit
    //Ryan Worked on This:
    public boolean canEditJob(String id) { //Method Called in openEditDialog() -- Approved Job -- MainControllerFrame Class
    	
    	//1. Always Check DB (persistent truth) Locked in DB can't edit
        if (dbManager.isJobLocked(id)) {
            return false;
        }
        //2. If exists in DB then it's editable (approved state) unless locked
        return dbManager.getJobById(id) != null;  
     }           
   //----------------------------------------------------------------------------------------------------------------------
    
    //VCController becomes live system state (RAM/Java Objects) where everything happens here first that UI reads After
  
    //updateApprovedJobFromDB() and updateApprovedVehicleFromDB() sync database and in-memory system (hybrid)
    //Changes written to SQL and Java memory VCCOntroller lists + UI stays consistent

    // ================= Admin Fix Job =================
    //Jaden Worked On This 
 
    //Database Fetch upon admin approval by job ID wrapper method returns object so UI can read it
    public Job getJobFromDB(String id) {//Method Called openEditDialog() -- Approved Job -- MainControllerFrame Class
        return dbManager.getJobById(id); //In SQLDatabaseManager
    }

    public void updateApprovedJobFromDB(Job job, String oldName) { //Method Called in openEditDialog() -- Approved Job -- MainControllerFrame Class

        //calls update method from SQL Manager Class updates database / Calls SQL
        if (dbManager.updateJob(job)) {

        	//Notify Server global notif
        	logServerMessage("DB UPDATED JOB: " + job.getJobID());
        	 //Reload latest version job from database
            Job dbJob = dbManager.getJobById(job.getJobID());
        	//Find correct owner from database (user feedback)
            String owner = dbManager.getJobOwner(job.getJobID());
           //update internal memory mapping for owner
            jobOwnerMap.put(job.getJobID(), owner);
            
            //Important: Memory sync for list (active jobs) to match system values with approved jobs in database
            for (int i = 0; i < activeJobs.size(); i++) {
                if (activeJobs.get(i).getJobID().equals(job.getJobID())) {
                    activeJobs.set(i, dbJob); 
                }
            }
            //Update SeverFrame job tab
            updateJobDisplay(job.getJobID(), owner, job.getJobName(), "APPROVED (UPDATED)");

            if (owner != null) {
            	//Notify User Dash board
                addNotification(owner,
                    "Job Submission: " + oldName + " Was Updated To: " + job.getJobName());
            }

            //GLOBAL UI REFRESH
            refreshServerGUI();
        }
    }
    
    
    // ================= Admin Fix Vehicle =================
    //Subat Worked On This
    
    //Database Fetch upon admin approval by Vehicle ID wrapper method returns object so UI can read it
    public Vehicle getVehicleFromDB(String id) {//Method Called in openEditDialog() -- Approved Vehicle -- MainControllerFrame Class
        return dbManager.getVehicleById(id); //In SQLDatabaseManager Class
    }

    public void updateApprovedVehicleFromDB(Vehicle v, String oldName) { //Method Called in openEditDialog() -- Approved Vehicle -- MainControllerFrame Class
        //calls update method from SQL Manager Class updates database / Calls SQL
        if (dbManager.updateVehicle(v)) {

        	//Notify Server global notif
        	logServerMessage("DB UPDATED VEHICLE: " + v.getVehicleID());	
        	//Reload latest version vehicle from database
            Vehicle dbVehicle = dbManager.getVehicleById(v.getVehicleID());
          //Find correct owner from database (user feedback
            String owner = dbManager.getVehicleOwner(v.getVehicleID());
          //update internal memory mapping
            vehicleOwnerMap.put(v.getVehicleID(), owner);
           
          //Important: Memory sync for list (connected vehicles) to match system values with approved vehicles in database
            for (int i = 0; i < connectedVehicles.size(); i++) {
                if (connectedVehicles.get(i).getVehicleID().equals(v.getVehicleID())) {
                    connectedVehicles.set(i, dbVehicle);
                }
            }
            
            //Update SeverFrame vehicle tab
            updateVehicleDisplay(v.getVehicleID(), owner, v.getVehicleName(), "APPROVED (UPDATED)");

            if (owner != null) {
            	//Notify User Dash board
                addNotification(owner,
                		"Vehicle Submission: " + oldName + " Was Updated To: " + v.getVehicleName());
            }
            //GLOBAL UI REFRESH
            refreshServerGUI();
        }
    }
       
       
    // ================= RESTORE UPON PROGRAM RE-OPEN =================
    //Kendra + Jaden + Ryan + Avneet + Subat + Moontarin 
   
    //Allows start up recovery so when admin logs in and edits job submissions for no fifo calculations mainly
    //it re-loads jobs and vehicle, fetch and maps owner, updates server frame and send notification
    public void restoreStateFromDatabase() { //called in public ServerFrame openServerFrame() in VCController Class upon sever frame opening when admin logs in

    	if (systemRestoredFromDB) return; //restore from database once 

        //Filters ONLY load jobs that have NOT gone through FIFO
        activeJobs = dbManager.getApprovedJobsNoFIFO(); //Called in SQL Manager Class
        int restoredJobCount = 0; //counts how many jobs approved no fifo
        
        // rebuild job owner (active jobs in memory) then maps and update UI
        for (Job j : activeJobs) {
            String owner = dbManager.getJobOwner(j.getJobID()); //getJobOwner from SQLDATABASE Class
            jobOwnerMap.put(j.getJobID(), owner); //stores in map
            updateJobDisplay(j.getJobID(),owner,j.getJobName(),"APPROVED"); //updates display
            restoredJobCount++; //count increment
        }

        // rebuild vehicle owner maps and UI
        for (Vehicle v : connectedVehicles) {
            String owner = dbManager.getVehicleOwner(v.getVehicleID()); //getVehicleOwner from SQLDATABASE Class
            vehicleOwnerMap.put(v.getVehicleID(), owner); //stores in map
        }

        //GLOBAL SERVER NOTIFICATION
        //Done only if there are approved jobs in database without fifo
        if (restoredJobCount > 0) {
        	logServerMessage(
                "SYSTEM RESTORE: " + restoredJobCount +
                " job(s) with no FIFO yet are ready for scheduling"
            );
        }

        systemRestoredFromDB = true; //marks completed 
        refreshServerGUI();  	//refresh GUI to server frame
     }

    
    
    
    //Methods
    //(M5 Implementation: =========== Core Vehicle Methods ===================================
    //Subat Wrote This -
    // wrapper for vehicle requests - same idea as JobRequest
    public static class VehicleRequest {
        public Vehicle vehicle;
        String client;
        String requestID;

        public VehicleRequest(Vehicle vehicle, String client,String requestID) {
            this.vehicle = vehicle;
            this.client = client; //display client name
            this.requestID = requestID;
        }
    }

    // called when a vehicle owner sends a vehicle request via socket
    public synchronized String receiveVehicleRequest(Vehicle vehicle, String client) {
       
    	//Create unique request id to avoid client over writing each other upon request
    	String requestID = client + "_" + System.nanoTime();

    	// add to pending list for admin to review
        pendingVehicleRequests.add(new VehicleRequest(vehicle, client,requestID));

        //Track decisions not decided yet
        decisions.put(requestID, null);

        ////New M6 Change: store owner
        vehicleOwnerMap.put(vehicle.getVehicleID(), client);

        // notify admin that a new vehicle request came in
        addNotification("ADMIN", "Vehicle request from " + client);

        // log it to the server frame
        logServerMessage("New vehicle request from " + client);

        // show it in the vehicle tab of server frame as pending
        updateVehicleDisplay(vehicle.getVehicleID(), client, vehicle.getVehicleName(), "PENDING");
        
        //Update GUI
        refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database

        //Returns request ID
        return requestID;
    }


    // admin approved the vehicle - add it to the system and save to file
    public synchronized void approveVehicle(String requestID) {
        
    	// put the decision in the map so waitForDecision() unblocks
        decisions.put(requestID, "APPROVED");
        notifyAll();

    	for (Iterator<VehicleRequest> it = pendingVehicleRequests.iterator();
    		it.hasNext();) {
          
    		VehicleRequest req = it.next();
           
    		if (req.requestID.equals(requestID)) {
            	
            	 // add vehicle to connected vehicles list
                connectedVehicles.add(req.vehicle);

                
                //---M6 Kendra Worked On This:
                dbManager.insertVehicle(req.vehicle, req.client);  //save to database upon admin aproval called from SQL Manager Class
                //-------------------------------------------
                
                
                // save to file only on approval (requirement)
                saveApprovedData("VEHICLE",req.vehicle.getVehicleID(), req.client, req.vehicle.getVehicleName());
                                
                addNotification(req.client, "Your vehicle \"" + req.vehicle.getVehicleID() + "\" was APPROVED");
                
                logServerMessage("Vehicle " + req.vehicle.getVehicleID() + " APPROVED");
                updateVehicleDisplay(req.vehicle.getVehicleID(), req.client, req.vehicle.getVehicleName(), "APPROVED");

                refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database
                
                it.remove(); // remove AFTER notifications
                break;
            }
        }
    }

    // admin rejected the vehicle, do NOT save to file
    public synchronized void rejectVehicle(String requestID) {
       
    	 // put the decision in the map so waitForDecision() unblocks
        decisions.put(requestID, "REJECTED");
        notifyAll();

    	for(Iterator<VehicleRequest> it = pendingVehicleRequests.iterator();
          it.hasNext();) {
            
        	VehicleRequest req = it.next();
          
        	if (req.requestID.equals(requestID)) {
                 
                // notify the vehicle owner
                addNotification(req.client, "Your vehicle \"" + req.vehicle.getVehicleID() + "\" was REJECTED");
              
                // update server frame
                logServerMessage("Vehicle " + req.vehicle.getVehicleID() + " REJECTED");
                updateVehicleDisplay(req.vehicle.getVehicleID(), req.client, req.vehicle.getVehicleName(), "REJECTED");

                refreshServerGUI(); //Moontarin added this - updates all GUI in server when admin changes database
                
                it.remove(); // remove AFTER notifications
                break;
            }
        }
    }
    //---------------------------------------------------------------------------------------------------------------------
   
    //=====================================================================================================================
    //----For Both JobRequest & VehicleRequest Classes----
    
    // SINGLE SAVE METHOD
    private void saveApprovedData(String type, String id, String owner, String name) {
        try (FileWriter fw = new FileWriter(APPROVED_FILE, true)) {

            fw.write("Timestamp: " + LocalDateTime.now() + "\n");
            fw.write("Type: " + type + "\n");
            fw.write("ID: " + id + "\n");
            fw.write("Owner: " + owner + "\n");
            fw.write("Name: " + name + "\n");
            fw.write("---------------------------------\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Used by client Socket to wait until admin approves or reject job
   	public synchronized String waitForDecision(String requestID) {
   	    while (!decisions.containsKey(requestID) || decisions.get(requestID) == null) { //ensure thread waits until decision exists
   	        try {
   	            wait(); //wait until notifyAll is called *note used for thread sync (called by clientHandler)*
   	        } catch (InterruptedException e) {
   	            e.printStackTrace();
   	        }
   	    }
   	    return decisions.remove(requestID); //return decision once admin decides
   	}
    //=======================================================================================================================
    

   
   	
    //Jaden Wrote This-
    //-----------------Set Current User Context-----------------------------
   
    //standardize user ID so it’s not consistent anywhere and can convert lowercase to uppercase that prevents mismatches. 
    //Ensure notifications are stored and retrieved correctly (avoid notification loss due to inconsistent IDs).
   	private String normalize(String userId) {
   	   
   		if (userId == null) return "";

   	    userId = userId.trim();

   	    if (userId.startsWith("Client ")) {
   	        userId = userId.replace("Client ", "");
   	    }

   	    return userId.toLowerCase(); // IMPORTANT FOR: consistent keying 	
   	}

   	public void setCurrentUserId(String userId, UserRole role) {
      
        //Prevent fake admin login
        if (role == UserRole.USER && userId.equals("admin")) {
            throw new IllegalArgumentException("Username 'admin' is reserved.");
        }
        
        this.currentUserID = normalize(userId);
        this.currentRole = role;

        // Open server frame if admin   
        if (roleFrame != null) {
            roleFrame.refreshNotifications();
        }
        //Update Server
        updateUserDisplay(userId, role.toString(), "ONLINE");
        System.out.println("LOGIN -> " + userId + " (" + role + ")"); // DEBUG (keep this while testing) 
    }
    
    public String getCurrentUserID() {
        return currentUserID;
    }

    //Set frame for notifications on RoleselectionFrame 
    public void setRoleFrame(RoleSelectionFrame roleFrame) {
        this.roleFrame = roleFrame;
    }
    
    //--------------------------------------------------------------------------------------------------------------
    
    
    //Jaden Wrote This-
 //============= M5: Notifications for Admin and User ==============================================================
    //Still need notifications for message history, GUI updates, offline message tracking and dash board visibility
    //Socket is short lived where connection opens, sends message, then closes
    
    //adds notification message for specific user in that calls normalized(userId) and stores in memory (Map<String,List<String>>)
    //to keep users informed and up to date (file ensures notification persist after program closes) 
    
    public void addNotification(String userId, String message) {

        String normalizedUser = normalize(userId);
        notifications.computeIfAbsent(normalizedUser, k -> new ArrayList<>()).add(message);

        if (roleFrame == null || currentUserID == null) return;

        SwingUtilities.invokeLater(() -> {
            String current = normalize(currentUserID);
            boolean isAdminMsg = currentRole == UserRole.ADMIN && normalizedUser.equals("admin");
            boolean isUserMsg = currentRole == UserRole.USER && normalizedUser.equals(current);

            if (isAdminMsg || isUserMsg) {
                roleFrame.refreshNotifications();
            }
        });
    }
    
    //Removes all notifications for specific user and keep the notification storage manageable 
    public void clearNotifications(String userId) {
    	userId = normalize(userId);
        notifications.remove(userId); //explicit clear
    }
        
    //refresh notification from file
    //Updates in memory notification with the latest from the file and reload file to reflect any changes made externally by other processes, calls loadnotification 
    //Merge/replace current map to ensure memory state is consistent with persisted data
    public void refreshNotificationsFromFile() {
        if (roleFrame != null) {
            roleFrame.refreshNotifications();
        }
    }
    
    //Returns the list of notifications for a given user by looking up the user ID in-memory map and providing a dashboard with current notifications.
    public List<String> getNotifications(String userId) {
    	userId = normalize(userId);
        return notifications.getOrDefault(userId, new ArrayList<>());
    }
    
    //Avneet + Moon Worked On This-
    //----------------- Server Management-----------------------------
    // Store ServerFrame
    public ServerFrame openServerFrame (JFrame referenceFrame) {
    	if (serverFrame == null || !serverFrame.isDisplayable()) {
            serverFrame = new ServerFrame(referenceFrame);
            
            //RESTORE DATA WHEN REOPENED
            serverFrame.updateLogs(serverLogs);
            serverFrame.updateJobs(jobDisplay);
            serverFrame.updateVehicles(vehicleDisplay);     
            serverFrame.updateUsers(userDisplay); 
            // Immediately populate SYSTEM tab
            updateSystemTab();
            
            
            //New: Refresh UI upon admin update edit when program reopens
            restoreStateFromDatabase();
            //--------------------------------------------------------------------------
            
            
    
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
        
        String formatted =
                "==============================\n" +
                "Time: " + timestamp + "\n" +
                "Event: " + message + "\n" +
                "==============================\n";
        
        // Add to internal log
        serverLogs.add(formatted);
        
        if (serverFrame != null) {
            serverFrame.updateLogs(serverLogs); // append instead of reset
        }
        // Print to console as well
        System.out.println(formatted);

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

    public void updateVehicleDisplay(String vehicleId, String owner, String type, String status) {
        String entry = vehicleId + " | " + owner + " | " + type + " | " + status;

        vehicleDisplay.removeIf(v -> v.startsWith(vehicleId + " "));
        vehicleDisplay.add(entry);

        if (serverFrame != null) {
            serverFrame.updateVehicles(vehicleDisplay);
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

    //-------New M6 Moontarin Worked on This------------------------------------
    public void refreshServerGUI() {
        if (serverFrame != null) {
            serverFrame.updateJobs(jobDisplay);
            serverFrame.updateVehicles(vehicleDisplay);
        }
    }
  //---------------------------------------------------------------------
    
    
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
    public List<Job> getCurrentBatch() { 
    	return currentBatch; 
    }

    public Server getServerConnection() {
        return serverConnection;
    }
 
    public void setServerConnection(Server serverConnection) {
        this.serverConnection = serverConnection;
    }
    
    
    //---------- Pending Request uses these getter Memory for admin edit ---------------
    public List<Vehicle> getConnectedVehicles() {
        return connectedVehicles;
    }
    public List<Job> getActiveJobs() {
        return activeJobs;
    }
    //M5 getter so MainControllerFrame can check pending job requests
    public synchronized List<JobRequest> getPendingJobRequests() {
        return pendingJobRequests;
    }
    //M5 getter so MainControllerFrame can check pending vehicle requests
    public synchronized List<VehicleRequest> getPendingVehicleRequests() {
        return pendingVehicleRequests;
    }
    //-----------------------------------------------------------------------------------------

 
    @Override
    public String toString() {
        return "VCController{"
                + "controllerID='" + controllerID + '\''
                + ", connectedVehicles=" + connectedVehicles.size()
                + ", activeJobs=" + activeJobs.size()
                + '}';
    }
}    