import java.time.LocalDateTime;

/*=====================
Class User Logic - Jaden
======================*/

//User class needs to inherit attributes and behavior from both JobOwner and VehicleOwner
//*note java doesn't do extends JobOwner, VehicleOwner so we'll need composition for this part maybe

public class User{
	
	//Attributes
	private String userID;
	private String userName;
	private String email;
	private String role; // describes type of user regular user or admin for jobowner or vehicleowner
	private String password;
	
	public User(String userID, String userName, String email, String role, String password) {
		this.userID = userID;
		this.userName = userName;
		this.email = email;
		this.role = role;
		this.password = password;
	}
	
	//Methods needed - Jaden
	
	//login(),logout(), viewsummary()

}

