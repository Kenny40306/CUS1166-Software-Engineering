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
    public MainControllerFrame(VCController vcController, JFrame jobOwnerFrame) {
    	// Store controller reference
    	this.vcController = vcController;
    	
        setTitle("MC Dashboard"); // Set window title
        setSize(300, 300); // Set window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window when user exits
        setLayout(new BorderLayout());  // Use BorderLayout for organizing components

        // Position this window next to the Job Owner frame (if available) 
        if(jobOwnerFrame !=null) {
            Point loc = jobOwnerFrame.getLocation();  // get location of other frame
            int x = loc.x + jobOwnerFrame.getWidth() + 10; //place to the right with 10px gap
            int y = loc.y;
            setLocation(x, y);  // set new position
        }else {
        	setLocationRelativeTo(null); // center on screen if no reference frame
        }
        
        // Create main panel with spacing
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15)); // Add padding inside panel
        UIStyling.stylePanel(mainPanel); // Apply custom styling
        
        // Create title label
        JLabel title = new JLabel("Main Controller Output", SwingConstants.CENTER);
        UIStyling.styleLabel(title);  // Apply styling to label
        title.setFont(new Font("Georgia", Font.BOLD,22)); // Set font style
        
        // Add title to top of frame
        add(title, BorderLayout.NORTH);
        mainPanel.add(title, BorderLayout.NORTH);

        outputArea = new JTextArea(15,50); // Create text area for output display
        outputArea.setEditable(false); // Make text area read-only (user cannot edit)

        JScrollPane scrollPane = new JScrollPane(outputArea); // Add scroll functionality to text area
        add(scrollPane, BorderLayout.CENTER); // Add scroll pane to center of frame

     // Connect text area to backend controller so it can print output
        vcController.setOutputArea(outputArea);
        
        setVisible(true); // Make frame visible
    }
    //
    
    //=====================
    // METHODS FOR DISPLAYING DATA
    //=====================
    
    // Clears all text from output area
    public void clearOutput() {
        outputArea.setText(""); // removes previous logs
    }
        
    // Displays all active (not completed) jobs
    public void displayCurrentJobs() {
    	// Print header
        outputArea.append("===== Jobs Log =============\n");

        // Get all jobs from controller (history list)
        List<Job> currentBatch = vcController.getCurrentBatch(); //all jobs ever submitted as history log
        if (currentBatch.isEmpty()) { // If no jobs exist
            outputArea.append("No active jobs at the moment.\n");
        } else {
            for (Job j : currentBatch) { // Loop through each job
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

 
