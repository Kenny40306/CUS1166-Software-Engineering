import java.time.LocalDateTime;

/*=====================
Class User Logic - Jaden
======================*/

//----M4 Implementation: User class needs to inherit attributes and behavior from both JobOwner and VehicleOwner ---
//*note java doesn't do extends JobOwner, VehicleOwner so we'll need composition for this part maybe

public class User{
	
	//Attributes
	protected String userID;
    protected String userName;
    protected String email;
    protected String role;
    protected String password;
    protected boolean isLoggedIn;
	
    public User(String userID, String userName, String email, String role, String password) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.isLoggedIn = false;
    }
	//login method - Jaden
    public boolean login(String inputEmail, String inputPassword) {
        if (this.email.equals(inputEmail) && this.password.equals(inputPassword)) {
            isLoggedIn = true;
            System.out.println(userName + " logged in successfully at " + LocalDateTime.now());
            return true;
        } else {
            System.out.println("Login failed for " + userName);
            return false;
        }
    }

   //logout
    public void logout() {
        if (isLoggedIn) {
            isLoggedIn = false;
            System.out.println(userName + " logged out.");
        } else {
            System.out.println("User is not logged in.");
        }
    }
	
    // Getters
    public String getUserId()
    { 
    	return userID; 
    }
    public String getUsername() 
    { 
    	return userName; 
    }
    public String getEmail() 
    { 
    	return email; 
    }
    public String getRole()
    { 
    	return role; 
    }
    public boolean isLoggedIn()
    { 
    	return isLoggedIn; 
    }

    @Override
    public String toString() {
        return "User ID: " + userID +
               "\nRole: " + role +
               "\nEmail: " + email +
               "\nUsername: " + userName +
               "\nLogged In: " + isLoggedIn;
    }
}
