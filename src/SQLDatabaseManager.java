import java.sql.*;

//Test Class Prototype
public class SQLDatabaseManager {

	//Kendra Worked On This:
    private Connection connection;

    public SQLDatabaseManager() {
        try {
        	connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/m6vcrts",
                "KennyP",
                "JellyBean44$"
            );
            System.out.println("Database Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Jaden Worked On This: 
    // ================= SAVE JOB =================
    public void insertJob(Job job, String client) {
        try {
            String sql = "INSERT INTO jobs (job_id, job_client, job_clientid, job_name, job_duration, job_deadline_min, job_status) VALUES (?, ?, ?, ?, ?, ?, ?)"; //(?) are place holders for sql
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, job.getJobID());
            ps.setString(2, client);
            ps.setString(3, job.getClientID());
            ps.setString(4, job.getJobName());
            ps.setInt(5, (int) job.getDuration().toMinutes());
            ps.setInt(6, (int) job.getDeadlineMinutes());          
            ps.setString(7, "APPROVED");
                    
            ps.executeUpdate(); //my sql handle insert row to create time stamp

            System.out.println("[DB] Job inserted: " + job.getJobID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Subat Worked On this 
    // ================= SAVE VEHICLE =================
    public void insertVehicle(Vehicle v, String owner) {
        try {
            String sql = "INSERT INTO vehicles (vehicle_id, vehicle_owner, vehicle_ownerid, vehicle_year, vehicle_model, vehicle_residency, vehicle_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, v.getVehicleID());
            ps.setString(2, owner);
            ps.setString(3, v.getOwnerID());
            ps.setInt(4, v.getYearMade());
            ps.setString(5, v.getVehicleName());
            ps.setString(6, v.getResidencyDisplay());
            ps.setString(7, "APPROVED");
            
            ps.executeUpdate(); //my sql handle insert row to create time stamp
            
            System.out.println("[DB] Vehicle inserted: " + v.getVehicleID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //Moontarin Worked On This
    // ================= ADMIN JOB EDIT ====================
    public void updateJob(Job job) {
        try {
            String sql = "UPDATE jobs SET job_client=?, job_clientid=?, job_name=?, job_duration=?, job_deadline_min=?, job_status=? WHERE job_id=?";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, job.getClientID());
            ps.setString(2, job.getClientID());
            ps.setString(3, job.getJobName());
            ps.setInt(4, (int) job.getDuration().toMinutes());
            ps.setInt(5, (int) job.getDeadlineMinutes());
            ps.setString(6, "UPDATED");
            ps.setString(7, job.getJobID());

            ps.executeUpdate();

            System.out.println("[DB] Job updated: " + job.getJobID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Avneet Worked On This
    // ================ ADMIN VEHICLE EDIT====================
    public void updateVehicle(Vehicle v) {
        try {
            String sql = "UPDATE vehicles SET vehicle_owner=?, vehicle_ownerid=?, vehicle_year=?, vehicle_model=?, vehicle_residency=?, vehicle_status=? WHERE vehicle_id=?";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, v.getVehicleID());
            ps.setString(2, v.getOwnerID());
            ps.setInt(3, v.getYearMade());
            ps.setString(4, v.getVehicleName());
            ps.setString(5, v.getResidencyDisplay());
            ps.setString(6, "UPDATED");
            ps.setString(7, v.getVehicleID());

            ps.executeUpdate();

            System.out.println("[DB] Vehicle updated: " + v.getVehicleID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Ryan Worked On This:
    // ================= FIFO UPDATE =================
    //Used after Calculation Button Is Pressed
    public void updateJobFIFO(Job job, int order, long startTime) {
        try {
            String sql = "UPDATE jobs SET execution_order = ?, start_time = ?, completion_time_min = ? WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, order);
            ps.setLong(2, startTime);
            ps.setLong(3, job.getCompletionTime());
            ps.setString(4, job.getJobID());

            ps.executeUpdate(); //my sql handle insert row to create time stamp

            System.out.println("[DB] FIFO updated: " + job.getJobID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
