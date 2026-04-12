import javax.swing.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.*;

//All New For M5 =================================================================
/*=====================
UserDashboardFrame - Avneet + Moontarin
======================*/

public class UserDashboardFrame extends JFrame {

	private VCController vcController;
	private JTextArea notificationArea;
	private JTextArea infoArea;  // field so applyDecorations() can access it

	private JButton prevBtn; // shows past Submissions
	private JButton nextBtn; // shows current Submissions
	
	private List<String> allSubmissions = new ArrayList<>();//stores latest pending submission
    private int displayIndex = -1; // index to track cycling through past submissions

    
    public UserDashboardFrame(VCController vcController) {
    	// Store controller reference
    	this.vcController = vcController;

        setTitle("User Dashboard Frame");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title        
        JLabel title = UIStyling.createDashboardTitle("User Dashboard");
        add(title, BorderLayout.NORTH);
        
        //Notifications-------------------------------------------------
        notificationArea = new JTextArea(8,25);
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
       
        JScrollPane scroll = new JScrollPane(notificationArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Your Notifications"));
        scroll.setPreferredSize(new Dimension(280, 70));
        panel.add(scroll, BorderLayout.NORTH);
              
                
        Dimension buttonSize = new Dimension(85, 25); // fixed width & height
       
        JPanel notifBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // 8 px gap horizontally
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");
        UIStyling.styleDashboardButton(refreshBtn); //CHANGED 
        UIStyling.styleDashboardButton(clearBtn);   //CHANGED 
        refreshBtn.setPreferredSize(buttonSize);
        clearBtn.setPreferredSize(buttonSize);
        notifBtnPanel.add(refreshBtn);
        notifBtnPanel.add(clearBtn);

        // Wrapper to add vertical spacing
        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));
        bottomWrapper.add(Box.createVerticalStrut(10)); // spacing
        bottomWrapper.add(notifBtnPanel);

        
        JPanel notifPanel = new JPanel(new BorderLayout());
        notifPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        notifPanel.add(scroll, BorderLayout.CENTER);
        notifPanel.add(bottomWrapper, BorderLayout.SOUTH);     
        
        panel.add(notifPanel, BorderLayout.NORTH);
       

        //Info Area with scroll
        infoArea = new JTextArea("Welcome! View your updates below.");
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createTitledBorder("Dashboard Info"));
        panel.add(infoScroll, BorderLayout.CENTER);
        
        // Two separate buttons for Current / Past submissions
        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");
        UIStyling.styleDashboardButton(prevBtn);
        UIStyling.styleDashboardButton(nextBtn);
        prevBtn.setPreferredSize(buttonSize);
        nextBtn.setPreferredSize(buttonSize);


        // Put them side by side with spacing
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // 5 px gap
        togglePanel.add(prevBtn);
        togglePanel.add(nextBtn);
              
        panel.add(togglePanel, BorderLayout.SOUTH);
        add(panel);
        
        //Load notifications AFTER user is set
        refreshNotifications(); 
    	
    	// ================= BUTTON ACTIONS =================
    	refreshBtn.addActionListener(e -> {
    		refreshNotifications();
    	});

    	clearBtn.addActionListener(e -> {
      		notificationArea.setText("");// clear only the notification area
    	});
    
    	   // Navigate to next (newer) submission
        nextBtn.addActionListener(e -> {
            if (!allSubmissions.isEmpty()) {
                displayIndex++;
                if (displayIndex >= allSubmissions.size()) displayIndex = allSubmissions.size() - 1;
                showSubmission(allSubmissions.get(displayIndex), "SUBMISSION");
            }
        });

        // Navigate to previous (older) submission
        prevBtn.addActionListener(e -> {
            if (!allSubmissions.isEmpty()) {
                displayIndex--;
                if (displayIndex < 0) displayIndex = 0;
                showSubmission(allSubmissions.get(displayIndex), "SUBMISSION");
            }
        });
    	
        // Initial load
        refreshNotifications();
        // Apply all decorations (dark theme, title
    	applyDecorations(); //NEW
	}
        
    // Load all notifications and separate approved/rejected submissions */
  
    public void refreshNotifications() {
        notificationArea.setText("");

        String userId = vcController.getCurrentUserId();
        if (userId == null) {
            notificationArea.append("No user logged in\n");
            return;
        }

        List<String> msgs = vcController.getNotifications(userId);

        if (msgs.isEmpty()) {
            notificationArea.append("No updates yet\n");
            infoArea.setText("No submissions to display.");
            return;
        }

        for (String m : msgs) {
            notificationArea.append(m + "\n");
            if ((m.toLowerCase().contains("approved") || m.toLowerCase().contains("rejected"))
                    && !allSubmissions.contains(m)) {
                allSubmissions.add(m);
            }
        }

        if (!allSubmissions.isEmpty()) {
            displayIndex = allSubmissions.size() - 1; // most recent first
            showSubmission(allSubmissions.get(displayIndex),"SUBMISSION");

        }
    }
    
    private void showSubmission(String notification, String type) {
    	
    	infoArea.setText(""); // clear previous content
    	
    	if (notification == null) {
    		infoArea.setText("No submissions to display.");
            return;
        }

    	String header = "SUBMISSION";
        if (notification.toLowerCase().contains("job")) header = "JOB";
        else if (notification.toLowerCase().contains("vehicle")) header = "VEHICLE";

        infoArea.append("== " + header + " SUBMISSION ==\n");
        
        if (notification.toLowerCase().contains("approved")) {
        	if (header.equals("JOB")) {
        		 infoArea.append(notification + "\n");
        		 displaySubmitJob(notification);
        		 typeJob();
        	}else if (header.equals("VEHICLE")) {
                 displaySubmitVehicle(notification);
            }
        	trackJobProgress();             
        }else if (notification.toLowerCase().contains("rejected")) {
        	infoArea.append(notification + "\n");
        	infoArea.append("Please submit again.\n");  
        }else {
            infoArea.append(notification + "\n");   // fallback for unexpected messages
        }
    }

        
 //Moon worked on this: PLACEHOLDER TEXTS ===============================
    public void displaySubmitJob(String notification) {    	    	
    	// Extract job name from notification message, e.g., "Job 'Fix Engine' APPROVED"
        String jobName = "[Unknown Job]";
        if (notification != null && !notification.isEmpty()) {
            jobName = notification.replaceAll("(?i).*job\\s+'(.+?)'.*", "$1");
        }

    	// infoArea.setText("=== JOB SUBMISSION ===\n");
    	 infoArea.append("Job Name:" + jobName + "\n");
    	 infoArea.append("Duration: [e.g., 2 hours]\n");
    	 infoArea.append("Deadline: [MM/DD/YYYY]\n");
    	 infoArea.append("Assigned Vehicle: V-101\n");
    }
  //Type job
    public void typeJob() {
    	infoArea.append("\n=== JOB DETAILS ===\n");
        infoArea.append("Description: [Job description will appear here]\n");
    }
    
    //Submit Vehicle: type of vehicle
    public void displaySubmitVehicle(String notification) {
      	// Extract vehicle name from notification message, e.g., "Vehicle 'Fix Engine' APPROVED"
        String vehicleName = "[Unknown Vehicle]";
        if (notification != null && !notification.isEmpty()) {
            vehicleName = notification.replaceAll("(?i).*vehicle\\s+'(.+?)'.*", "$1");
        }
    	//infoArea.append("\n=== VEHICLE SUBMISSION ===\n");
        infoArea.append("Vehicle ID: " + vehicleName + "\n");
        infoArea.append("Model: Ford Bronco\n");
        infoArea.append("Capacity: 1000 kg\n");
        infoArea.append("Connection Status: [Connected]\n");
        infoArea.append("Last Sync: Date/Time\n");
    }
    
    //Track Job Progress (status:pending/processing/completed/failed)
    public void trackJobProgress() {
    	infoArea.append("Status: ----\n");
    	infoArea.append("Progress: 60%\n");
    }
    
    
//========================
// Designing the Dashboard
//========================
 // Applies dark theme and title label to the frame

    private void applyDecorations() {

        UIStyling.styleFrameDark(this); 
        UIStyling.styleTextAreaDark(notificationArea); 
        UIStyling.styleTextAreaDark(infoArea);        

        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                UIStyling.stylePanelDeep((JPanel) c);
            }
        }
    }
}
