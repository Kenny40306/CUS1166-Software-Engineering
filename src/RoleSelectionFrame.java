import javax.swing.*;
import java.awt.*;

/*=====================
Role Selection Frame - Jaden + Ryan
======================*/


class RoleSelectionFrame extends JFrame{
	
	private VCController vcController;

    public RoleSelectionFrame(VCController vcController) {
    	this.vcController = vcController;

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        UIStyling.stylePanel(buttonPanel); // optional: style panel same color
        
        int buttonWidth = 120;
        int buttonHeight = 35;
        int margin = 20;

        JButton logoutButton = new JButton("Logout");

        logoutButton.setBounds(
                500 - buttonWidth - margin,
                350 - buttonHeight - margin,
                buttonWidth,
                buttonHeight
        );

        UIStyling.styleButton(logoutButton);
        add(logoutButton);
        
        JButton vehicleOwnerBtn = new JButton("Vehicle Owner");
        JButton jobOwnerBtn = new JButton("Job Owner (Client)");
        UIStyling.styleButton(vehicleOwnerBtn);
        UIStyling.styleButton(jobOwnerBtn);
        
        Dimension buttonSize = new Dimension(200, 200); // width 180px, height 150px
        vehicleOwnerBtn.setPreferredSize(buttonSize);
        jobOwnerBtn.setPreferredSize(buttonSize);

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);
    

        // Button Actions
        vehicleOwnerBtn.addActionListener(e -> {
            dispose();          // close main menu
            new VehicleOwnerFrame(vcController);  // open owner screen
        });

        jobOwnerBtn.addActionListener(e -> {
            dispose();          // close main menu
            new JobOwnerFrame(vcController);      // open client screen
        });
        
        logoutButton.addActionListener(e -> { //close menu
        	dispose();						//opens login screen
        	new UserLoginFrame(vcController);
        	}
        );
        
        setVisible(true);
    	}
    }