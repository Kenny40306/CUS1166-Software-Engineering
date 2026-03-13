import java.util.ArrayList;
import java.util.List;


/*=====================
Class JobOwner  - domain logic for client
======================*/

public class JobOwner extends User{
	
	private List<Job> jobSubmitted = new ArrayList<>();
	
	public JobOwner(String userID, String name, String role, String email, String password) {
		super(userID, name, email, "JobOwner", password);
	}

	public void submitJob(Job j) {
		jobSubmitted.add(j);
		System.out.println("Job submitted: " + j.getJobName());
	}
	
	public void trackJobStatus(Job j) {
		System.out.println("Job" + j.getJobName() + "Status: " + j.getProgressStatus());
	}
	
	public void cancelJob(Job j) {
		if (jobSubmitted.remove(j)) {
			System.out.println("Job " + j.getJobName() + " canceled successfully");
		}else {
			System.out.println("Job not found");
		}
	}
	
	
	//Getters
	public List<Job> getJobSubmitted(){
		return jobSubmitted;
	}
}
