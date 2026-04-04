import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Role Selection Frame - Jaden + Ryan + Kendra + Avneet
======================*/


class RoleSelectionFrame extends JFrame{
	
	private VCController vcController;
	private String role;
	private MainControllerFrame adminDashboardFrame; //single instance for mainControllerFrame (Admin)
	private UserDashboardFrame userDashboardFrame; //single instance for UserDashbordFrame (User) 
	
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
        
      //New: always shown (admin & user) Dashboards
        JButton adminDashboardBtn = new JButton("VC Controller Dashboard"); 
        JButton userDashboardBtn = new JButton("User Dashboard");
        		
        UIStyling.styleButton(vehicleOwnerBtn);
        UIStyling.styleButton(jobOwnerBtn);
        UIStyling.styleButton(adminDashboardBtn); 
        UIStyling.styleButton(userDashboardBtn); 
        
        Dimension buttonSize = new Dimension(200, 200); // width 180px, height 150px
        vehicleOwnerBtn.setPreferredSize(buttonSize);
        jobOwnerBtn.setPreferredSize(buttonSize);

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        
        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);
      
        // Add dashboard button based on role
        if (role.equalsIgnoreCase("Admin")) {
            buttonPanel.add(adminDashboardBtn);
        } else {
            buttonPanel.add(userDashboardBtn);
        }
                
        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);
    

        // M5: Notification Area Panel: bottom panel for Admin dashboard buttons to logout, also refresh and clear notifications ======================= 
        // Admin notification area + buttons
        
        if (role.equalsIgnoreCase("Admin")) {
            // Left-side panel to hold notification + server button
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // minimal padding
            UIStyling.stylePanel(leftPanel);

            // ----- Notification Panel (includes Refresh/Clear buttons) -----
            JPanel notifPanel = new JPanel();
            notifPanel.setLayout(new BoxLayout(notifPanel, BoxLayout.Y_AXIS));
            notifPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // remove extra border
            UIStyling.stylePanel(notifPanel);

            // Notification area
            notificationArea = new JTextArea(7, 25); // slightly taller
            notificationArea.setEditable(false);
            notificationArea.setLineWrap(true);
            notificationArea.setWrapStyleWord(true);
            notificationArea.setMargin(new Insets(5,5,5,5)); // reduce padding inside text
            JScrollPane scroll = new JScrollPane(notificationArea);
            scroll.setBorder(BorderFactory.createTitledBorder("User Notifications"));
            scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
            scroll.setPreferredSize(new Dimension(250, 150)); // bigger area
            scroll.setMaximumSize(new Dimension(250, 150));
            notifPanel.add(scroll);

            // Refresh / Clear buttons centered
            JPanel notifBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // tighter spacing
            JButton refreshBtn = new JButton("Refresh");
            JButton clearBtn = new JButton("Clear");
            refreshBtn.setPreferredSize(new Dimension(90, 25));
            clearBtn.setPreferredSize(new Dimension(90, 25));
            UIStyling.styleButton(refreshBtn);
            UIStyling.styleButton(clearBtn);
            notifBtnPanel.add(refreshBtn);
            notifBtnPanel.add(clearBtn);
            notifBtnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notifPanel.add(notifBtnPanel);

            notifPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(notifPanel);

            leftPanel.add(Box.createVerticalStrut(10)); // small spacing before server button

            
            
            // ----- Server Console Button (Independent) -----
            JButton serverBtn = new JButton("Server Console");
            serverBtn.setPreferredSize(new Dimension(200, 30));
            UIStyling.styleButton(serverBtn);
            JPanel serverBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            serverBtnPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            serverBtnPanel.add(serverBtn);
            serverBtnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(serverBtnPanel);

            
            // Add left panel to main panel
            panel.add(leftPanel, BorderLayout.WEST);

            // Button actions
            refreshBtn.addActionListener(e -> {
                vcController.refreshNotificationsFromFile();
                refreshNotifications();
            });
            clearBtn.addActionListener(e -> {
                vcController.clearNotifications("ADMIN");
                refreshNotifications();
            });
            serverBtn.addActionListener(e -> vcController.openServerFrame(RoleSelectionFrame.this));

            refreshNotifications(); // initial load
        }
        //===============================================================================
        
        // NEW Logout button at bottom-right -----------------------------------------------
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        UIStyling.stylePanel(logoutPanel);
        
        JButton logoutButton = new JButton("Logout");
        UIStyling.styleButton(logoutButton);
        logoutPanel.add(logoutButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        UIStyling.stylePanel(bottomPanel);
        bottomPanel.add(logoutPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        
        //Changed for user and admin to have same button behavior
        vehicleOwnerBtn.addActionListener(e -> 
        	openRoleChildFrame(new VehicleOwnerFrame(vcController, this)));
        jobOwnerBtn.addActionListener(e -> 
        	openRoleChildFrame(new JobOwnerFrame(vcController, this)));
        
        adminDashboardBtn.addActionListener(e -> openDashboard());
        userDashboardBtn.addActionListener(e -> openDashboard());
        
        //logout button
        logoutButton.addActionListener(e -> {

        	//Close admin server frame
        	//vcController.closeServerFrame();

        	// Close all open dashboards
            if (adminDashboardFrame != null && adminDashboardFrame.isDisplayable()) {
                adminDashboardFrame.dispose();
            }
            if (userDashboardFrame != null && userDashboardFrame.isDisplayable()) {
                userDashboardFrame.dispose();
            }
            // Close server frame if admin
            if (role.equalsIgnoreCase("Admin")) {
                vcController.closeServerFrame();
            }
            dispose(); // close role selection
            new UserLoginFrame(vcController);
        });

        setVisible(true);
        
        }
    //---------------------------------------------------------------------------------------------------------------
    
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
            if (adminDashboardFrame == null) {
                adminDashboardFrame = new MainControllerFrame(vcController);

                //Force layout BEFORE positioning
                adminDashboardFrame.setSize(300, 400); // use your actual preferred size
                adminDashboardFrame.doLayout();
            }
            dashboardFrame = adminDashboardFrame;

        } else {
            if (userDashboardFrame == null) {
                userDashboardFrame = new UserDashboardFrame(vcController);

                //Same fix for user frame
                userDashboardFrame.setSize(300, 400);
                userDashboardFrame.doLayout();
            }
            dashboardFrame = userDashboardFrame;
        }
              
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
        SwingUtilities.invokeLater(() -> dashboardFrame.setVisible(true));
        
    }
    
    //get notification message to show up
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
    //==============================================================================================================