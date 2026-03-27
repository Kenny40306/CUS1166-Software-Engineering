import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat
======================*/

//---- M4 Implementation: main frame for VCController to show output on dashboard ----

//Main Frame (GUI window for displaying system output)
public class MainControllerFrame extends JFrame{
  
	  
    private VCController vcController;  // Reference to VCController (connects GUI to backend logic)
    private JTextArea outputArea;	// Text area used to display backend output/logs
    private JPanel topPanel;	
    
    //initializes the frame and connects it to controller
    public MainControllerFrame(VCController vcController) {
    	// Store controller reference
    	this.vcController = vcController;
    	
        setTitle("VC Dashboard"); // Set window title
        setSize(300, 400); // Set window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window when user exits
        setLayout(new BorderLayout(10,10));  // Use BorderLayout for organizing components
        
     // Title
        JLabel title = UIStyling.createDashboardTitle("VC Controller Dashboard");
        
        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");
        JButton calcButton = new JButton("Calculate");

        UIStyling.styleDashboardButton(acceptButton); /* CHANGED */
        UIStyling.styleDashboardButton(rejectButton); /* CHANGED */
        UIStyling.styleDashboardButton(calcButton);   /* CHANGED */

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(calcButton);
        UIStyling.styleDashboardPanel(buttonPanel);

        // Combine into header
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        UIStyling.styleDashboardPanel(topPanel);
        
        add(topPanel, BorderLayout.NORTH);

        //Text Area For Output
        outputArea = new JTextArea(15,50); // Create text area for output display
        outputArea.setEditable(false); // Make text area read-only (user cannot edit)
        JScrollPane scrollPane = new JScrollPane(outputArea);
        UIStyling.styleScrollPaneDark(scrollPane);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        UIStyling.styleDashboardPanel(centerWrapper);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        
        add(centerWrapper, BorderLayout.CENTER); // Add scroll pane to center of frame
        
       
      //====================================================================================
        //!!!M5 Implementation: Buttons for Accept and Reject!!!
        //==================================================================================
       acceptButton.addActionListener(e -> {
            if (!vcController.getPendingJobRequests().isEmpty()) {
                VCController.JobRequest req = vcController.getPendingJobRequests().get(0);
                vcController.approveJob(req.job); //method passed here
                outputArea.append("[VCController] Approved job: " + req.job.getJobName() + "\n");
            } else {
                outputArea.append("[VCController] No pending jobs to approve.\n");
            }
        });

        rejectButton.addActionListener(e -> {
            if (!vcController.getPendingJobRequests().isEmpty()) {
                VCController.JobRequest req = vcController.getPendingJobRequests().get(0);
                vcController.rejectJob(req.job); //method passed here
                outputArea.append("[VCController] Rejected job: " + req.job.getJobName() + "\n");
            } else {
                outputArea.append("[VCController] No pending jobs to reject.\n");
            }
        });

        //==========================================================================================
        
   
        //Calculate button action
        calcButton.addActionListener(e -> {
            clearOutput();               // Clear previous logs
            displayCurrentJobs();        // Display active jobs
            displayCompletionTimes();    // Display FIFO completion times
            displayQueue();              // Display job queue
            displayServerStatus();		// Display server status
            displayVehicleMonitor();
            displayCheckpointActivity();
            
        });
        
        applyDecorations(); // keep method, but modified
        setVisible(true); // Make frame visible
    }
    
    //============================
    // METHODS FOR DISPLAYING DATA
    //============================
    
    // Clears all text from output area
    public void clearOutput() {
        outputArea.setText(""); // removes previous logs
    }
        
    // Displays all active (not completed) jobs
    public void displayCurrentJobs() {
    	// Print header
        outputArea.append("===== Jobs Log =============\n");

        // Get all jobs from controller (history list)
        List<Job> allJobs = vcController.getActiveJobs(); //all jobs ever submitted as history log
        if (allJobs.isEmpty()) { // If no jobs exist
            outputArea.append("No active jobs at the moment.\n");
        } else {
            for (Job j : allJobs) { // Loop through each job
                if (j.getProgressStatus() != Job.JobStatus.COMPLETED) { // Only display jobs that are NOT completed
                    // Print job name and current status
                    outputArea.append("Job: " + j.getJobName() + 
                    		" | Status: " + j.getProgressStatus() + "\n");
                }
            }
        }
     // Print closing divider
        outputArea.append("================================\n\n");
    }
        
    //Displays FIFO-based completion time calculations
    public void displayCompletionTimes() {
    	// Print header
    	 outputArea.append("===== FIFO Completion Times ===================\n");
    	
         // Get calculated completion times from controller
    	 List<Long> completionTimes = vcController.calculateCompletionTimes();

    	 if (completionTimes.isEmpty()) { // If no jobs exist
    	        outputArea.append("No jobs to calculate completion times.\n");
    	        outputArea.append("===============================================\n\n");
    	        return; // stop method
    	    }

    	// Get job list to match with completion times
    	    List<Job> batch = vcController.getCurrentBatch(); // only show the batch just calculated
    	    for (int i = 0; i < batch.size(); i++) { // Loop through jobs and corresponding times
    	        Job j = batch.get(i);
    	        Long time = completionTimes.get(i);
    	        long durationMin = j.getDuration().toMinutes(); // get its completion time
    	        // Display job name, ID, and computed time
    	        outputArea.append("Job: " + j.getJobName() + 
    	        		" | JobID: " + j.getJobID() +
    	        		" | Duration: " + durationMin + " min" +
    	                " | Completion Time: " + time + " min\n");
    	    }
    	    outputArea.append("===============================================\n\n");	    
    }
    
    //Queue - List of all jobs waiting for completion
    public void displayQueue() {
    	// Print header
        outputArea.append("===== Job Queue =====\n");
 
        // Get job list
        List<Job> currentBatch = vcController.getCurrentBatch();
        if (currentBatch.isEmpty()) { // If no jobs exist
            outputArea.append("No jobs in queue.\n");
        } else {
            int position = 1; // queue position counter
            for (Job j : currentBatch) { // Loop through jobs
                if (j.getProgressStatus() != Job.JobStatus.COMPLETED) { // Only show jobs not completed
                	 // Display position, job name, and ID
                    outputArea.append(position + ". " + j.getJobName() +
                            " | ID: " + j.getJobID() + "\n");
                    position++;  // increment position
                }
            }
        }
        outputArea.append("=====================\n\n");
    }
    
    //Server Status - Displays server status and statistics
    public void displayServerStatus() {
    	// Print header
        outputArea.append("===== Server Status =====\n");
 
        Server server = vcController.getServerConnection(); // Get server object from controller
        // If no server connected
        if (server == null) {
            outputArea.append("No server connected.\n");
        } else {
        	// Display server details
            outputArea.append("Server ID: " + server.getServerID() + "\n");
            outputArea.append("Status: " + server.getStatus() + "\n");
            outputArea.append("Jobs in Storage: " + server.getStorage().size() + "\n");
            outputArea.append("Completed Jobs : " + server.getCompletedJobs().size() + "\n");
        }
        outputArea.append("=========================\n\n");
    }
    
    
    
    //NEW========================================================================================================================
    //Moon Worked 
    //Vehicle Monitoring - List of all vehicles with id, status, current job, compute power
    public void displayVehicleMonitor() {
        // Print header
        outputArea.append("===== Vehicle Monitor =====\n");


        List<Vehicle> vehicles = vcController.getConnectedVehicles();
        // vcController.getConnectedVehicles() returns all Vehicle objects registered in the system


        if (vehicles == null || vehicles.isEmpty()) { // If no vehicles registered
            outputArea.append("No vehicles currently registered.\n");
        } else {
            for (Vehicle v : vehicles) { // Loop through each vehicle
                String assignedJob = (v.getCurrentJob() != null)
                        ? v.getCurrentJob().getJobName()
                        : "idle";
                // Display vehicle ID, status, assigned job, and compute power
                outputArea.append("Vehicle ID: " + v.getVehicleID() +
                        " | Status: " + v.getStatus() +
                        " | Job: " + assignedJob +
                        " | Power: " + v.getComputePower() + " GHz\n");
            }


            long available = vehicles.stream()
                    .filter(v -> v.getStatus() == Vehicle.VehicleStatus.AVAILABLE)
                    .count();
            outputArea.append("Available: " + available + " / " + vehicles.size() + " vehicles\n");
            // summary: how many vehicles are currently available out of total registered
        }
        outputArea.append("===========================\n\n");
    }


    //Checkpoint activity - number of checkpoints per job, last checkpoint time, which vehicle created it
    public void displayCheckpointActivity() {
        // Print header
        outputArea.append("===== Checkpoint Activity =====\n");


        List<Job> currentBatch = vcController.getCurrentBatch();
        // loops through all jobs that are not yet completed


        if (currentBatch.isEmpty()) { // If no jobs exist
            outputArea.append("No active jobs to track.\n");
        } else {
            for (Job j : currentBatch) {


                if (j.getProgressStatus() == Job.JobStatus.COMPLETED) continue;
                // skip jobs that are already done


                List<Checkpoint> checkpoints = j.getCheckpoints();
                int reached = (checkpoints != null) ? checkpoints.size() : 0;
                // getCheckpoints() returns a List<Checkpoint> of checkpoint objects for this job


                // Display job name and checkpoint count
                outputArea.append("Job: " + j.getJobName() +
                        " | Checkpoints reached: " + reached + "\n");


                String lastCP = (checkpoints != null && !checkpoints.isEmpty())
                        ? checkpoints.get(checkpoints.size() - 1).getCheckpointID()
                        : "none yet";
                outputArea.append("  Latest: " + lastCP + "\n");
                // prints the most recent checkpoint ID for this job
            }
        }
        outputArea.append("===============================\n\n");
    }
    
    //=====================
    // FUTURE METHODS (NOT IMPLEMENTED YET)
    //=====================
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //Redundancy Tracking - shows required vs assigned vehicles
    
    //(Maybe) Alert system → vehicle departure, job failure, checkpoints, reassignment

    //Overall System Performance → avg completion time, jobs/min, vehicle utilization

    private void applyDecorations() {
    	UIStyling.styleFrameDark(this);
    	UIStyling.styleTextAreaDark(outputArea);

    	// Wrap outputArea scroll pane
    	JScrollPane scrollPane = new JScrollPane(outputArea);
    	UIStyling.styleScrollPaneDark(scrollPane);

    	JPanel centerWrapper = new JPanel(new BorderLayout());
    	UIStyling.styleDashboardPanel(centerWrapper);
    	centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    	centerWrapper.add(scrollPane, BorderLayout.CENTER);

    	add(centerWrapper, BorderLayout.CENTER);

    	// Keep buttons visible by re-adding topPanel if needed
    	if (topPanel != null) {
    		add(topPanel, BorderLayout.NORTH); // ensure topPanel with buttons stays
    	}
	}
}
