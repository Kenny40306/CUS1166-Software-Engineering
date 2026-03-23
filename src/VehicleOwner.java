import java.util.ArrayList;
import java.util.List;

/*=====================
Class Vehicle Owner Logic - Avneet
class represents a vehicle owner in the system
stores owner info and the list of vehicles submitted by the owner
======================*/

//----M4 Implementation: 
public class VehicleOwner {

	//Protected Attributes based on user class inheritance to access these fields
	protected String ownerID;//unique ID for the vehicle owner
	protected String ownerName;//name of the vehicle owner
	protected String residencyTime;//how long the vehicle will stay
	protected List<Vehicle> vehicleSubmitted;// list of vehicles submitted
	
	//Contructor:
	//initializes the vehicle owner object with ID, name, and residency time
	//also creates an empty list to store vehicles
	public VehicleOwner(String ownerID, String ownerName, String residencyTime) {
		this.ownerID = ownerID;
		this.ownerName = ownerName;
		this.residencyTime = residencyTime;
		this.vehicleSubmitted = new ArrayList<>();
	}
	
	//method to add the vehicle
		public void addVehicle(Vehicle vehicle) {
			vehicleSubmitted.add(vehicle);	
		}
		//method to view vehicles from the list submitted by the owner
		public List<Vehicle> getVehicles() {
			return vehicleSubmitted;
		}
		//method to show the owner info
		public String displayOwnerInfo() {
			return "Owner ID: " + ownerID +
					", Name: " +ownerName +
					", Residency: " + residencyTime;
		}
		//getters
		public String getOwnerID() {
			return ownerID;
		}
		public String getOwnerName() {
			return ownerName;
		}
		public String getResidencyTime() {
			return residencyTime;
		}
		//setter method to update residency time if needed
		public void setResidencyTime(String residencyTime) {
			this.residencyTime = residencyTime;
		}
	}
	//test
