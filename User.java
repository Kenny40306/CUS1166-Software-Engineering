import java.time.LocalDateTime;

/*=====================
Class User Logic - Jaden
======================*/

public class User {
	
	//Attributes
	private String userID;
	private String name;
	private String email;
	private String role; // describes type of user regular user or admin for jobowner or vehicleowner
	private String password;
	
	public User(String userID, String name, String email, String role, String password) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.role = role;
		this.password = password;
	}
	
	//Methods needed - Jaden
	//login(),logout(), viewsummary()

}

