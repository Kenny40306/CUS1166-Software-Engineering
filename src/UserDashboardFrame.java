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
	//private boolean showingCurrent = true; // true = current submission, false = past submissions
	
	private List<String> allSubmissions = new ArrayList<>();//stores latest pending submission
   // private final List<String> pastSubmissions = new ArrayList<>(); //stores approved/rejected submission
    private int displayIndex = 0; // index to track cycling through past submissions

    
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
        notificationArea = new JTextArea(5,25);
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
       
        JScrollPane scroll = new JScrollPane(notificationArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Your Notifications"));
        scroll.setPreferredSize(new Dimension(200, 60));
        panel.add(scroll, BorderLayout.NORTH);
        
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");
        UIStyling.styleDashboardButton(refreshBtn); //CHANGED 
        UIStyling.styleDashboardButton(clearBtn);   //CHANGED 

        JPanel notifBtnPanel = new JPanel();
        notifBtnPanel.add(refreshBtn);
        notifBtnPanel.add(clearBtn);
        
        JPanel notifPanel = new JPanel(new BorderLayout());
        notifPanel.add(scroll, BorderLayout.CENTER);
        notifPanel.add(notifBtnPanel, BorderLayout.SOUTH);
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


        // Put them side by side with spacing
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // 5 px gap
        prevBtn.setPreferredSize(new Dimension(70, 30));
        nextBtn.setPreferredSize(new Dimension(70, 30));
        togglePanel.add(prevBtn);
        togglePanel.add(nextBtn);
      
        panel.add(togglePanel, BorderLayout.SOUTH);
        
        add(panel);
        

        //Load notifications AFTER user is set
        refreshNotifications(); 
    	
    	// ================= BUTTON ACTIONS =================
    	refreshBtn.addActionListener(e -> {
    		vcController.refreshNotificationsFromFile();
    		refreshNotifications();
    	});

    	clearBtn.addActionListener(e -> {
    		String userId = vcController.getCurrentUserId();
    		vcController.clearNotifications(userId);
    		refreshNotifications();
    	});
    
        nextBtn.addActionListener(e -> showNextSubmission());
        prevBtn.addActionListener(e -> showPreviousSubmission());
    	
        // Initial load
        refreshNotifications();
        // Apply all decorations (dark theme, title
    	applyDecorations(); //NEW
	}
        
    // Load all notifications and separate approved/rejected submissions */
    public void refreshNotifications() {
        notificationArea.setText("");
        allSubmissions.clear();
        displayIndex = 0;

        String userId = vcController.getCurrentUserId();
        if (userId == null) {
            notificationArea.append("No user logged in\n");
            return;
        }

        vcController.loadNotificationsFromFile();
        List<String> msgs = vcController.getNotifications(userId);

        if (msgs.isEmpty()) {
            notificationArea.append("No updates yet\n");
            infoArea.setText("No submissions to display.");
            return;
        }

        for (String m : msgs) {
            notificationArea.append(m + "\n");
            if (m.toLowerCase().contains("approved") || m.toLowerCase().contains("rejected")) {
                allSubmissions.add(m);
            }
        }

        if (allSubmissions.isEmpty()) {
            infoArea.setText("No approved/rejected submissions yet.");
        } else {
            displayIndex = allSubmissions.size() - 1; // show most recent first
            showSubmission(allSubmissions.get(displayIndex), "SUBMISSION");
        }
    }
   
    
    private void showNextSubmission() {
        if (allSubmissions.isEmpty()) return;

        displayIndex++;
        if (displayIndex >= allSubmissions.size()) {
            displayIndex = allSubmissions.size() - 1; // stay at last
        }
        showSubmission(allSubmissions.get(displayIndex), "SUBMISSION");
    }

    private void showPreviousSubmission() {
        if (allSubmissions.isEmpty()) return;

        displayIndex--;
        if (displayIndex < 0) {
            displayIndex = 0; // stay at first
        }
        showSubmission(allSubmissions.get(displayIndex), "SUBMISSION");
    }    
    
    
    private void showSubmission(String notification, String type) {
        if (notification == null) {
            infoArea.append("No " + type.toLowerCase() + " submission.\n");
            return;
        }

        infoArea.append("=== " + type + " SUBMISSION ===\n");
        infoArea.append(notification + "\n");

        if (notification.toLowerCase().contains("approved")) {
            displaySubmitJob(notification);
            displaySubmitVehicle(notification);
            trackJobProgress();
            typeJob();
        } else if (notification.toLowerCase().contains("rejected")) {
            infoArea.append("Please submit again.\n");
        }
    }

        
 //Moon worked on this: PLACEHOLDER TEXTS ===============================
    public void displaySubmitJob(String notification) {
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}
    	    	
    	// Extract job name from notification message, e.g., "Job 'Fix Engine' APPROVED"
        String jobName = "[Unknown Job]";
        if (notification != null && !notification.isEmpty()) {
            jobName = notification.replaceAll("(?i).*job\\s+'(.+?)'.*", "$1");
        }

    	 infoArea.setText("=== JOB SUBMISSION ===\n");
    	 infoArea.append("Job Name:" + jobName + "\n");
    	 infoArea.append("Duration: [e.g., 2 hours]\n");
    	 infoArea.append("Deadline: [MM/DD/YYYY]\n");
    }
  //Type job
    public void typeJob() {
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}
    	infoArea.append("\n=== JOB TYPE ===\n");
        infoArea.append("Description: [Job description will appear here]\n");
    }
    
    //Submit Vehicle: type of vehicle
    public void displaySubmitVehicle(String notification) {
    	
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}

    	infoArea.append("\n=== VEHICLE SETUP ===\n");
        infoArea.append("Vehicle Type: [N/A] \n");
        infoArea.append("Connection Status: [Not Connected]\n");
    }
    
    //Track Job Progress (status:pending/processing/completed/failed)
    public void trackJobProgress() {
    	infoArea.append("Status: ----\n");
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

//==================================
//FUTURE METHODS (NOT IMPLEMENTED YET)
//===================================
  		
	//refresh dashboard
   //Submit Job: type of job submitted, connected vehicles
  
  //refresh dashboard
  /*public void refreshDashboard() {
  	refreshNotifications();
  	trackJobProgress();
  	infoArea.append("\nDashboard refreshed at " + java.time.LocalTime.now());
  }*/
      