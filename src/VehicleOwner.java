import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/*=====================
Class Vehicle Owner Logic - Avneet + Jaden
class represents a vehicle owner in the system
stores owner info and the list of vehicles submitted by the owner
======================*/

//----M4 Implementation: 
public class VehicleOwner extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	//Attributes based on user class inheritance to access these fields
	private String residencyTime;
    private List<Vehicle> vehicleSubmitted;
	
	//Contructor:
    
    public VehicleOwner(String userID, String userName, String email, String password, String residencyTime) {
        super(userID, userName, email, "VehicleOwner", password);
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
			return "Owner ID: " + userID +
					", Name: " + userName +
					", Residency: " + residencyTime;
		}
		//getters
		public String getOwnerID() {
			return userID;
		}
		public String getOwnerName() {
			return userName;
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
