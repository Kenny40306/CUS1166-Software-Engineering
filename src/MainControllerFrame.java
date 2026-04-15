import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat + Kendra
======================*/

//---- M4 Implementation: main frame for VCController to show output on dashboard ----

//Main Frame (GUI window for displaying system output)
public class MainControllerFrame extends JFrame{
	
    private VCController vcController;

    private JPanel requestPanel;
    private JTextArea fifoOutput;
    private JButton editDbButton;
    
    public MainControllerFrame(VCController vcController) {
        this.vcController = vcController;

        setTitle("VC Controller Dashboard");
        setSize(340, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        UIStyling.styleFrameDark(this);

        // ================= TITLE =================
        JLabel title = new JLabel("VC Controller Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(UIStyling.ACCENT);

        JPanel titlePanel = new JPanel(new BorderLayout());
        UIStyling.styleDashboardPanel(titlePanel);
        titlePanel.add(title, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel center = new JPanel(new BorderLayout(10, 10));
        UIStyling.styleDashboardPanel(center);

        // ================= PENDING TITLE (LEFT + SMALL) =================
        JLabel pendingTitle = new JLabel("Pending Requests");
        pendingTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pendingTitle.setForeground(UIStyling.ACCENT);

        //M6 Create Edit Button
        editDbButton = new JButton("Edit DB");
        editDbButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        UIStyling.styleDashboardButton(editDbButton);
        
        
        // RIGHT side container for button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        UIStyling.styleDashboardPanel(rightPanel);
        rightPanel.add(editDbButton);

        // LEFT PANEL (ENSURES SAME ALIGNMENT AS FIFO)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        UIStyling.styleDashboardPanel(leftPanel);
        leftPanel.add(pendingTitle);
      
        // FULL HEADER PANEL (LEFT title + RIGHT button)
        JPanel pendingTitlePanel = new JPanel(new BorderLayout());
        UIStyling.styleDashboardPanel(pendingTitlePanel);
        pendingTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        pendingTitlePanel.add(pendingTitle, BorderLayout.WEST);
        pendingTitlePanel.add(rightPanel, BorderLayout.EAST);
        
        // ================= REQUEST PANEL =================        
        requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        UIStyling.styleDashboardPanel(requestPanel);

        JScrollPane requestScroll = new JScrollPane(requestPanel);
       // UIStyling.styleScrollPaneDark(requestScroll);
        UIStyling.styleScrollPaneCompact(requestScroll);
        
        JPanel requestContainer = new JPanel(new BorderLayout(8, 9));
        UIStyling.styleDashboardPanel(requestContainer);
        requestContainer.add(pendingTitlePanel, BorderLayout.NORTH);
        requestContainer.add(requestScroll, BorderLayout.CENTER);

        center.add(requestContainer, BorderLayout.CENTER);

        // ================= FIFO TITLE (LEFT + SMALL) =================
       
        JLabel fifoTitle = new JLabel("Calculate FIFO");
        fifoTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        fifoTitle.setForeground(UIStyling.ACCENT);

        JPanel fifoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        UIStyling.styleDashboardPanel(fifoTitlePanel);
        fifoTitlePanel.add(fifoTitle);

        // ================= FIFO PANEL =================
        JPanel fifoPanel = new JPanel(new BorderLayout(8, 8));
        UIStyling.styleDashboardPanel(fifoPanel);

        JButton calcButton = new JButton("Calculate");
        UIStyling.styleDashboardButton(calcButton);
        
        fifoOutput = new JTextArea(8, 40);
        fifoOutput.setEditable(false);
        UIStyling.styleTextAreaDark(fifoOutput);

        JScrollPane fifoScroll = new JScrollPane(fifoOutput);
        UIStyling.styleScrollPaneCompact(fifoScroll);

        fifoPanel.add(calcButton, BorderLayout.NORTH);
        fifoPanel.add(fifoScroll, BorderLayout.CENTER);

        JPanel fifoContainer = new JPanel(new BorderLayout(5, 5));
        UIStyling.styleDashboardPanel(fifoContainer);
       
        // FIX: balanced with request panel
        fifoContainer.setPreferredSize(new Dimension(200, 180));

        fifoContainer.add(fifoTitlePanel, BorderLayout.NORTH);
        fifoContainer.add(fifoPanel, BorderLayout.CENTER);

        center.add(fifoContainer, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        refreshRequests();
        
        calcButton.addActionListener(e -> runFIFO());
       
        //M6 Button Listener
        editDbButton.addActionListener(e -> {
            if (vcController != null) {
                openEditDialog();
            }
        });
       
    }

    // =========================================================
    // REFRESH REQUESTS
    // =========================================================
    private void refreshRequests() {
        requestPanel.removeAll();

        // combine both lists into one timeline-sorted list
        List<Object> allRequests = new ArrayList<>();

        allRequests.addAll(vcController.getPendingJobRequests());
        allRequests.addAll(vcController.getPendingVehicleRequests());

        // sort by requestID timestamp (nanoTime embedded at end)
        allRequests.sort((a, b) -> {

            long t1 = extractTime(a);
            long t2 = extractTime(b);

            return Long.compare(t1, t2);
        });

        //Correct UI Order
        for (Object req : allRequests) {

            if (req instanceof VCController.JobRequest jr) {
                requestPanel.add(createJobRow(jr));
            } else if (req instanceof VCController.VehicleRequest vr) {
                requestPanel.add(createVehicleRow(vr));
            }
        }

        //Refresh UI
        requestPanel.revalidate();
        requestPanel.repaint();
    }
    
    //Helper method that extract timestamp form requestID in VCController to sort requests
    private long extractTime(Object req) {

        String id;

        if (req instanceof VCController.JobRequest jr) {
            id = jr.requestID;
        } else {
            id = ((VCController.VehicleRequest) req).requestID;
        }

        // requestID format: client + "_" + nanoTime
        try {
            return Long.parseLong(id.split("_")[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    // =========================================================
    // JOB ROW (ALIGNED)
    // =========================================================
    private JPanel createJobRow(VCController.JobRequest req) {

        JPanel row = new JPanel(new GridBagLayout());
        UIStyling.styleDashboardPanel(row);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridy = 0;

        // ================= MULTI-LINE JOB INFO =================
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        UIStyling.styleDashboardPanel(textPanel);

        // user-friendly + system values
        long durationMin = req.job.getDuration().toMinutes();

        JLabel line1 = new JLabel("JOB | " + req.client + " Requested");
        JLabel line2 = new JLabel("Client ID: " + req.job.getClientID());
        JLabel line3 = new JLabel("Job ID: " + req.job.getJobID());
        JLabel line4 = new JLabel("Name: " +  req.job.getJobName());
        JLabel line5 = new JLabel("Duration: " + durationMin + " min");     
        JLabel line6 = new JLabel("Deadline: " + req.job.getDeadlineMinutes() + " min");
        
        line1.setForeground(UIStyling.TEXT);
        line2.setForeground(UIStyling.TEXT);
        line3.setForeground(UIStyling.TEXT);
        line4.setForeground(UIStyling.TEXT);
        line5.setForeground(UIStyling.TEXT);
        line6.setForeground(UIStyling.TEXT);

        textPanel.add(line1);
        textPanel.add(line2);
        textPanel.add(line3);
        textPanel.add(line4);
        textPanel.add(line5);
        textPanel.add(line6);
        
        JLabel separator = new JLabel("---------------------------------------");
        separator.setForeground(UIStyling.TEXT);
        textPanel.add(separator);
       
        Color approveColor = new Color(127, 140, 141);        // grey
        Color hoverApproveColor = new Color(41, 128, 185);	//hover is blue

        Color rejectColor = new Color(127, 140, 141);          // grey
        Color hoverRejectColor = new Color(231, 76, 60);	//hover is red
                
        JButton approve = UIStyling.createIconButton("\u2713", approveColor, hoverApproveColor); // "✓"
        JButton reject  = UIStyling.createIconButton("\u2715", rejectColor, hoverRejectColor); // "✕"

        Font symbolFont = new Font("Dialog", Font.BOLD, 11); //cross-platform safe for mac os and windows
        approve.setFont(symbolFont);
        reject.setFont(symbolFont);
        
        //Button Action Listeners
        approve.addActionListener(e -> {
            vcController.approveJob(req.job);
            refreshRequests();
        });

        reject.addActionListener(e -> {
            vcController.rejectJob(req.job);
            refreshRequests();
        });

        // LEFT label (expands)
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(textPanel, gbc);

        // APPROVE
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(approve, gbc);

        // REJECT
        gbc.gridx = 2;
        row.add(reject, gbc);

        return row;
    }

    // =========================================================
    // VEHICLE ROW (ALIGNED)
    // =========================================================
    private JPanel createVehicleRow(VCController.VehicleRequest req) {

        JPanel row = new JPanel(new GridBagLayout());
        UIStyling.styleDashboardPanel(row);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridy = 0;

        
     // ================= MULTI-LINE VEHICLE INFO =================
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        UIStyling.styleDashboardPanel(textPanel);
        
        JLabel line1 = new JLabel("VEHICLE | " + req.client + " Requested");
        JLabel line2 = new JLabel("Client ID: " + req.vehicle.getOwnerID());
        JLabel line3 = new JLabel("Vehicle ID: " + req.vehicle.getVehicleID());
        JLabel line4 = new JLabel("Year Made: " + req.vehicle.getYearMade());
        JLabel line5 = new JLabel("Name: " + req.vehicle.getVehicleName());
        JLabel line6 = new JLabel("Residency Time: " + req.vehicle.getResidencyDisplay());

        line1.setForeground(UIStyling.TEXT);
        line2.setForeground(UIStyling.TEXT);
        line3.setForeground(UIStyling.TEXT);
        line4.setForeground(UIStyling.TEXT);
        line5.setForeground(UIStyling.TEXT);
        line6.setForeground(UIStyling.TEXT);

        textPanel.add(line1);
        textPanel.add(line2);
        textPanel.add(line3);
        textPanel.add(line4);
        textPanel.add(line5);
        textPanel.add(line6);


        JLabel separator = new JLabel("---------------------------------------");
        separator.setForeground(UIStyling.TEXT);
        textPanel.add(separator);
        
        Color approveColor = new Color(127, 140, 141);        // grey
        Color hoverApproveColor = new Color(41, 128, 185);	//hover is blue

        Color rejectColor = new Color(127, 140, 141);          // grey
        Color hoverRejectColor = new Color(231, 76, 60);	//hover is red
        
        JButton approve = UIStyling.createIconButton("\u2713", approveColor, hoverApproveColor); // "✓"
        JButton reject  = UIStyling.createIconButton("\u2715", rejectColor, hoverRejectColor); // "✕"

        Font symbolFont = new Font("Dialog", Font.BOLD, 11); // cross-platform safe for mac os and windows
        approve.setFont(symbolFont);
        reject.setFont(symbolFont);
        
        //Button Action Listeners
        approve.addActionListener(e -> {
            vcController.approveVehicle(req.vehicle);
            refreshRequests();
        });

        reject.addActionListener(e -> {
            vcController.rejectVehicle(req.vehicle);
            refreshRequests();
        });

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(textPanel, gbc); 

        //Approve
        gbc.gridx = 1;
        gbc.weightx = 0;
        row.add(approve, gbc);

        //Reject
        gbc.gridx = 2;
        row.add(reject, gbc);

        return row;
    }
    
    
    
    // =========================================================
    // FIFO LOGIC DISPLAYED
    // =========================================================
    private void runFIFO() {

        fifoOutput.setText("");

        List<Long> times = vcController.calculateCompletionTimes();
        List<Job> batch = vcController.getCurrentBatch();

        if (times.isEmpty()) {
            fifoOutput.append("No jobs to calculate.\n");
            return;
        }

        fifoOutput.append("===== FIFO RESULTS =====\n");

        //New: M6 Change Avneet Worked On This: 
        long startTime = 0; 
      
        for (int i = 0; i < batch.size(); i++) {

            Job j = batch.get(i);

            fifoOutput.append(
                    "Order: " + (i + 1) + "\n" +
                    "Job: " + j.getJobName() + "\n" +
                    "ID: " + j.getJobID() + "\n" +
                    "Start Time: " + startTime + " min\n" +
                    "Duration: " + j.getDuration().toMinutes() + " min\n" +
                    "Completion: " + times.get(i) + " min\n\n"
            );

            startTime = times.get(i);
        }
    }
    
    
    
    
    //M6: New Database Edit Button Method ==================================================================================
    //Subat + Kendra + Jaden
   
    private void openEditDialog() {

        String[] options = {"Job", "Vehicle"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Which submission do you want to edit?",
                "Database Editor",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == -1) return;

        String id = JOptionPane.showInputDialog(this, "Enter ID:");
        if (id == null || id.trim().isEmpty()) return;

        id = id.trim();

     
        // ================= JOB EDIT =============================
        if (choice == 0) {

            // -------- PENDING JOB --------
            for (VCController.JobRequest jr : vcController.getPendingJobRequests()) {

                if (jr.job.getJobID().equals(id)) {

                	//LOCK CHECK ADDED
                    if (jr.job.isLocked()) {
                        JOptionPane.showMessageDialog(this,
                                "Job is LOCKED after FIFO calculation.");
                        return;
                    }
                    
                    // ===== INLINE EDIT UI =====
                    JTextField nameField = new JTextField(jr.job.getJobName());
                    JTextField durationField = new JTextField(String.valueOf(jr.job.getDuration().toMinutes()));
                    JTextField deadlineField = new JTextField(String.valueOf(jr.job.getDeadlineMinutes()));

                    Object[] fields = {
                            "Job Name:", nameField,
                            "Duration (min):", durationField,
                            "Deadline (min):", deadlineField
                    };

                    int result = JOptionPane.showConfirmDialog(
                            this,
                            fields,
                            "Edit Pending Job",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {

                        jr.job.setJobName(nameField.getText());
                        jr.job.setDuration(Duration.ofMinutes(Long.parseLong(durationField.getText())));
                        jr.job.setDeadlineMinutes(Long.parseLong(deadlineField.getText()));

                        vcController.adminUpdateJob(jr.job);
                        refreshRequests();
                    }
                    return;
                }
            }

            // -------- APPROVED JOB --------
            for (Job j : vcController.getActiveJobs()) {

                if (j.getJobID().equals(id)) {

                	// 🔒 LOCK CHECK ADDED
                    if (j.isLocked()) {
                        JOptionPane.showMessageDialog(this,
                                "Job is LOCKED after FIFO calculation.");
                        return;
                    }
                    
                    JTextField nameField = new JTextField(j.getJobName());
                    JTextField durationField = new JTextField(String.valueOf(j.getDuration().toMinutes()));
                    JTextField deadlineField = new JTextField(String.valueOf(j.getDeadlineMinutes()));

                    Object[] fields = {
                            "Job Name:", nameField,
                            "Duration (min):", durationField,
                            "Deadline (min):", deadlineField
                    };

                    int result = JOptionPane.showConfirmDialog(
                            this,
                            fields,
                            "Edit Approved Job",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {

                        j.setJobName(nameField.getText());
                        j.setDuration(Duration.ofMinutes(Long.parseLong(durationField.getText())));
                        j.setDeadlineMinutes(Long.parseLong(deadlineField.getText()));

                        vcController.adminUpdateJob(j);
                    }

                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Job ID not found.");
        }
        
        

        //Avneet + Moontarin + Ryan     
        // ================= VEHICLE EDIT =========================
        else {

            // -------- PENDING VEHICLE --------
            for (VCController.VehicleRequest vr : vcController.getPendingVehicleRequests()) {

                if (vr.vehicle.getVehicleID().equals(id)) {

                    JTextField nameField = new JTextField(vr.vehicle.getVehicleName());
                    JTextField yearField = new JTextField(String.valueOf(vr.vehicle.getYearMade()));
                    JTextField residencyField = new JTextField(vr.vehicle.getResidencyDisplay());

                    Object[] fields = {
                            "Vehicle Name:", nameField,
                            "Year Made:", yearField,
                            "Residency:", residencyField
                    };

                    int result = JOptionPane.showConfirmDialog(
                            this,
                            fields,
                            "Edit Pending Vehicle",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {

                        vr.vehicle.setVehicleName(nameField.getText());
                        vr.vehicle.setYearMade(Integer.parseInt(yearField.getText()));
                        vr.vehicle.setResidencyDisplay(residencyField.getText());

                        vcController.adminUpdateVehicle(vr.vehicle);
                        refreshRequests();
                    }

                    return;
                }
            }

            // -------- APPROVED VEHICLE --------
            for (Vehicle v : vcController.getConnectedVehicles()) {

                if (v.getVehicleID().equals(id)) {

                    JTextField nameField = new JTextField(v.getVehicleName());
                    JTextField yearField = new JTextField(String.valueOf(v.getYearMade()));
                    JTextField residencyField = new JTextField(v.getResidencyDisplay());

                    Object[] fields = {
                            "Vehicle Name:", nameField,
                            "Year Made:", yearField,
                            "Residency:", residencyField
                    };

                    int result = JOptionPane.showConfirmDialog(
                            this,
                            fields,
                            "Edit Approved Vehicle",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {

                        v.setVehicleName(nameField.getText());
                        v.setYearMade(Integer.parseInt(yearField.getText()));
                        v.setResidencyDisplay(residencyField.getText());

                        vcController.adminUpdateVehicle(v);
                    }

                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Vehicle ID not found.");
        }
    }
}
	    
    //=====================
    // FUTURE METHODS (NOT IMPLEMENTED YET)
    //=====================
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //Redundancy Tracking - shows required vs assigned vehicles
    
    //(Maybe) Alert system → vehicle departure, job failure, checkpoints, reassignment

    //Overall System Performance → avg completion time, jobs/min, vehicle utilization
