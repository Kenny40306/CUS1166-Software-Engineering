import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.io.Serializable;

/*=====================
Class Vehicle Logic - Subat
======================*/

//----M4 Implementation:
public class Vehicle implements Serializable {
	private static final long serialVersionUID =1L; //Avneet: M5 Serializable for Socket
	
	// Enums for vehicle status
	public enum VehicleStatus {
	    AVAILABLE,
	    BUSY,
	    DEPARTING,
	    OFFLINE
	}


    // attributes for each vehicle in the system
    protected String vehicleID;
    protected String vehicleName;
    protected VehicleStatus status; // Uses enums: can be AVAILABLE, BUSY, DEPARTING, or OFFLINE
    protected double computePower; //GHz
    protected Job currentJob; // the job this vehicle is currently running
    protected boolean availability = true; // true = free, false = busy
    protected LocalDateTime arrivalTime;
    protected LocalDateTime departureTime;
    protected boolean scheduleKnown; // true if we know when the vehicle is leaving

    // constructor - sets up a new vehicle with its basic info
    public Vehicle(String vehicleID, String vehicleName, double computePower,
                   LocalDateTime arrivalTime, LocalDateTime departureTime, boolean scheduleKnown) {
        this.vehicleID = vehicleID;
        this.vehicleName = vehicleName;
        this.computePower = computePower;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.scheduleKnown = scheduleKnown;
        this.status = VehicleStatus.AVAILABLE; // starts as available by default
        this.availability = true;
        this.currentJob = null; // no job assigned yet
    }
    
    public Vehicle(String ownerID, String make, String model, String vin, String residencyTime) {
        this.vehicleID = vin;              // use VIN as vehicle ID
        this.vehicleName = make + " " + model;
        this.computePower = 1.0;           // default compute power
        this.arrivalTime = LocalDateTime.now();
        this.departureTime = LocalDateTime.now().plusHours(1);
        this.scheduleKnown = false;
        this.status = VehicleStatus.AVAILABLE;
        this.availability = true;
        this.currentJob = null;
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
    
    public void processJob() {
        new Thread(() -> {
            try {
                System.out.println("Vehicle " + vehicleID + " started processing job " + currentJob.getJobID());

                updateStatus(VehicleStatus.BUSY);

                // Simulate job processing time
                Thread.sleep(3000);

                System.out.println("Vehicle " + vehicleID + " completed job " + currentJob.getJobID());

                if (currentJob != null) {
                    currentJob.updateProgress(Job.JobStatus.COMPLETED);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // subat
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
    
    public String getVehicleStatusInfo() {
        String assignedJob = (currentJob != null) ? currentJob.getJobName() : "idle";
        return "Vehicle ID: " + vehicleID +
               " | Status: " + status +
               " | Job: " + assignedJob +
               " | Power: " + computePower + " GHz";
    }


    // getters
    public String getVehicleID() { 
    	return vehicleID; 
    }
    public String getVehicleName() { 
    	return vehicleName; 
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