import java.awt.*;
import javax.swing.*;
//Jaden opens the main frame where you select your role as either a vehicle owner or job owner (client)
public class VCRTSGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("Vehicular Cloud Console (VCRTS)");
        frame.setSize(450, 250);
        frame.setLocationRelativeTo(null); // center window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with spacing
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Select Your Role", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        JButton vehicleOwnerBtn = new JButton("Vehicle Owner");
        JButton jobOwnerBtn = new JButton("Job Owner (Client)");

        vehicleOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jobOwnerBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        buttonPanel.add(vehicleOwnerBtn);
        buttonPanel.add(jobOwnerBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(panel);

        // Button Actions
        vehicleOwnerBtn.addActionListener(e -> {
            frame.dispose();          // close main menu
            new VehicleOwnerFrame();  // open owner screen
        });

        jobOwnerBtn.addActionListener(e -> {
            frame.dispose();          // close main menu
            new JobOwnerFrame();      // open client screen
        });

        frame.dispose();
        frame.setVisible(true);
    }
}