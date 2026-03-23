import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*=====================
Class Job Logic - Kendra
======================*/

//----M4 Implementation: Job class contain client's submitted job data------
public class Job {
	
	//Enum : define possible job states
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
	private List<Vehicle> assignedVehicles; //stores vehicles working on job for resource allocation
	
	//M4 implementation in this class-
	private boolean completionTimeCalculated = false; //tracks if job has already been completed for FIFO calculation (avoid recalculating)
	private long completionTime; //in minutes to store computed time FIFO results
	
	//Constructor:
	public Job(String jobID, String jobName, Duration duration, LocalDateTime deadline, int redundancyLevel) {
		//Assigns correct data to objects (initialize properties)
		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.redundancyLevel = redundancyLevel;
		this.progressStatus = JobStatus.PENDING; //default state for every job
		this.checkpoints = new ArrayList<>();
		this.assignedVehicles = new ArrayList<>(); //(!!!)
		this.completionTime = 0; //default value
	}
	
	//(Methods uses (checkpointID for new created checkpoint at vehicleID to be added to checkpoint class) and (Checkpoint cp) that calls Checkpoint Class)
	
	//Mark job as completed using jobStatus enums
	public void markCompleted() {
		this.progressStatus = JobStatus.COMPLETED; //update status to complete
		System.out.println("Job" + jobName + "completed");
	}
	
    //Method to track which vehicles are being assigned (!!! only for job to be self aware of which vehices are being worked on)
    public void assignVehicles(List<Vehicle> vehicles) {
    	if(vehicles !=null) { //prevent null pointer errors
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
		if (checkpoints.contains(cp)) { //ensure checkpoint exists
			System.out.println("Restore Job" + jobID + "to checkpoint:" + cp.getProgressState());
			this.progressStatus = JobStatus.valueOf(cp.getProgressState()); //restore state
			cp.loadState(); //simulate restore
		}else {
			System.out.println("Checkpoint not found for job: "+ jobID);
		}
	}
	
	//Remove checkpoints if needed
	public void deleteCheckpoint(Checkpoint cp) {
		if (checkpoints.remove(cp)) {
			System.out.println("Job "+ jobID + ": Deleting Checkpoint " + cp.getCheckpointID());
			cp.deleteCheckpoint(); //simulate delete
		}else {
			System.out.println("Checkpoint " + cp.getCheckpointID()+ "not found in Job: " + jobID);
		}
	}
	
	//Set
	public void setProgressStatus(JobStatus status) { 
		this.progressStatus = status; 
	}
	//Set 
	public void setRedundancyLevel(int level) { 
		this.redundancyLevel = level; 
	}
	
	//Set completion time for FIFO for VC Controller
	public void setCompletionTime (long completionTime) {
		this.completionTime = completionTime;
	}
	//Tracks true or false for 
	public boolean isCompletionTimeCalculated() {
		return completionTimeCalculated;
	}
	//
	public void setCompletionTimeCalculated(boolean completionTimeCalculated) {
	    this.completionTimeCalculated = completionTimeCalculated;
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
