import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*=====================
Class Job Logic - Kendra
======================*/

// Job class contain client's job data
public class Job {
	
	//Enum 
	public enum JobStatus {
	    PENDING,
	    IN_PROGRESS,
	    COMPLETED,
	    FAILED
	}

	//Attributes:
	private String jobID; //unique identifier
	private String jobName;	//job name description
	private Duration duration;	//how much time job will take to finish
	private LocalDateTime deadline;	//precise time job will finish
	private int redundancyLevel;	//how many vehicles are connected to one job
	private JobStatus progressStatus; //nested enum is used shows current status
	private List<Checkpoint> checkpoints; //stores many snapshot checkpoints one job may have
	private List<Vehicle> assignedVehicles;
	
	private long completionTime; //in minutes to store computed time FIFO results
	
	//Constructor:
	public Job(String jobID, String jobName, Duration duration, LocalDateTime deadline, int redundancyLevel) {
		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.redundancyLevel = redundancyLevel;
		this.progressStatus = JobStatus.PENDING;
		this.checkpoints = new ArrayList<>();
		this.assignedVehicles = new ArrayList<>();
		this.completionTime = 0;
	}
	
	//(Methods uses (checkpointID for new created checkpoint at vehicleID to be added to checkpoint class) and (Checkpoint cp) that calls Checkpoint Class)
	
	//Mark job as completed using jobStatus enums
	public void markCompleted() {
		this.progressStatus = JobStatus.COMPLETED;
		System.out.println("Job" + jobName + "completed");
	}
	
    //Method to track which vehicles are being assigned
    public void assignVehicles(List<Vehicle> vehicles) {
    	if(vehicles !=null) {
    		this.assignedVehicles.addAll(vehicles);
    	}
    }

	//Create checkpoint for a particular job id, given vehicle ID and convert enum to string
	public void createCheckpoint(String checkpointID, String vehicleID) {
		Checkpoint cp = new Checkpoint(checkpointID, this.jobID, vehicleID, this.progressStatus.name());
		checkpoints.add(cp);
		System.out.println("Job "+ jobID + ": Creating Checkpoint: " + checkpointID + "For Vehicle: " + vehicleID);
		cp.saveState(); //also simulate saving process internally
		
	}
	
	//Restore job from a given checkpoint if progress state has failed
	public void restoreFromCheckpoint(Checkpoint cp) {
		if (checkpoints.contains(cp)) {
			System.out.println("Restore Job" + jobID + "to checkpoint:" + cp.getProgressState());
			this.progressStatus = JobStatus.valueOf(cp.getProgressState());
			cp.loadState(); //simulate loading process
		}else {
			System.out.println("Checkpoint not found for job: "+ jobID);
		}
	}
	
	//Remove checkpoints if needed
	public void deleteCheckpoint(Checkpoint cp) {
		if (checkpoints.remove(cp)) {
			System.out.println("Job "+ jobID + ": Deleting Checkpoint " + cp.getCheckpointID());
			cp.deleteCheckpoint();
		}else {
			System.out.println("Checkpoint " + cp.getCheckpointID()+ "not found in Job: " + jobID);
		}
	}
	
	//Set completion time for FIFO for VC Controller
	public void setCompletionTime (long completionTime) {
		this.completionTime = completionTime;
	}
	public void setProgressStatus(JobStatus status) { 
		this.progressStatus = status; 
	}
	public void setRedundancyLevel(int level) { 
		this.redundancyLevel = level; 
	}
	
	//Getters
	public String getJobID() {
		return jobID;
	}
	public String getJobName() {
		return jobName;
	}
	public Duration getDuration() {
		return duration;
	}
	public LocalDateTime getDeadline() {
		return deadline;
	}
	public int getRedundancyLevel() {
		return redundancyLevel;
	}
	public JobStatus getProgressStatus() {
		return progressStatus;
	}
	public List<Checkpoint> getCheckpoints(){
		return checkpoints;
	}
	public List<Vehicle> getAssignedVehicles() { 
		return assignedVehicles; 
	}
	public long getCompletionTime() {
		return completionTime;
	}
	
	//override method from super class or implement method from interface to avoid mistakes if methods don't match
	 @Override
	    public String toString() {
	        return "Job{" +
	                "jobID='" + jobID + '\'' +
	                ", jobName='" + jobName + '\'' +
	                ", duration=" + duration +
	                ", deadline=" + deadline +
	                ", redundancyLevel=" + redundancyLevel +
	                ", progressStatus=" + progressStatus +
	                ", checkpoints=" + checkpoints.size()+
	                '}';
	    }
}
