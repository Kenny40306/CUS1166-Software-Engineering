import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.util.List;

//NEW
public class ServerFrame extends JFrame {

    private JTextArea logArea;
    private JTextArea jobsArea;
    private JTextArea usersArea;
    private JTextArea systemArea;

    public ServerFrame(JFrame referenceFrame) {
        setTitle("Server Console Frame");
        setSize(340, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // ===== LOGS =====
        logArea = createArea();
        tabs.addTab("Global Notifs", new JScrollPane(logArea));

        // ===== JOBS =====
        jobsArea = createArea();
        tabs.addTab("Jobs", new JScrollPane(jobsArea));

        // ===== USERS =====
        usersArea = createArea();
        tabs.addTab("Users", new JScrollPane(usersArea));

        // ===== Systems =====
        systemArea = createArea();
        tabs.addTab("Systems", new JScrollPane(systemArea));

        add(tabs, BorderLayout.CENTER);

        // Position next to reference frame if given
        if (referenceFrame != null) {
            int x = referenceFrame.getX() - getWidth();
            int y = referenceFrame.getY();
            setLocation(x, y);
        } else {
            setLocation(50, 100);
        }

        setVisible(true);
    }

    private JTextArea createArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return area;
    }

    // ================= UPDATE METHODS =================
    
    public void updateLogs(List<String> logs) {
        logArea.setText("");
        for (String log : logs) {
            logArea.append(log + "\n");
        }
    }

    // --- Append single log ---
    public void appendLog(String log) {
        logArea.append(log + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // auto-scroll
    }
    
    public void updateJobs(List<String> jobs) {
        jobsArea.setText("JobID | Owner | Type | Status\n");
        jobsArea.append("---------------------------------\n");

        for (String j : jobs) {
            jobsArea.append(j + "\n");
        }
    }

    public void updateUsers(List<String> users) {
        usersArea.setText("UserID | Role | Status\n");
        usersArea.append("---------------------------------\n");

        for (String u : users) {
            usersArea.append(u + "\n");
        }
    }
    
    public void updateSystem(List<String> system) {
        systemArea.setText("");
        system.forEach(s -> systemArea.append(s + "\n"));
    }
}