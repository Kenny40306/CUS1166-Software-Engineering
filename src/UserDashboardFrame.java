import javax.swing.*;
import java.util.List;
import java.awt.*;

//All New For M5 =================================================================
public class UserDashboardFrame extends JFrame {

	private VCController vcController;
	private JTextArea notificationArea;
	private JTextArea infoArea;  // field so applyDecorations() can access it
	   
    public UserDashboardFrame(VCController vcController) {
    	// Store controller reference
    	this.vcController = vcController;

        setTitle("User Dashboard");
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
        UIStyling.styleDashboardButton(refreshBtn); /* CHANGED */
        UIStyling.styleDashboardButton(clearBtn);   /* CHANGED */

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
    
    	//maybe have a cancel Job button eventually?

        // Apply all decorations (dark theme, title
    	applyDecorations(); //NEW
    	setVisible(true);
	}
    
    // Load all stored notifications
    public void refreshNotifications() {
        notificationArea.setText("");
       
        String userId = vcController.getCurrentUserId();
        if (userId == null) {
            notificationArea.append("No user logged in\n");
            return;
        }
        
        vcController.loadNotificationsFromFile();
        List<String> msgs = vcController.getNotifications(userId);

        if (msgs.isEmpty()) {
            notificationArea.append("No job updates yet\n");
        } else {
            for (String m : msgs) {
                notificationArea.append(m + "\n");
           
                //  KEY LOGIC: trigger dashboard updates
                if (m.toLowerCase().contains("approved")) {
                    displaySubmitJob();
                    displaySubmitVehicle();
                    trackJobProgress();
                    typeJob();
                } 
                else if (m.toLowerCase().contains("rejected")) {
                    infoArea.setText("Your job was rejected. Please submit again.");
                }
            }
        }
        notificationArea.setCaretPosition(0); //scroll up
    }
      

    
    
    
    
 //Moon worked on this: PLACEHOLDER TEXTS
//==================================
//FUTURE METHODS (NOT IMPLEMENTED YET)
//===================================
  //Submit Vehicle: type of vehicle
	//Track Job Progress (status:pending/processing/completed/failed)
	//Type job
	//refresh dashboard
    //Submit Job: type of job submitted, connected vehicles
    
    public void displaySubmitJob() {
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}
    	 infoArea.setText("=== JOB SUBMISSION ===\n");
    	 infoArea.append("Job Name: [Enter Job Name]\n");
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
    public void displaySubmitVehicle() {
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}
    	infoArea.append("\n=== VEHICLE SETUP ===\n");
        infoArea.append("Vehicle Type: [N/A]\n");
        infoArea.append("Connection Status: [Not Connected]\n");
    }
    
    //Track Job Progress (status:pending/processing/completed/failed)
    public void trackJobProgress() {
    	String userId = vcController.getCurrentUserId();
    	if (userId == null) {
    		infoArea.setText("No user logged in.");
    		return;
    	}
    	infoArea.append("Status: ----\n");
    }
    
    //refresh dashboard
    /*public void refreshDashboard() {
    	refreshNotifications();
    	trackJobProgress();
    	infoArea.append("\nDashboard refreshed at " + java.time.LocalTime.now());
    }
        
    //Notification update: job completed
    /*public void updateNotification(String message) {
    	String userId = vcController.getCurrentUserId();
    	if (userId != null) {
    		vcController.addNotification(userId, message);
    		refreshNotifications();
    	}
    }*/

    
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