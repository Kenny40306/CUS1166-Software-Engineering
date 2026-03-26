import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Role Selection Frame - Jaden + Ryan
======================*/


class RoleSelectionFrame extends JFrame{
	
	private VCController vcController;
	private String role;
	private MainControllerFrame adminDashboardFrame; //single instance
	private UserDashboardFrame userDashboardFrame; //single instance
	
	//M5 notification area for accept or rejected job
    private JTextArea notificationArea = new JTextArea(5, 20);
    
    public RoleSelectionFrame(VCController vcController, String role) {
    	this.vcController = vcController;
    	this.role=role; //User or Admin

    	vcController.setRoleFrame(this); //link this frame in VCController for notification updates
    	 
        setTitle("Vehicular Cloud Console (VCRTS)");
        setSize(600, 400);
        setLocationRelativeTo(null); // center window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with spacing
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        UIStyling.stylePanel(panel);
        add(panel, BorderLayout.CENTER);

        // Title
        JLabel title = UIStyling.createTitleLabel("Select Your Role");
        add(title, BorderLayout.NORTH);
        

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3,1,0,15));
        UIStyling.stylePanel(buttonPanel); // optional: style panel same color
        
        //Job and VehicleOwner Buttons
        JButton vehicleOwnerBtn = new JButton("Vehicle Owner");
        JButton jobOwnerBtn = new JButton("Job Owner (Client)");
        JButton dashboardBtn = new JButton("Dashboard");
        
        UIStyling.styleButton(vehicleOwnerBtn);
        UIStyling.styleButton(jobOwnerBtn);
        UIStyling.styleButton(dashboardBtn); //always shown (admin & user)
      
        Dimension buttonSize = new Dimension(200, 200); // width 180px, height 150px
        vehicleOwnerBtn.setPreferredSize(buttonSize);
        jobOwnerBtn.setPreferredSize(buttonSize);

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);
        buttonPanel.add(dashboardBtn); 
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);
    

        // M5: Bottom panel for Admin dashboard button and logout ======================= 
        JPanel bottomPanel = new JPanel(new BorderLayout());
        UIStyling.stylePanel(bottomPanel);
        	
        if (role.equalsIgnoreCase("Admin")) {
        		notificationArea = new JTextArea(5,25);
        		notificationArea.setEditable(false);
        		notificationArea.setLineWrap(true);
                notificationArea.setWrapStyleWord(true);
        		
        		JScrollPane scroll = new JScrollPane(notificationArea);
        		scroll.setBorder(BorderFactory.createTitledBorder("Notifications"));
        		scroll.setPreferredSize(new Dimension(400, 60));
        
        		//Buttons
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

                bottomPanel.add(notifPanel, BorderLayout.WEST);

                refreshNotifications();  //load notifications for current user*/

                refreshBtn.addActionListener(e -> {
                    vcController.refreshNotificationsFromFile();
                    refreshNotifications();
                });

                clearBtn.addActionListener(e -> {
                    vcController.clearNotifications("ADMIN");
                    refreshNotifications();
                });
            }    
        //===============================================================================
        
        // Logout button at bottom-right
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        UIStyling.stylePanel(logoutPanel);
        
        JButton logoutButton = new JButton("Logout");
        UIStyling.styleButton(logoutButton);
       
        logoutPanel.add(logoutButton);
        
        bottomPanel.add(logoutPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        
        //Changed for user and admin to have same button behavior
        vehicleOwnerBtn.addActionListener(e -> 
        	openRoleChildFrame(new VehicleOwnerFrame(vcController, this)));
        jobOwnerBtn.addActionListener(e -> 
        	openRoleChildFrame(new JobOwnerFrame(vcController, this)));
        dashboardBtn.addActionListener(e -> openDashboard());
        
        
        //logout button
        logoutButton.addActionListener(e -> {

        	// Close all open dashboards
            if (adminDashboardFrame != null && adminDashboardFrame.isDisplayable()) {
                adminDashboardFrame.dispose();
            }
            if (userDashboardFrame != null && userDashboardFrame.isDisplayable()) {
                userDashboardFrame.dispose();
            }
            dispose(); // close role selection
        	
            new UserLoginFrame(vcController);
        });

        setVisible(true);
        
        }
    //New M5 =================================================================================================
    // Unified method to open JobOwner or VehicleOwner frames
    private void openRoleChildFrame(JFrame childFrame) {
        childFrame.setVisible(true);
        this.setVisible(false); // hide RoleSelectionFrame
    }

    // Unified method to open dashboard
    private void openDashboard() {
    	vcController.refreshNotificationsFromFile();
    	 
        JFrame dashboardFrame;
        if (role.equalsIgnoreCase("Admin")) {
            if (adminDashboardFrame == null) adminDashboardFrame = new MainControllerFrame(vcController);
            dashboardFrame = adminDashboardFrame;
        } else {
            if (userDashboardFrame == null) userDashboardFrame = new UserDashboardFrame(vcController);
            dashboardFrame = userDashboardFrame;
        }

        dashboardFrame.setVisible(true);
        dashboardFrame.toFront();
        
        // Position next to RoleSelectionFrame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point loc = getLocationOnScreen();
        int spacing = 10;

        int x = loc.x + getWidth() + spacing;
        int y = loc.y;

        if (x + dashboardFrame.getWidth() > screenSize.width) {
            int newRoleX = screenSize.width - (getWidth() + dashboardFrame.getWidth() + spacing);
            newRoleX = Math.max(newRoleX, 10);
            setLocation(newRoleX, loc.y);
            x = newRoleX + getWidth() + spacing;
        }

        if (y + dashboardFrame.getHeight() > screenSize.height) {
            y = screenSize.height - dashboardFrame.getHeight() - 10;
        }

        dashboardFrame.setLocation(x, y);
    }
        
    public void appendNotification(String message) {
    	SwingUtilities.invokeLater(() -> {
    		notificationArea.append(message + "\n");
    	});
    }
    	
    	// Refresh all notifications for current role/user
    public void refreshNotifications() {
    	if (!role.equalsIgnoreCase("Admin")) return;

    		notificationArea.setText("");
            List<String> msgs = vcController.getNotifications("ADMIN");

            if (msgs.isEmpty()) {
                notificationArea.append("No admin notifications\n");
            }else{
                for (String m : msgs) {
                    notificationArea.append(m + "\n");
                }
            }
         }
    }
    