import java.sql.*;

//Test Class Prototype
public class SQLDatabaseManager {

    private Connection connection;

    public SQLDatabaseManager() {
        try {
        	connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/m6vccontroller_system",
                "root",
                "password"
            );
            System.out.println("Database Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAVE JOB =================
    public void insertJob(Job job, String client) {
        try {
            String sql = "INSERT INTO jobs (job_id, job_client, job_name, job_duration, job_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, job.getJobID());
            ps.setString(2, client);
            ps.setString(3, job.getJobName());
            ps.setInt(4, (int) job.getDuration().toMinutes());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAVE VEHICLE =================
    public void insertVehicle(Vehicle v, String owner) {
        try {
            String sql = "INSERT INTO vehicles (vehicle_id, vehicel_owner, vehicle_year, vehicle_name, vehicle_model, vehicle_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, v.getVehicleID());
            ps.setString(2, owner);
            ps.setString(3, v.getVehicleName());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
