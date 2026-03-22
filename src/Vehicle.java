import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/*=====================
Class Vehicle Logic - Subat
======================*/

//----M4 Implementation:
public class Vehicle {
	
	// Enums for vehicle status
	public enum VehicleStatus {
	    AVAILABLE,
	    BUSY,
	    DEPARTING,
	    OFFLINE
	}


    // attributes for each vehicle in the system
    protected String vehicleID;
    protected VehicleStatus status; // Uses enums: can be AVAILABLE, BUSY, DEPARTING, or OFFLINE
    protected double computePower;
    protected Job currentJob; // the job this vehicle is currently running
    protected boolean availability = true; // true = free, false = busy
    protected LocalDateTime arrivalTime;
    protected LocalDateTime departureTime;
    protected boolean scheduleKnown; // true if we know when the vehicle is leaving

    // constructor - sets up a new vehicle with its basic info
    public Vehicle(String vehicleID, String ownerID, double computePower,
                   LocalDateTime arrivalTime, LocalDateTime departureTime, boolean scheduleKnown) {
        this.vehicleID = vehicleID;
        this.computePower = computePower;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.scheduleKnown = scheduleKnown;
        this.status = VehicleStatus.AVAILABLE; // starts as available by default
        this.availability = true;
        this.currentJob = null; // no job assigned yet
    }

    // updates the vehicle's current status
    public void updateStatus(VehicleStatus newStatus) {
        this.status = newStatus;
    }

    // assigns a job to this vehicle and marks it as busy
    public void acceptJob(Job j) {
        this.currentJob = j;
        this.availability = false;
        this.status = VehicleStatus.BUSY;
    }

    // runs the computation for the current job
    public void processJob() {
        if (currentJob != null) {
        	System.out.println("Vehicle " + vehicleID + " is processing job: " + currentJob.getJobID());
        	}
    }

    // sends the completed job results to the server
    public void sendResults(Server server) {
        System.out.println("Vehicle " + vehicleID + " sending results to server.");
    }

    // wipes all job data before the vehicle leaves the parking lot
    public void eraseData() {
        this.currentJob = null;
        this.availability = true;
        this.status = VehicleStatus.AVAILABLE;
        System.out.println("Vehicle " + vehicleID + " data erased.");
    }

    // getters
    public String getVehicleID() { 
    	return vehicleID; 
    }
    public Job getCurrentJob() {
    	return currentJob;
    }
    public VehicleStatus getStatus() { 
    	return status; 
    }
    public boolean isAvailable() { 
    	return availability; 
    }
    public double getComputePower() { 
    	return computePower; 
    }
}