import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;


/*=====================
Class JobOwner Logic - Kendra + Jaden
======================*/

//----M4 Implementation: Job Owner manages jobs they submit, track or cancel -----
public class JobOwner extends User implements ClientInterface{
	
private List<Job> jobSubmitted;

	//Constructor:
	public JobOwner(String userID, String userName, String email, String password) {
	    super(userID, userName, email, "JobOwner", password);
	    this.jobSubmitted = new ArrayList<>();
	}

	//(Methods uses (Job j) that calls to Job class)

	//==================================================================================================
	//Kendra Wrote this-
	//M5 Implementation: ====== Send job to VCController (asynchronous) ========

	//This method goes to JobOwnerFrame M4 Implementation uses threading
	public void submitJobToController(Job j, VCController vc) { 
        System.out.println("[JobOwner] Sending job request: " + j.getJobName());
        j.setClientId(super.userID); //ensure clientID matches current userID for notification system
        jobSubmitted.add(j);//track job
        new Thread(() -> vc.receiveJobRequest(j, this)).start(); // async
    }
	
   // Notification from GUI and VCController
    @Override
    public void notify(String message) {
        System.out.println("[JobOwner Notification] " + message);
    
    }
    @Override
    public void sendDataToServer(VCController controller, Object data) {
        if (data instanceof Job) {
            System.out.println("[JobOwner] Sending job " + ((Job) data).getJobName() + " to server...");
            controller.receiveJobRequest((Job) data, this);
        }
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
