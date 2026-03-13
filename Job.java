import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*=====================
Class Job  - Kendra
======================*/

public class Job {
	
	enum JobStatus {
	    PENDING,
	    IN_PROGRESS,
	    COMPLETED,
	    Failed
	}

	private String jobID;
	private String jobName;
	private Duration duration;
	private LocalDateTime deadline;
	private int redundancyLevel;
	private JobStatus progressStatus; //nested enum is used
	private List<Checkpoint> checkpoints = new ArrayList<>();
	
	public Job(String jobID, String jobName, Duration duration, LocalDateTime deadline, int redundancyLevel) {
		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.redundancyLevel = redundancyLevel;
		this.progressStatus = JobStatus.PENDING;
	}
	
	public void markCompleted() {
		this.progressStatus = JobStatus.COMPLETED;
	}
	
	public void createCheckpoint(String checkpointID, String vehicleID) {
		Checkpoint cp = new Checkpoint(checkpointID, this.jobID, vehicleID, this.progressStatus.name());
		checkpoints.add(cp);
		cp.saveState();
		
	}
	
	public void restoreFromCheckpoint(Checkpoint cp) {
		if (checkpoints.contains(cp)) {
			System.out.println("Restore Job" + jobID + "to checkpoint:" + cp.getProgressState());
			this.progressStatus = JobStatus.valueOf(cp.getProgressState());
			cp.loadState();
		}else {
			System.out.println("Checkpoint not found for job "+ jobID);
		}
	}
	public void deleteCheckpoint(Checkpoint cp) {
		if (checkpoints.remove(cp)) {
			cp.deleteCheckpoint();
		}
	}
	
	//Getters
	public String getJobID() {
		return jobID;
	}
	public String getJobName() {
		return jobName;
	}
	public JobStatus getProgressStatus() {
		return progressStatus;
	}
	public List<Checkpoint> getCheckpoints(){
		return checkpoints;
	}

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
