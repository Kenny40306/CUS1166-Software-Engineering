import javax.swing.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class JobOwnerFrame extends JFrame {

    public JobOwnerFrame() {

        JFrame frame = new JFrame();

        JLabel clientIDLabel = new JLabel("Client ID:");
        JTextField clientIDField = new JTextField(10);

        JLabel jobNameLabel = new JLabel("Job Name:");
        JTextField jobNameField = new JTextField(10);

        JLabel durationLabel = new JLabel("Job Duration");
        JTextField durationField = new JTextField(10);

        JLabel deadlineLabel = new JLabel("Job Deadline");
        JTextField deadlineField = new JTextField(10);

        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");

        JPanel panel = new JPanel();
        panel.add(clientIDLabel);
        panel.add(clientIDField);
        panel.add(jobNameLabel);
        panel.add(jobNameField);
        panel.add(durationLabel);
        panel.add(durationField);
        panel.add(deadlineLabel);
        panel.add(deadlineField);
        panel.add(backButton);
        panel.add(submitButton);

        
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String clientID = clientIDField.getText();
                String jobName = jobNameField.getText();
                String durationText = durationField.getText();
                String deadlineText = deadlineField.getText();

                // Empty field validation
                if (clientID.isEmpty() || jobName.isEmpty() || durationText.isEmpty() || deadlineText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "All fields must be filled out!",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Save to file (append mode)
                try (FileWriter writer = new FileWriter("job_owner_data.txt", true)) {

                    writer.write("Timestamp: " + LocalDateTime.now() + "\n");
                    writer.write("Client ID: " + clientID + "\n");
                    writer.write("Job Name: " + jobName + "\n");
                    writer.write("Job Duration (minutes): " + durationText + "\n");
                    writer.write("Job Deadline (minutes): " + deadlineText + "\n");
                    writer.write("---------------------------------\n");

                    JOptionPane.showMessageDialog(frame,
                            "Job submitted successfully!");

                    // Clear fields after successful submit
                    clientIDField.setText("");
                    jobNameField.setText("");
                    durationField.setText("");
                    deadlineField.setText("");

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error saving file: " + ex.getMessage(),
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // =====================
        // Back Logic
        // =====================
        backButton.addActionListener(e -> {
            frame.dispose();
            VCRTSGUI.main(null); // return to main menu
        });

        // =====================
        // Styling (match VehicleOwnerFrame) - Moontarin
        // =====================

         // Set panel background
        panel.setBackground(new Color(0, 0, 128));
         
         // Font styling for labels
        Font labelFont = new Font("Georgia", Font.PLAIN, 16);
        clientIDLabel.setFont(labelFont); clientIDLabel.setForeground(Color.WHITE);
        jobNameLabel.setFont(labelFont); jobNameLabel.setForeground(Color.WHITE);
        durationLabel.setFont(labelFont); durationLabel.setForeground(Color.WHITE);
        deadlineLabel.setFont(labelFont); deadlineLabel.setForeground(Color.WHITE);

        // Button styling
        submitButton.setFont(new Font("Georgia", Font.BOLD, 14));
        backButton.setFont(new Font("Georgia", Font.BOLD, 14));

        // Title styling
        JLabel titleLabel = new JLabel("Job Owner Form", JLabel.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
         
        // Frame layout
        frame.setLayout(new BorderLayout());
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        
        frame.setSize(700, 500);
        frame.setTitle("Job Owner Information");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}