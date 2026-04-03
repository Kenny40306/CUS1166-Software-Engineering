import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.util.List;

// NEW - Moontarin
// This class creates a GUI window that displays server-related information
// such as logs, jobs, users, and system status using tabbed panels.
public class ServerFrame extends JFrame {

    // Text areas for displaying different categories of server data
    private JTextArea logArea;
    private JTextArea jobsArea;
    private JTextArea usersArea;
    private JTextArea systemArea;

    // Constructor that builds the GUI and optionally positions it relative to another frame
    public ServerFrame(JFrame referenceFrame) {
        setTitle("Server Console Frame");              
        setSize(340, 400);                             
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());                 

        // Tabbed pane to organize different sections (Logs, Jobs, Users, Systems)
        JTabbedPane tabs = new JTabbedPane();

        // ===== LOGS TAB =====
        logArea = createArea();                        
        tabs.addTab("Global Notifs", new JScrollPane(logArea)); 

        // ===== JOBS TAB =====
        jobsArea = createArea();
        tabs.addTab("Jobs", new JScrollPane(jobsArea));

        // ===== USERS TAB =====
        usersArea = createArea();
        tabs.addTab("Users", new JScrollPane(usersArea));

        // ===== SYSTEM TAB =====
        systemArea = createArea();
        tabs.addTab("Systems", new JScrollPane(systemArea));

        // Add tabbed pane to the center of the frame
        add(tabs, BorderLayout.CENTER);

        // Position this frame relative to another frame if provided
        if (referenceFrame != null) {
            int x = referenceFrame.getX() - getWidth(); 
            int y = referenceFrame.getY();              
        } else {
            // Default position if no reference frame is given
            setLocation(50, 100);
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

    // ================= UPDATE METHODS =================

    // Replace all logs with a new list of logs
    public void updateLogs(List<String> logs) {
        logArea.setText("");                          
        for (String log : logs) {
            logArea.append(log + "\n");              
        }
    }

    // Append a single log entry and auto-scroll to the bottom
    public void appendLog(String log) {
        logArea.append(log + "\n");
        // Move caret to the end to ensure auto-scrolling
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Update jobs table display with formatted header and job entries
    public void updateJobs(List<String> jobs) {
        jobsArea.setText("JobID | Owner | Type | Status\n"); 
        jobsArea.append("---------------------------------\n");

        for (String j : jobs) {
            jobsArea.append(j + "\n");                
        }
    }

    // Update users table display with formatted header and user entries
    public void updateUsers(List<String> users) {
        usersArea.setText("UserID | Role | Status\n"); 
        usersArea.append("---------------------------------\n");

        for (String u : users) {
            usersArea.append(u + "\n");              
        }
    }

    // Update system status display with a list of system messages/info
    public void updateSystem(List<String> system) {
        systemArea.setText("");                       
        system.forEach(s -> systemArea.append(s + "\n")); 
    }
}