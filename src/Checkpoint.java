
import java.time.LocalDateTime;

/*=====================
Class Checkpoint Logic - Kendra
======================*/

//----M4 Implementation: Checkpoint stores job state------
class Checkpoint{
	
	//Attributes:
	private String checkpointID; //unique identifier
	private String jobID;	//identify which job checkpoint belong to
	private String vehicleID;	//identify which vehicle is being used for the checkpoint
	private LocalDateTime timeStamp;	//records when checkpoint is created
	private String progressState;	//shows stored execution state of job checkpoint
	
	//Constructor:
	public Checkpoint(String checkpointID, String jobID, String vehicleID, String progressState) {
		//Assigns correct data to objects (initialize properties)
		this.checkpointID = checkpointID;
		this.jobID = jobID;
		this.vehicleID = vehicleID;
		this.timeStamp = LocalDateTime.now(); //auto record real-time tracking
		this.progressState = progressState;
	}
	
	//Method uses 
	
	//Save checkpoint state
	public void saveState() {
		System.out.println("Checkpoint " + checkpointID + "saved for Job " + jobID + "on Vehicle " + vehicleID + "at" + timeStamp + "with status " + progressState);
	}
	
	//Load checkpoint state
	public void loadState() {
		System.out.println("Checkpoint " + checkpointID + "loaded for Job " + jobID + "on Vehicle " + vehicleID + "at" + timeStamp + "restore status " + progressState);
	}
	
	//Delete checkpoint state to remove from storage
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
	
	//override method from super class or implement method from interface to avoid mistakes if methods don't match
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