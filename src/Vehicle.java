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
	protected String ownerID;
    protected String vehicleID;
    protected int yearMade;
    protected String vehicleName;
    protected VehicleStatus status; // Uses enums: can be AVAILABLE, BUSY, DEPARTING, or OFFLINE
    protected double computePower; //GHz
    protected Job currentJob; // the job this vehicle is currently running
    protected int residencyTime;
    protected String residencyUnit;
    protected boolean availability = true; // true = free, false = busy
    protected LocalDateTime arrivalTime;
    protected LocalDateTime departureTime;
    protected boolean scheduleKnown; // true if we know when the vehicle is leaving

    // constructor - sets up a new vehicle with its basic info
    public Vehicle(String vehicleID, String ownerID, String vehicleName, int yearMade, double computePower,
    			int residencyTime, String residencyUnit,
    			LocalDateTime arrivalTime, LocalDateTime departureTime, boolean scheduleKnown) {    
    	
    	this.vehicleID = vehicleID;
        this.ownerID = ownerID;
        this.yearMade = yearMade;
        this.vehicleName = vehicleName;
        this.residencyTime = residencyTime;
        this.residencyUnit = residencyUnit;
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

 // ================= ADMIN EDIT SETTERS =================
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setYearMade(int yearMade) {
        this.yearMade = yearMade;
    }

    public void setResidencyDisplay(String display) {
        // simple parse: "5 days" → split number + unit
        try {
            String[] parts = display.trim().split(" ");
            this.residencyTime = Integer.parseInt(parts[0]);
            this.residencyUnit = (parts.length > 1) ? parts[1] : "days";
        } catch (Exception e) {
            System.out.println("Invalid residency format: " + display);
        }
    }
    //====================================================================
    
    // getters
    public String getVehicleStatusInfo() {
        String assignedJob = (currentJob != null) ? currentJob.getJobName() : "idle";
        return "Vehicle ID: " + vehicleID +
               " | Status: " + status +
               " | Job: " + assignedJob +
               " | Power: " + computePower + " GHz";
    }
    public String getOwnerID() {
        return ownerID;
    }
    public String getVehicleID() { 
    	return vehicleID; 
    }
    public String getVehicleName() { 
    	return vehicleName; 
    }
    public Job getCurrentJob() {
    	return currentJob;
    }
    public int getYearMade() {
        return yearMade;
    }    
    
    public String getResidencyDisplay() {
        return residencyTime + " " + residencyUnit;
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
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID='" + vehicleID + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", status=" + status +
                ", computePower=" + computePower + "GHz" +
                ", currentJob=" + (currentJob != null ? currentJob.getJobName() : "None") +
                ", available=" + availability +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                ", scheduleKnown=" + scheduleKnown +
                '}';
    }
}