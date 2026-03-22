import java.util.ArrayList;
import java.util.List;

/*=====================
Class Vehicle Owner Logic - Avneet
======================*/

//----M4 Implementation: 
public class VehicleOwner {

	//Protected Attributes based on user class inheritance to access these fields
	protected String ownerID;
	protected String ownerName;
	protected String residencyTime;
	protected List<Vehicle> vehicleSubmitted;
	
	//Contructor:
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
		//method to view vehicles
		public List<Vehicle> getVehciles() {
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
		//setter
		public void setResidencyTime(String residencyTime) {
			this.residencyTime = residencyTime;
		}
	}
	//test
