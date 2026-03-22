import java.util.ArrayList;
import java.util.List;


/*=====================
Class JobOwner Logic - Kendra + Jaden
======================*/

//----M4 Implementation: Job Owner manages jobs they submit, track or cancel -----
public class JobOwner{
	
	//Protected Attributes based on user class inheritance to access these fields
	protected String clientID;
	protected String clientName;
	protected List<Job> jobSubmitted;
	
	//Constructor:
	public JobOwner(String clientID, String clientName) {
		this.clientID = clientID;
		this.clientName = clientName;
		this.jobSubmitted = new ArrayList<>();
	}
	
	//(Methods uses (Job j) that calls to Job class)

	//Submit client jobs
	public void submitJob(Job j) {
		jobSubmitted.add(j);
		System.out.println("Job submitted: " + j.getJobName());
	}
	
	//Track current jobs that are submitted
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
	
	//Getters
	public String getClientID() {
		return clientID;
	}
	public String getClientName() {
		return clientName;
	}
	public List<Job> getJobSubmitted(){
		return jobSubmitted;
	}
}
