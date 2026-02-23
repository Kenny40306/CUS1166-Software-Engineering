import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InformationStorage extends JFrame {

    // Declares GUI components as variables
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea notesArea;
    private JButton saveButton;

    public InformationStorage() {

        // Sets window title and size
        setTitle("Information Storage System");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initializes input fields
        nameField = new JTextField();
        emailField = new JTextField();
        notesArea = new JTextArea(4, 20);
        saveButton = new JButton("Save Information");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Adds labels for different fields
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));
        panel.add(new JLabel(""));

        panel.add(saveButton);
        add(panel);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });
    }

    // Method that saves user input to a file
    private void saveToFile() {

        // Get and trim user input
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String notes = notesArea.getText().trim();

        // Sends a message if a field is empty
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required fields.");
            return;
        }

        try (FileWriter writer = new FileWriter("information.csv", true)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Gets current time
            String timestamp = LocalDateTime.now().format(formatter);

            writer.write("Timestamp: " + timestamp + "\n");
            writer.write("Name: " + name + "\n");
            writer.write("Email: " + email + "\n");
            writer.write("Notes: " + notes + "\n");
            writer.write("------------------------------------------------\n");

            JOptionPane.showMessageDialog(this, "Information saved successfully!");

            // Clears each field after saving
            nameField.setText("");
            emailField.setText("");
            notesArea.setText("");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving information.");
        }
    }

    public static void main(String[] args) {

        // Makes sure the GUI is created
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // Creates and displays the window
                new InformationStorage().setVisible(true);
            }
        });
    }
}