import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.util.List;

//NEW - Moontarin
//This class creates a GUI window that displays server-related information
//such as logs, jobs, users, and system status using tabbed panels.

public class ServerFrame extends JFrame {

	 // Text areas for displaying different categories of server data
    private JTextArea logArea;
    private JTextArea jobsArea;
    private JTextArea vehiclesArea;
    private JTextArea usersArea;
    private JTextArea systemArea;

    // Constructor that builds the GUI and optionally positions it relative to another frame
    public ServerFrame(JFrame referenceFrame) { //Called in VCCOntroller
        setTitle("Server Console Frame");
        setSize(340, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabbed pane to organize different sections (Logs, Jobs, Users, Systems)
        JTabbedPane tabs = new JTabbedPane();
        
        // Make tabs smaller
        Font tabFont = new Font("Arial", Font.PLAIN, 10);
        tabs.setFont(tabFont);
        UIManager.put("TabbedPane.tabInsets", new Insets(2, 5, 2, 5));

        
        // ===== LOGS =====
        logArea = createArea();
        tabs.addTab("Global Notifs", createStyledScrollPane(logArea));

        // ===== JOBS =====
        jobsArea = createArea();
        tabs.addTab("Jobs", createStyledScrollPane(jobsArea));

        // ===== Vehicles =====
        vehiclesArea = createArea();
        tabs.addTab("Vehicle", createStyledScrollPane(vehiclesArea));
        
        // ===== USERS =====
        usersArea = createArea();
        tabs.addTab("Users",createStyledScrollPane(usersArea));

        // ===== Systems =====
        systemArea = createArea();
        tabs.addTab("Systems", createStyledScrollPane(systemArea));

        
        // Add tabbed pane to the center of the frame
        add(tabs, BorderLayout.CENTER);

        // Position next to reference frame if given
        if (referenceFrame != null) {
            int x = referenceFrame.getX() - getWidth();
            int y = referenceFrame.getY();
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }

        setVisible(true);
    }

    // Helper method to create a styled, non-editable text area
    private JTextArea createArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return area;
    }

    // Apply custom scroll styling
    private JScrollPane createStyledScrollPane(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        UIStyling.styleScrollPaneCompact(scroll);

        return scroll;
    }
    
    // ================= UPDATE METHODS =================
    
    //---- Logs ---- // Replace all logs with a new list of logs
    public void updateLogs(List<String> logs) {
        logArea.setText("");
        for (String log : logs) {
            logArea.append(log + "\n");
        }
    }

    // --- Append single log ---  //auto-scroll to the bottom
    public void appendLog(String log) {
        logArea.append(log + "\n");
     // Move caret to the end to ensure auto-scrolling
        logArea.setCaretPosition(logArea.getDocument().getLength()); // auto-scroll
    }
    
    // ---- Jobs ----    // Update jobs table display with formatted header and job entries
    public void updateJobs(List<String> jobs) {
        jobsArea.setText("JobID | Owner | Type | Status\n");
        jobsArea.append("---------------------------------\n");

        for (String j : jobs) {
            jobsArea.append(j + "\n");
        }
    }

    // ---- Vehicles ----     // Update vehicles table display with formatted header and job entries
    public void updateVehicles(List<String> vehicles) {
        vehiclesArea.setText("VehicleID | Owner | Type | Status\n");
        vehiclesArea.append("---------------------------------\n");

        for (String v : vehicles) {
        	vehiclesArea.append(v + "\n");
        }
    }

    // ---- Users ---- // Update users table display with formatted header and user entries
    public void updateUsers(List<String> users) {
        usersArea.setText("UserID | Role | Status\n");
        usersArea.append("---------------------------------\n");

        for (String u : users) {
            usersArea.append(u + "\n");
        }
    }
    // ---- System ---- // Update system status display with a list of system messages/info
    public void updateSystem(List<String> system) {
        systemArea.setText("");
        system.forEach(s -> systemArea.append(s + "\n"));
    }
}