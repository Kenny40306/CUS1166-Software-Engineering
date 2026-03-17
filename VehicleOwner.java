import java.util.ArrayList;
import java.util.List;

/*=====================
Class Vehicle Owner Logic - 
======================*/

public class VehicleOwner {

	protected String ownerID;
	protected String ownerName;
	protected String residencyTime;
	protected List<Vehicle> vehicleSubmitted;
	
	//Methods
	public VehicleOwner(String ownerID, String ownerName, String residencyTime) {
		this.ownerID = ownerID;
		this.ownerName = ownerName;
		this.residencyTime = residencyTime;
		this.vehicleSubmitted = new ArrayList<>();
	}
}
