import java.util.ArrayList;
import java.util.List;

/*=====================
Class Vehicle Owner Logic - Avneet
======================*/

public class VehicleOwner {

	//Protected Attributes based on user class inheritance to access these fields
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
