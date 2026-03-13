
import java.time.LocalDateTime;

class Checkpoint{
	
	private String checkpointID;
	private String jobID;
	private String vehicleID;
	private LocalDateTime timeStamp;
	private String progressState;
	
	public Checkpoint(String checkpointID, String jobID, String vehicleID, String progressState) {
		this.checkpointID = checkpointID;
		this.jobID = jobID;
		this.vehicleID = vehicleID;
		this.timeStamp = LocalDateTime.now();
		this.progressState = progressState;
	}
	
	public void saveState() {
		System.out.println("Checkpoint " + checkpointID + "saved for Job " + jobID + "on Vehicle " + vehicleID + "at" + timeStamp + "with status " + progressState);
	}
	
	public void loadState() {
		System.out.println("Checkpoint " + checkpointID + "loaded for Job " + jobID + "on Vehicle " + vehicleID + "at" + timeStamp + "restore status " + progressState);
	}
	
	public void deleteCheckpoint() {
		System.out.println("Checkpoint " + checkpointID + "for Job " + jobID + "deleted at" + timeStamp );
	}
	
	//Getters
	
	public String getCheckpointID() {
		return checkpointID;
	}
	public String getJobID() {
		return jobID;
	}
	public String getVehicleID() {
		return vehicleID;
	}
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public String getProgressState() {
		return progressState;
	}
	
	@Override
    public String toString() {
        return "Checkpoint{" +
                "checkpointID='" + checkpointID + '\'' +
                ", jobID='" + jobID + '\'' +
                ", vehicleID='" + vehicleID + '\'' +
                ", timeStamp=" + timeStamp +
                ", progressState='" + progressState + '\'' +
                '}';
    }
}