import java.util.ArrayList;
import java.util.List;

/*=====================
Class Vehicle Owner Logic - Avneet + Jaden
class represents a vehicle owner in the system
stores owner info and the list of vehicles submitted by the owner
======================*/

//----M4 Implementation: 
public class VehicleOwner extends User{ //implements ClientInterface{

	//Attributes based on user class inheritance to access these fields
	private String residencyTime;
    private List<Vehicle> vehicleSubmitted;
	
	//Contructor:
    
    public VehicleOwner(String userID, String userName, String email, String password, String residencyTime) {
        super(userID, userName, email, "VehicleOwner", password);
        this.residencyTime = residencyTime;
        this.vehicleSubmitted = new ArrayList<>();
    }
	
    
    
  //==================================================================================================
  	//-
  	//M5 Implementation: ====== Send vehicle to VCController (asynchronous) ========
    public void submitVehicleToServer(Vehicle vehicle) {
        new Thread(() -> {
            try {
                ClientConnection connection = new ClientConnection("localhost", 5001);

                MessageServer message = new MessageServer(
                        MessageServer.Type.VEHICLE_REQUEST,
                        vehicle,
                        this.userID
                );

                String response = connection.send(message);

                notify("Server Response: " + response);

            } catch (Exception e) {
                notify("Error sending vehicle: " + e.getMessage());
            }
        }).start();
    }

    
    public void notify(String message) {
        System.out.println("[VehicleOwner Notification] " + message);
    }
    
  //=====================================================================================================
	
    
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
