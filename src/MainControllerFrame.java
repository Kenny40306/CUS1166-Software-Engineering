import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat
======================*/

//---- M4 Implementation: main frame for VCController to show output on dashboard ----

// Main Frame (GUI window for displaying system output)
public class MainControllerFrame extends JFrame{
  
    // Text area used to display backend output/logs
    private JTextArea outputArea;
    
    // Reference to VCController (connects GUI to backend logic)
    private VCController vcController;
    
    //initializes the frame and connects it to controller
    public MainControllerFrame(VCController vcController, JFrame jobOwnerFrame) {
        
        // Store controller reference
        this.vcController = vcController;
        
        // Set window title
        setTitle("VCRTS - Main Controller Unit");
        
        // Set window size
        setSize(300, 300);
        
        // Close only this window when user exits
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Use BorderLayout for organizing components
        setLayout(new BorderLayout());
        
        // Position this window next to the Job Owner frame (if available)
        if(jobOwnerFrame !=null) {
            Point loc = jobOwnerFrame.getLocation(); // get location of other frame
            int x = loc.x + jobOwnerFrame.getWidth() + 10; // place to the right with 10px gap
            int y = loc.y;
            setLocation(x, y); // set new position
        }else {
            setLocationRelativeTo(null); // center on screen if no reference frame
        }
        
        // Create main panel with spacing
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        
        // Add padding inside panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        
        // Apply custom styling
        UIStyling.stylePanel(mainPanel);
        
        // Create title label
        JLabel title = new JLabel("Main Controller Output", SwingConstants.CENTER);
        
        // Apply styling to label
        UIStyling.styleLabel(title);
        
        // Set font style
        title.setFont(new Font("Georgia", Font.BOLD,22));
        
        // Add title to top of frame
        add(title, BorderLayout.NORTH);
        mainPanel.add(title, BorderLayout.NORTH);
        
        // Create text area for output display
        outputArea = new JTextArea(15,50);
        
        // Make text area read-only (user cannot edit)
        outputArea.setEditable(false);
        
        // Add scroll functionality to text area
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        // Add scroll pane to center of frame
        add(scrollPane, BorderLayout.CENTER);
        
        // Connect text area to backend controller so it can print output
        vcController.setOutputArea(outputArea);
        
        // Make frame visible
        setVisible(true);
    }
    
    //=====================
    // METHODS FOR DISPLAYING DATA
    //=====================
    
    // Clears all text from output area
    public void clearOutput() {
        outputArea.setText(""); // removes previous logs
    }
        
    // Displays all active (not completed) jobs
    public void displayCurrentJobs() {
        
        // Print section header
        outputArea.append("===== Jobs Log =====\n");
        
        // Get all jobs from controller (history list)
        List<Job> currentBatch = vcController.getCurrentBatch();
        
        // If no jobs exist
        if (currentBatch.isEmpty()) {
            outputArea.append("No active jobs at the moment.\n");
        } else {
            
          // Loop through each job
           for (Job j : currentBatch) {
                
          // Only display jobs that are NOT completed
          if (j.getProgressStatus() != Job.JobStatus.COMPLETED) {
                    
          // Print job name and current status
          outputArea.append("Job: " + j.getJobName() + 
         " | Status: " + j.getProgressStatus() + "\n");
              
          }
            }
        }
        
        // Print closing divider
        outputArea.append("======================\n");
    }
        
    // Displays FIFO-based completion time calculations
    public void displayCompletionTimes() {
        
         // Get calculated completion times from controller
         List<Long> completionTimes = vcController.calculateCompletionTimes();
         
         // If no jobs exist
         if (completionTimes.isEmpty()) {
                outputArea.append("\nNo jobs to calculate completion times.\n");
                return; // stop method
            }
            
            // Print section header
            outputArea.append("\n=== FIFO Completion Times ===\n");
            
            // Get job list to match with completion times
            List<Job> batch = vcController.getCurrentBatch();
            
            // Loop through jobs and corresponding times
            for (int i = 0; i < batch.size(); i++) {
                
                Job j = batch.get(i); // get job
                Long time = completionTimes.get(i); // get its completion time
                
                // Display job name, ID, and computed time
                outputArea.append("Job: " + j.getJobName() + 
                        " | JobID: " + j.getJobID() +
                        " | Completion Time: " + time + " min\n");
            }
            
         
            outputArea.append("============================\n\n");      
    }
    
    //=====================
    // FUTURE METHODS (NOT IMPLEMENTED YET)
    //=====================
    
    // Current Job Submissions → will show client name + timestamp
    
    // Vehicle Monitoring → track vehicle ID, status, job, compute power
    
    // Checkpoint Activity → track progress checkpoints per job
    
    // Redundancy Tracking → ensure enough vehicles are assigned
    
    // Queue → manage waiting jobs
   
    // Displays jobs in queue order (FIFO)
    public void displayQueue() {
        
        // Print header
        outputArea.append("===== Job Queue =====\n");
 
        // Get job list
        List<Job> currentBatch = vcController.getCurrentBatch();
        
        // If no jobs exist
        if (currentBatch.isEmpty()) {
            outputArea.append("No jobs in queue.\n");
        } else {
            
            int position = 1; // queue position counter
            
            // Loop through jobs
            for (Job j : currentBatch) {
                
        // Only show jobs not completed
         if (j.getProgressStatus() != Job.JobStatus.COMPLETED) {
                    
        // Display position, job name, and ID
         outputArea.append(position + ". " + j.getJobName() +
         " | ID: " + j.getJobID() + "\n");
                    
         position++; // increment position
                }
            }
        }
        
    
        outputArea.append("=====================\n");
    }
        
    // Displays server status and statistics
    public void displayServerStatus() {
        
        // Print header
        outputArea.append("===== Server Status =====\n");
 
        // Get server object from controller
        Server server = vcController.getServerConnection();
        
        // If no server connected
        if (server == null) {
            outputArea.append("No server connected.\n");
        } else {
            
            // Display server details
            outputArea.append("Server ID      : " + server.getServerID() + "\n");
            outputArea.append("Status         : " + server.getStatus() + "\n");
            outputArea.append("Jobs in Storage: " + server.getStorage() + "\n");
            outputArea.append("Completed Jobs : " + server.getCompletedJobs().size() + "\n");
        }
        
       
        outputArea.append("=========================\n");
    }
    
    
    //(Maybe) Alert system → vehicle departure, job failure, checkpoints, reassignment
    
    //Overall System Performance → avg completion time, jobs/min, vehicle utilization
}
