import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;


/*=====================
Class JobOwner Logic - Kendra + Jaden
======================*/

//----M4 Implementation: Job Owner manages jobs they submit, track or cancel -----
//(ENTITY ONE)
public class JobOwner extends User{ //implements ClientInterface{
	
private List<Job> jobSubmitted;

	//Constructor:
	public JobOwner(String userID, String userName, String email, String password) {
	    super(userID, userName, email, "JobOwner", password);
	    this.jobSubmitted = new ArrayList<>();
	}

	//(Methods uses (Job j) that calls to Job class)

	//==================================================================================================
	//Kendra Wrote This-
	//M5 Implementation: ====== Send Job to VCController (asynchronous) ========

    //: Now uses socket instead of direct VCController call
    public void submitJobToServer(Job job) {
    	
    	//this makes sure the job belongs to the user
    	job.setClientId(this.userID);
    	//This tracks the submitted jobs
    	jobSubmitted.add(job);
    	
        new Thread(() -> {
            try {
                ClientConnection connection = new ClientConnection("localhost", 5001);
                MessageServer message = new MessageServer( MessageServer.Type.JOB_REQUEST,job,this.userID);
                String response = connection.send(message); // Send job and receive server response

                //Notify user with server decision locally
                notify("Server Response: " + response);

            } catch (Exception e) {
                notify("Error sending job: " + e.getMessage());
            }
        }).start();
    }

    //Local called notification for server responds
    public void notify(String message) {
        System.out.println("Notification for " + userID + ": " + message);
    }

    //=====================================================================================================
	
    
    
	//Track current jobs that are submitted and verify if job belongs to this user
	public void trackJobStatus(Job j) {
		if(jobSubmitted.contains(j)) {
			System.out.println("Job" + j.getJobName() + "Status: " + j.getProgressStatus());
		}else {
			System.out.println("Job not found");
		}
	}
	
	//Cancel any jobs that client doesn't want active anymore
	public void cancelJob(Job j) {
		if (jobSubmitted.remove(j)) {
			System.out.println("Job " + j.getJobName() + " canceled successfully");
		}else {
			System.out.println("Job not found");
		}
	}
	
	//Getters return to external class to access data
	public String getClientID() {
		return super.userID;
	}
	public String getClientName() {
		return super.userName;
	}
	public List<Job> getJobSubmitted(){
		return jobSubmitted;
	}
}
