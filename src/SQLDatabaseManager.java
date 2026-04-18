import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Handles SQL Database information being inserted, updated edits, fetch rows and does FIFO result updates
//All methods get called in VCController Class and SQL is permanent storage

public class SQLDatabaseManager {

	//Kendra Worked On This:
	//Permanent connection is created once and is reused to avoid reconnection logic
	//================ Connect To SQL Data Base ============
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

    
    //Insert Job and Vehicle takes objects from memory when admin approves submission
    //extracts values using getters and then insert into Job / Vehicle Table sql rows
    
    //Jaden Worked On This: 
    // ================= SAVE JOB =================
    public void insertJob(Job job, String client) { 
        try {
            String sql = "INSERT INTO jobs (job_id, job_client, job_clientid, job_name, job_duration, job_deadline_min, job_status) VALUES (?, ?, ?, ?, ?, ?, ?)"; //(?) are place holders for sql
            PreparedStatement ps = connection.prepareStatement(sql); //Allows data to be written into MYSQL Table 

            ps.setString(1, job.getJobID());
            ps.setString(2, client);
            ps.setString(3, job.getClientID());
            ps.setString(4, job.getJobName());
            ps.setInt(5, (int) job.getDuration().toMinutes());
            ps.setInt(6, (int) job.getDeadlineMinutes());          
            ps.setString(7, "APPROVED");
                    
            ps.executeUpdate(); //my sql handle insert row to create time stamp and store data internally 

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

    
    //Update Job and Vehicle modify existing SQL Data by finding job / vehicle ID in database
    //Updates established fields only core attributes
    
    //Moontarin Worked On This
    // ================= ADMIN JOB EDIT UPDATE ====================
    public boolean updateJob(Job job) {
        String sql = "UPDATE jobs SET job_name=?, job_duration=?, job_deadline_min=?, job_status=? WHERE job_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, job.getJobName());
            ps.setInt(2, (int) job.getDuration().toMinutes());
            ps.setInt(3, (int) job.getDeadlineMinutes());
            ps.setString(4, "APPROVED (UPDATED)");
            ps.setString(5, job.getJobID());

            int rows = ps.executeUpdate();

            System.out.println("[DB] UPDATE JOB: " + job.getJobID());

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Avneet Worked On This
    // ================ ADMIN VEHICLE EDIT UPDATE ====================
    
    public boolean updateVehicle(Vehicle v) {
        String sql = "UPDATE vehicles SET vehicle_model=?, vehicle_year=?, vehicle_residency=?, vehicle_status=? WHERE vehicle_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, v.getVehicleName());
            ps.setInt(2, v.getYearMade());
            ps.setString(3, v.getResidencyDisplay());
            ps.setString(4, "APPROVED (UPDATED)");
            ps.setString(5, v.getVehicleID());

            int rows = ps.executeUpdate();

            System.out.println("[DB] UPDATE VEHICLE: " + v.getVehicleID());

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Used after Calculation Button Is Pressed, it writes scheduling results into database 
    //SQL stores computed scheduling results not just raw data based on Alter job tables

    //Ryan Worked On This:
    // ================= FIFO UPDATE =================
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
   
    
    //========== WRITE AND READ LOCK METHODS ===============
    // Ryan Worked On This:
    //Write (updates database) as locked after fifo 1= true prevents future edits
    public void lockJobs(String jobId) {
        try {
            String sql = "UPDATE jobs SET job_locked = 1 WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, jobId);

            int rows = ps.executeUpdate();

            System.out.println("[DB] Locked rows updated = " + rows + " for " + jobId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Jaden + Subat Worked On This: 
    //Reads lock state if true = 1 or false = 0 that maps boolean value and if true admin can't edit later
    public boolean isJobLocked(String jobId) {
        try {
            String sql = "SELECT job_locked FROM jobs WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, jobId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean locked = rs.getBoolean("job_locked");

                System.out.println("[DB] Job " + jobId + " -> " + locked);

                return locked;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }    	
    	   
    
    //Loads From SQL For Updating Approved Jobs Already In database
    //get Job/Vehicle by Id methods reads sql data from row then converts it back to object
    //Note: also has place holder values since constructor would have more fields than Database stores
    
    //Subat + Kendra + Jaden
    // ================= GET JOB BY ID =================
    public Job getJobById(String id) {
        try {
            String sql = "SELECT * FROM jobs WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();  //return row then convert into java objects

            if (rs.next()) {

                String jobID = rs.getString("job_id");
                String jobName = rs.getString("job_name");
                String clientID = String.valueOf(rs.getInt("job_clientid"));
                Duration duration = Duration.ofMinutes(rs.getInt("job_duration"));
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                long deadlineMin = rs.getLong("job_deadline_min");

                return new Job(
                    jobID,
                    jobName,
                    clientID,
                    duration,
                    createdAt,        // used as base timestamp
                    deadlineMin,
                    1                 // redundancy default (DB doesn't store it yet)
                );
                
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Avneet + Moontarin + Ryan
    // ================= GET VEHICLE BY ID =================
    public Vehicle getVehicleById(String id) {
        try {
            String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery(); //return row then convert to java objects

            if (rs.next()) {

                String vehicleID = rs.getString("vehicle_id");
                String ownerID = String.valueOf(rs.getInt("vehicle_ownerid"));
                String vehicleName = rs.getString("vehicle_model");
                int yearMade = rs.getInt("vehicle_year");

                double computePower = 2.5; // fallback (DB has no column)

                String residencyRaw = rs.getString("vehicle_residency");
                int residencyTime = 1;
                String residencyUnit = "days";

                if (residencyRaw != null) {
                	
                	residencyRaw = residencyRaw.toLowerCase().trim();
                    String[] parts = residencyRaw.split(" ");
                    
                    try {
                        residencyTime = Integer.parseInt(parts[0]);

                        if (parts.length > 1) {
                            residencyUnit = parts[1].replace("(s)", "").replace("s", "");
                        }

                    } catch (Exception e) {
                        System.out.println("Invalid residency format: " + residencyRaw);
                    }
                }

                LocalDateTime arrival = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime departure = arrival; // no DB column, fallback
                boolean scheduleKnown = false;

                return new Vehicle(
                    vehicleID,
                    ownerID,
                    vehicleName,
                    yearMade,
                    computePower,
                    residencyTime,
                    residencyUnit,
                    arrival,
                    departure,
                    scheduleKnown
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}  
    