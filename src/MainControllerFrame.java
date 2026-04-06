import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat + Kendra
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
    	
        setTitle("VC Controller Frame"); // Set window title
        setSize(300, 400); // Set window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window when user exits
        setLayout(new BorderLayout(10,10));  // Use BorderLayout for organizing components
        
     // Title
        //JLabel title = UIStyling.createDashboardTitle("VC Controller Dashboard");
        
        JLabel title = new JLabel("VC Controller Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 20)); // smaller than default 24
        title.setForeground(UIStyling.ACCENT);
        
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
        //Kendra + Subat Wrote This-
           
        acceptButton.addActionListener(e -> {
            // Determine which request came first
            Object firstReq = null; // could be JobRequest or VehicleRequest

            if (!vcController.getPendingJobRequests().isEmpty() && !vcController.getPendingVehicleRequests().isEmpty()) {
                VCController.JobRequest jobReq = vcController.getPendingJobRequests().get(0);
                VCController.VehicleRequest vehReq = vcController.getPendingVehicleRequests().get(0);

                // Compare by requestID timestamp or nanoTime
                if (jobReq.requestID.compareTo(vehReq.requestID) < 0) { // job came first
                    firstReq = jobReq;
                } else {
                    firstReq = vehReq;
                }

            } else if (!vcController.getPendingJobRequests().isEmpty()) {
                firstReq = vcController.getPendingJobRequests().get(0);
            } else if (!vcController.getPendingVehicleRequests().isEmpty()) {
                firstReq = vcController.getPendingVehicleRequests().get(0);
            }

            if (firstReq instanceof VCController.JobRequest jobReq) {
                vcController.approveJob(jobReq.job);
                outputArea.append("[APPROVED] Job: " + jobReq.job.getJobName() + "\n");

            } else if (firstReq instanceof VCController.VehicleRequest vehReq) {
                vcController.approveVehicle(vehReq.vehicle);
                outputArea.append("[APPROVED] Vehicle: " + vehReq.vehicle.getVehicleID() + "\n");

            } else {
                outputArea.append("[VCController] No pending requests to approve.\n");
            }
        });

        rejectButton.addActionListener(e -> {
            // Same logic for rejecting
            Object firstReq = null;

            if (!vcController.getPendingJobRequests().isEmpty() && !vcController.getPendingVehicleRequests().isEmpty()) {
                VCController.JobRequest jobReq = vcController.getPendingJobRequests().get(0);
                VCController.VehicleRequest vehReq = vcController.getPendingVehicleRequests().get(0);

                if (jobReq.requestID.compareTo(vehReq.requestID) < 0) {
                    firstReq = jobReq;
                } else {
                    firstReq = vehReq;
                }

            } else if (!vcController.getPendingJobRequests().isEmpty()) {
                firstReq = vcController.getPendingJobRequests().get(0);
            } else if (!vcController.getPendingVehicleRequests().isEmpty()) {
                firstReq = vcController.getPendingVehicleRequests().get(0);
            }

            if (firstReq instanceof VCController.JobRequest jobReq) {
                vcController.rejectJob(jobReq.job);
                outputArea.append("[REJECTED] Job: " + jobReq.job.getJobName() + "\n");

            } else if (firstReq instanceof VCController.VehicleRequest vehReq) {
                vcController.rejectVehicle(vehReq.vehicle);
                outputArea.append("[REJECTED] Vehicle: " + vehReq.vehicle.getVehicleID() + "\n");

            } else {
                outputArea.append("[VCController] No pending requests to reject.\n");
            }
        });
        
        
        //Calculate button action
        calcButton.addActionListener(e -> {
            clearOutput();               // Clear previous logs
            displayCompletionTimes();    // Display FIFO completion times
        });
        
        applyDecorations(); // decorates buttons and dash board
    }
        //==========================================================================================
        
   
           
    //============================
    // METHODS FOR DISPLAYING DATA
    //============================
    
    // Clears all text from output area
    public void clearOutput() {
        outputArea.setText(""); // removes previous logs
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
    	        outputArea.append("Job: " + j.getJobName() + "\n" +
    	        		" | JobID: " + j.getJobID() + "\n"+
    	        		" | Duration: " + durationMin + " min" + "\n" +
    	                " | Completion Time: " + time + " min\n");
    	    }
    	    outputArea.append("===============================================\n\n");	    
    }
    
    
    //Moontarin Worked On This:
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
     
    
    //=====================
    // FUTURE METHODS (NOT IMPLEMENTED YET)
    //=====================
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //Redundancy Tracking - shows required vs assigned vehicles
    
    //(Maybe) Alert system → vehicle departure, job failure, checkpoints, reassignment

    //Overall System Performance → avg completion time, jobs/min, vehicle utilization
