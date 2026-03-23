import javax.swing.*;
import java.awt.*;
import java.util.List;

/*=====================
Main Controller Frame - Moontarin + Subat
======================*/

//---- M4 Implementation: main frame for VCController to show output on dash board ----

//Main Frame 
public class MainControllerFrame extends JFrame{
  
    //Output area for back end code and VCController reference
    private JTextArea outputArea;
    private VCController vcController;
    
    public MainControllerFrame(VCController vcController, JFrame jobOwnerFrame) {
    	this.vcController = vcController;
    	
        setTitle("MC Dashboard");
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

        setVisible(true);
    }
    
    //Methods that are displayed:
    
    public void clearOutput() {
        outputArea.setText(""); // clears all previous logs
    }
        
    //Display all Active Jobs submitted by clients
    public void displayCurrentJobs() {
        outputArea.append("===== Jobs Log =============\n");

        List<Job> currentBatch = vcController.getCurrentBatch(); //all jobs ever submitted as history log
        if (currentBatch.isEmpty()) {
            outputArea.append("No active jobs at the moment.\n");
        } else {
            for (Job j : currentBatch) {
                if (j.getProgressStatus() != Job.JobStatus.COMPLETED) {
                    outputArea.append("Job: " + j.getJobName() + 
                    		" | Status: " + j.getProgressStatus() + "\n");
                }
            }
        }
        outputArea.append("================================\n\n");
    }
        
    //Display estimations Actual FIFO Calculation for job completion-
    public void displayCompletionTimes() {
    	 outputArea.append("===== FIFO Completion Times ===================\n");
    	
    	 List<Long> completionTimes = vcController.calculateCompletionTimes();

    	 if (completionTimes.isEmpty()) {
    	        outputArea.append("No jobs to calculate completion times.\n");
    	        outputArea.append("===============================================\n\n");
    	        return;
    	    }

    	    List<Job> batch = vcController.getCurrentBatch(); // only show the batch just calculated
    	    for (int i = 0; i < batch.size(); i++) {
    	        Job j = batch.get(i);
    	        Long time = completionTimes.get(i);
    	        long durationMin = j.getDuration().toMinutes();
    	        outputArea.append("Job: " + j.getJobName() + 
    	        		" | JobID: " + j.getJobID() +
    	        		" | Duration: " + durationMin + " min" +
    	                " | Completion Time: " + time + " min\n");
    	    }
    	    outputArea.append("===============================================\n\n");	    
    }
    
    //Queue - List of all jobs waiting
    public void displayQueue() {
        outputArea.append("===== Job Queue =====\n");
 
        List<Job> currentBatch = vcController.getCurrentBatch();
        if (currentBatch.isEmpty()) {
            outputArea.append("No jobs in queue.\n");
        } else {
            int position = 1;
            for (Job j : currentBatch) {
                if (j.getProgressStatus() != Job.JobStatus.COMPLETED) {
                    outputArea.append(position + ". " + j.getJobName() +
                            " | ID: " + j.getJobID() + "\n");
                    position++;
                }
            }
        }
        outputArea.append("=====================\n\n");
    }
    
    //Server Status - shows central status of server, jobs in storage, completed job count,
    public void displayServerStatus() {
        outputArea.append("===== Server Status =====\n");
 
        Server server = vcController.getServerConnection(); // TODO: make sure getServerConnection() exists in VCController
        if (server == null) {
            outputArea.append("No server connected.\n");
        } else {
            outputArea.append("Server ID: " + server.getServerID() + "\n");
            outputArea.append("Status: " + server.getStatus() + "\n");
            outputArea.append("Jobs in Storage: " + server.getStorage().size() + "\n");
            outputArea.append("Completed Jobs : " + server.getCompletedJobs().size() + "\n");
        }
        outputArea.append("=========================\n\n");
    }
    
//More Methods Eventually:
    
    //Current Job Submissions from client- with client Name, job name, time stamp
    
    //Vehicle Monitoring - List of all vehicles with id, status, current job, compute power
    
    //Checkpoint activity - number of checkpoints per job, last checkpoint time, which vehicle created it
    
    //Redundancy Tracking - shows required vs assigned vehicles
    
    //Alert - vehicle departing, job failed, checkpoint created, job reassigned

    //Overall System Performance - average completion time, jobs completed per minute, percentage vehicle utilization

}

 
