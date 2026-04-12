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

        JPanel pendingTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        UIStyling.styleDashboardPanel(pendingTitlePanel);
        pendingTitlePanel.add(pendingTitle);

        // ================= REQUEST PANEL =================
        requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        UIStyling.styleDashboardPanel(requestPanel);

        JScrollPane requestScroll = new JScrollPane(requestPanel);
        UIStyling.styleScrollPaneDark(requestScroll);
        
        JPanel requestContainer = new JPanel(new BorderLayout(8, 9));
        UIStyling.styleDashboardPanel(requestContainer);
        requestContainer.add(pendingTitlePanel, BorderLayout.NORTH);
        requestContainer.add(requestScroll, BorderLayout.CENTER);

        center.add(requestContainer, BorderLayout.CENTER);

        // ================= FIFO TITLE (LEFT + SMALL) =================
       
        JLabel fifoTitle = new JLabel("Calculate FIFO");
        fifoTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        fifoTitle.setForeground(UIStyling.ACCENT);

        JPanel fifoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        UIStyling.styleScrollPaneDark(fifoScroll);

        fifoPanel.add(calcButton, BorderLayout.NORTH);
        fifoPanel.add(fifoScroll, BorderLayout.CENTER);

        JPanel fifoContainer = new JPanel(new BorderLayout(5, 5));
        UIStyling.styleDashboardPanel(fifoContainer);
        fifoContainer.add(fifoTitlePanel, BorderLayout.NORTH);
        fifoContainer.add(fifoPanel, BorderLayout.CENTER);

        center.add(fifoContainer, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        refreshRequests();
        calcButton.addActionListener(e -> runFIFO());

        setVisible(true);
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
        long deadlineMin = Duration.between(
                LocalDateTime.now(),
                req.job.getDeadline()
        ).toMinutes();


        JLabel line1 = new JLabel("JOB | " + req.client + " Requested");
        JLabel line2 = new JLabel("ID: " + req.job.getJobID());
        JLabel line3 = new JLabel("Name: " + req.job.getJobName());
      
        JLabel line4 = new JLabel("Duration: " + durationMin + " min");
        JLabel line5 = new JLabel("Deadline: " + deadlineMin  + " min");
        
        line1.setForeground(UIStyling.TEXT);
        line2.setForeground(UIStyling.TEXT);
        line3.setForeground(UIStyling.TEXT);
        line4.setForeground(UIStyling.TEXT);
        line5.setForeground(UIStyling.TEXT);

        textPanel.add(line1);
        textPanel.add(line2);
        textPanel.add(line3);
        textPanel.add(line4);
        textPanel.add(line5);
        
        JLabel separator = new JLabel("---------------------------------------");
        separator.setForeground(UIStyling.TEXT);
        textPanel.add(separator);
       
        Color approveColor = new Color(127, 140, 141);        // grey
        Color hoverApproveColor = new Color(41, 128, 185);	//hover is blue

        Color rejectColor = new Color(127, 140, 141);          // grey
        Color hoverRejectColor = new Color(231, 76, 60);	//hover is red
        
        JButton approve = UIStyling.createIconButton("✓", approveColor, hoverApproveColor);
        JButton reject  = UIStyling.createIconButton("X", rejectColor, hoverRejectColor);
        
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
        JLabel line2 = new JLabel("ID: " + req.vehicle.getVehicleID());
        JLabel line3 = new JLabel("Year Made: " + req.vehicle.getYearMade());
        JLabel line4 = new JLabel("Name: " + req.vehicle.getVehicleName());
        JLabel line5 = new JLabel("Residency Time: " + req.vehicle.getResidencyDisplay());

        line1.setForeground(UIStyling.TEXT);
        line2.setForeground(UIStyling.TEXT);
        line3.setForeground(UIStyling.TEXT);
        line4.setForeground(UIStyling.TEXT);
        line5.setForeground(UIStyling.TEXT);

        textPanel.add(line1);
        textPanel.add(line2);
        textPanel.add(line3);
        textPanel.add(line4);
        textPanel.add(line5);

        JLabel separator = new JLabel("---------------------------------------");
        separator.setForeground(UIStyling.TEXT);
        textPanel.add(separator);
        
        Color approveColor = new Color(127, 140, 141);        // grey
        Color hoverApproveColor = new Color(41, 128, 185);	//hover is blue

        Color rejectColor = new Color(127, 140, 141);          // grey
        Color hoverRejectColor = new Color(231, 76, 60);	//hover is red
        
        JButton approve = UIStyling.createIconButton("✓", approveColor, hoverApproveColor);
        JButton reject  = UIStyling.createIconButton("X", rejectColor, hoverRejectColor);
        
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
    // FIFO LOGIC
    // =========================================================
    private void runFIFO() {

        fifoOutput.setText("");

        List<Long> times = vcController.calculateCompletionTimes();
        List<Job> batch = vcController.getCurrentBatch();

        if (times.isEmpty()) {
            fifoOutput.append("No jobs to calculate.\n");
            return;
        }

        fifoOutput.append("===== FIFO RESULTS =====\n\n");

        for (int i = 0; i < batch.size(); i++) {

            Job j = batch.get(i);

            fifoOutput.append(
                    "Job: " + j.getJobName() + "\n" +
                    "ID: " + j.getJobID() + "\n" +
                    "Duration: " + j.getDuration().toMinutes() + " min\n" +
                    "Completion: " + times.get(i) + " min\n\n"
            );
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
