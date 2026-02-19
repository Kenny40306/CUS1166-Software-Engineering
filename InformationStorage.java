import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InformationStorage extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextArea notesArea;
    private JButton saveButton;

    public InformationStorage() {

        setTitle("Information Storage System");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        nameField = new JTextField();
        emailField = new JTextField();
        notesArea = new JTextArea(4, 20);
        saveButton = new JButton("Save Information");

        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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

    private void saveToFile() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String notes = notesArea.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required fields.");
            return;
        }

        try (FileWriter writer = new FileWriter("information.txt", true)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);

            writer.write("Timestamp: " + timestamp + "\n");
            writer.write("Name: " + name + "\n");
            writer.write("Email: " + email + "\n");
            writer.write("Notes: " + notes + "\n");
            writer.write("------------------------------------------------\n");

            JOptionPane.showMessageDialog(this, "Information saved successfully!");

            
            nameField.setText("");
            emailField.setText("");
            notesArea.setText("");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving information.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InformationStorage().setVisible(true);
            }
        });
    }
}
