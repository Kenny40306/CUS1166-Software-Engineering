import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat
======================*/

//---- M4 Implementation: main frame for VCController to show output on dashboard ----

//Main Frame (GUI window for displaying system output)
public class MainControllerFrame extends JFrame{
  
	 // Text area used to display backend output/logs
    private JTextArea outputArea;
    // Reference to VCController (connects GUI to backend logic)
    private VCController vcController;
    
    //initializes the frame and connects it to controller
    public MainControllerFrame(VCController vcController) {
    	// Store controller reference
    	this.vcController = vcController;
    	
        setTitle("VC Dashboard"); // Set window title
        setSize(300, 300); // Set window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window when user exits
        setLayout(new BorderLayout(10,10));  // Use BorderLayout for organizing components
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                       
        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");
        JButton calcButton = new JButton("Calculate");

        UIStyling.styleButton(acceptButton);
        UIStyling.styleButton(rejectButton);
        UIStyling.styleButton(calcButton);

        topPanel.add(acceptButton);
        topPanel.add(rejectButton);
        topPanel.add(calcButton);
        
        add(topPanel, BorderLayout.NORTH);

        //Text Area For Output
        outputArea = new JTextArea(15,50); // Create text area for output display
        outputArea.setEditable(false); // Make text area read-only (user cannot edit)
        JScrollPane scrollPane = new JScrollPane(outputArea); // Add scroll functionality to text area
        add(scrollPane, BorderLayout.CENTER); // Add scroll pane to center of frame
        
       
      //====================================================================================
        //M5 Implementation: Buttons for Accept and Reject
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
            displayServerStatus();       // Display server status
        });
        
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
    
    //=====================
    // FUTURE METHODS (NOT IMPLEMENTED YET)
    //=====================
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //Vehicle Monitoring - List of all vehicles with id, status, current job, compute power
    
    //Checkpoint activity - number of checkpoints per job, last checkpoint time, which vehicle created it
    
    //Redundancy Tracking - shows required vs assigned vehicles
    
    //(Maybe) Alert system → vehicle departure, job failure, checkpoints, reassignment

    //Overall System Performance → avg completion time, jobs/min, vehicle utilization

}

 
