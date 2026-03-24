import javax.swing.*;
import java.awt.*;

/*=====================
Role Selection Frame - Jaden + Ryan
======================*/


class RoleSelectionFrame extends JFrame{
	
	private VCController vcController;
	private String role;
	private MainControllerFrame dashboardFrame; //single instance

    public RoleSelectionFrame(VCController vcController, String role) {
    	this.vcController = vcController;
    	this.role=role; //User or Admin

        setTitle("Vehicular Cloud Console (VCRTS)");
        setSize(600, 400);
        setLocationRelativeTo(null); // center window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with spacing
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        UIStyling.stylePanel(panel);

        // Title
        JLabel title = UIStyling.createTitleLabel("Select Your Role");
        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3,1,0,15));
        UIStyling.stylePanel(buttonPanel); // optional: style panel same color
        
        //Job and VehicleOwner Buttons
        JButton vehicleOwnerBtn = new JButton("Vehicle Owner");
        JButton jobOwnerBtn = new JButton("Job Owner (Client)");
        JButton dashboardBtn = new JButton("Dashboard");
        UIStyling.styleButton(vehicleOwnerBtn);
        UIStyling.styleButton(jobOwnerBtn);
        UIStyling.styleButton(dashboardBtn);

        Dimension buttonSize = new Dimension(200, 200); // width 180px, height 150px
        vehicleOwnerBtn.setPreferredSize(buttonSize);
        jobOwnerBtn.setPreferredSize(buttonSize);

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);

        // If role is admin, show "Open Admin Dashboard" button
        if (role.equalsIgnoreCase("Admin")) {
            buttonPanel.add(dashboardBtn);
        }

        //---Maybe have a dash board for user to show general info eventually---
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);
    

     // Bottom panel for Admin dashboard button and logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        UIStyling.stylePanel(bottomPanel);

        // Logout button at bottom-right
        JButton logoutButton = new JButton("Logout");
        UIStyling.styleButton(logoutButton);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        panel.add(logoutPanel, BorderLayout.SOUTH);

      
        // Button Actions
        vehicleOwnerBtn.addActionListener(e -> {
            if (role.equalsIgnoreCase("Admin")) {
                new VehicleOwnerFrame(vcController, this); // pass this frame
                this.setVisible(false); // hide RoleSelectionFrame
            } else {
                dispose();
                new VehicleOwnerFrame(vcController, null);
            }
        });

        jobOwnerBtn.addActionListener(e -> {
            if (role.equalsIgnoreCase("Admin")) {
                new JobOwnerFrame(vcController, this); // pass this frame
                this.setVisible(false); // hide RoleSelectionFrame
            } else {
                dispose();
                new JobOwnerFrame(vcController, null);
            }
        });

        dashboardBtn.addActionListener(e -> {
            if (dashboardFrame == null) { // create dashboard only once
                dashboardFrame = new MainControllerFrame(vcController);
                // Position next to RoleSelectionFrame
                Point loc = getLocation();
                dashboardFrame.setLocation(loc.x + getWidth() + 10, loc.y);
            }
            dashboardFrame.setVisible(true); // show existing dashboard
            dashboardFrame.toFront();        // bring to front
        });

        logoutButton.addActionListener(e -> {
            dispose();
            if (dashboardFrame != null) dashboardFrame.dispose(); // close dashboard on logout
            new UserLoginFrame(vcController);
        });
        
        setVisible(true);
        
        }
    }