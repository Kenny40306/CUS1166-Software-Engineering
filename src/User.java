
/*=====================
Class User Logic - Jaden
======================*/

//User class needs to inherit attributes and behavior from both JobOwner and VehicleOwner
//*note java doesn't do extends JobOwner, VehicleOwner so we'll need composition for this part maybe

public class User {
    private String userId;
    private String role;
    private String email;
    private String username;
    private String password;

    // Default constructor
    public User() {
    }

    // Full constructor
    public User(String userId, String role, String email,
                String username, String password) {
        this.userId = userId;
        this.role = role;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User ID: " + userId +
               "\nRole: " + role +
               "\nEmail: " + email +
               "\nUsername: " + username;
    }
}