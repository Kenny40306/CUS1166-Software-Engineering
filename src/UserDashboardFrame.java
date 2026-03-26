import javax.swing.*;
import java.util.List;
import java.awt.*;

//All New For M5 =================================================================
public class UserDashboardFrame extends JFrame {

	private VCController vcController;
	private JTextArea notificationArea;
	
    public UserDashboardFrame(VCController vcController) {
    	// Store controller reference
    	this.vcController = vcController;

        setTitle("User Dashboard");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("User Dashboard", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH); 
        
        notificationArea = new JTextArea(5,25);
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
       
     
        JScrollPane scroll = new JScrollPane(notificationArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Your Notifications"));
        scroll.setPreferredSize(new Dimension(200, 50));
        panel.add(scroll, BorderLayout.NORTH);
        
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");
        UIStyling.styleButton(refreshBtn);
        UIStyling.styleButton(clearBtn);
        
        JPanel notifBtnPanel = new JPanel();
        notifBtnPanel.add(refreshBtn);
        notifBtnPanel.add(clearBtn);

        JPanel notifPanel = new JPanel(new BorderLayout());
        notifPanel.add(scroll, BorderLayout.CENTER);
        notifPanel.add(notifBtnPanel, BorderLayout.SOUTH);
        topPanel.add(notifPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JTextArea infoArea = new JTextArea("Welcome! View your job updates below.");
        infoArea.setEditable(false);
        panel.add(infoArea, BorderLayout.CENTER);

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
            }
        }
    }
}

       
//Methods that will be used for user

//Submit Job: type of job submitted, connected vehicles
//Submit Vehicle: type of vehicle 
//Track Job Progress (status:pending/processing/completed/failed)
//Type job
//refresh dashboard

//Notification update: job completed

//
//VC Controller provide notification (accepted or rejected)