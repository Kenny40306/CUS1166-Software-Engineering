import javax.swing.*;
import java.awt.*;

/*=====================
Role Selection Frame - Jaden + Ryan
======================*/

class RoleSelectionFrame extends JFrame{
    	
    public RoleSelectionFrame() {
 
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        JButton vehicleOwnerBtn = new JButton("Vehicle Owner");
        JButton jobOwnerBtn = new JButton("Job Owner (Client)");
        UIStyling.styleButton(vehicleOwnerBtn);
        UIStyling.styleButton(jobOwnerBtn);
       
        panel.add(vehicleOwnerBtn);
        panel.add(jobOwnerBtn);

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        // Button Actions
        vehicleOwnerBtn.addActionListener(e -> {
            dispose();          // close main menu
            new VehicleOwnerFrame();  // open owner screen
        });

        jobOwnerBtn.addActionListener(e -> {
            dispose();          // close main menu
            new JobOwnerFrame();      // open client screen
        });

        setVisible(true);
    	}
    }