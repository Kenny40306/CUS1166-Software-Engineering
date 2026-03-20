import javax.swing.*;
import java.awt.*;
import java.util.List;

//Main Frame 
public class MainControllerFrame extends JFrame{
  
    //Output area for back end code and VCController reference
    private JTextArea outputArea;
    private VCController vcController;
    
    public MainControllerFrame(VCController vcController, JFrame jobOwnerFrame) {
    	this.vcController = vcController;
    	
        setTitle("VCRTS - Main Controller Unit");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        //position next to job owner frame 
        if(jobOwnerFrame !=null) {
            Point loc = jobOwnerFrame.getLocation();
            int x = loc.x + jobOwnerFrame.getWidth() + 10; // 10px gap to the right
            int y = loc.y;
            setLocation(x, y);
        }else {
        	setLocationRelativeTo(null); //sets it to center
        }
        
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        UIStyling.stylePanel(mainPanel);
        
        JLabel title = new JLabel("Main Controller Output", SwingConstants.CENTER);
        UIStyling.styleLabel(title);
        title.setFont(new Font("Georgia", Font.BOLD,22));
        add(title, BorderLayout.NORTH);
        mainPanel.add(title, BorderLayout.NORTH);

        outputArea = new JTextArea(15,50);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        
        //Connects to back end code
        vcController.setOutputArea(outputArea);

        //shows existing jobs immediately
        displayCurrentJobs();

        setVisible(true);
    }
    
    //Methods that are displayed:
    
    //Display Current Active Job
    private void displayCurrentJobs() {
        outputArea.append("===== Current Jobs =====\n");

        if(vcController.getActiveJobs().isEmpty()) {
        	outputArea.append("No active jobs at the moment.\n");
        }else {
        	for (Job j : vcController.getActiveJobs()) {
        		outputArea.append("Job: " + j.getJobName() + "\n");
        }
    }
        outputArea.append("======================\n");
    }
    //More Methods:
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //List all vehicles-
    
    //Time estimations Actual FIFO Calculation-
    public void displayCompletionTimes() {
        List<Job> jobs = vcController.getActiveJobs();
        if (jobs.isEmpty()) {
            outputArea.append("\nNo jobs to calculate completion times.\n");
            return;
        }

        List<Long> completionTimes = vcController.calculateCompletionTimes();
        outputArea.append("\n=== FIFO Completion Times ===\n");

        int size = Math.min(jobs.size(), completionTimes.size());
        for (int i = 0; i < size; i++) {
            Job j = jobs.get(i);
            Long time = completionTimes.get(i);
            outputArea.append("Job: " + j.getJobName() +
                    " | Completion Time: " + time + " min\n");
        }
        outputArea.append("============================\n");
    }
}

 
