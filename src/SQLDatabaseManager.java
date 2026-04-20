import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Handles SQL Database information being inserted, updated edits, fetch rows and does FIFO result updates
//All methods get called in VCController Class and SQL is permanent storage

public class SQLDatabaseManager { // Called in VCController attribute private SQLDatabaseManager dbManager;

	//Kendra Worked On This:
	//Permanent connection is created once and is reused to avoid any reconnection logic
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
    public void insertJob(Job job, String client) { //Called in Method approvdJob() VCController Class
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
    public void insertVehicle(Vehicle v, String owner) { //Called in Method approvedVehicle() VCController Class
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

    
    //Read/Loads From SQL For Then Update Approved Submissions Already In database
    //get Job/Vehicle by Id methods reads sql data from row then converts it back to object
    
    //Subat + Kendra + Jaden
    // ================= GET JOB BY ID =================
    public Job getJobById(String id) { //Called in Method getJobFromDB() VCController under === Admin Fix Job === and canEditJob() VCController under === ADMIN FIFO DATABASE LOCK DECISION ===
        try {
            String sql = "SELECT * FROM jobs WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();  //return row then convert into java objects

            if (rs.next()) {

                String jobID = rs.getString("job_id");
                String jobName = rs.getString("job_name");
                String clientID = rs.getString("job_clientid");
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
  
    // This method retrieves a vehicle from the database using its ID
    public Vehicle getVehicleById(String id) { //Called in Method getVehicleFromDB() VCController under  === Admin Fix Vehicle ===
        try {

    // SQL query to select all columns from vehicles table where vehicle_id matches
            String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
    
    // Prepare the SQL statement to prevent SQL injection
            PreparedStatement ps = connection.prepareStatement(sql);

     // Set the vehicle ID parameter in the query  
            ps.setString(1, id);

    // Execute the query and store the result in ResultSet
            ResultSet rs = ps.executeQuery(); //return row then convert to java objects

     // Check if a matching vehicle was found
            if (rs.next()) {

     // Extract vehicle data from database columns
                String vehicleID = rs.getString("vehicle_id");
                String ownerID = rs.getString("vehicle_ownerid");
                String vehicleName = rs.getString("vehicle_model");
                int yearMade = rs.getInt("vehicle_year");
                double computePower = 2.5; // fallback (DB has no column)
               
                String residencyRaw = rs.getString("vehicle_residency");
                
                // Default fallback values
                int residencyTime = 1;
                String residencyUnit = "Days(s)";

                 // If residency exists and is not empty, parse it
                if (residencyRaw != null && !residencyRaw.isBlank()) {
                   
                     // Split string into parts 
                    String[] parts = residencyRaw.trim().split("\\s+");

                    try {
                        // first part is the number part
                        residencyTime = Integer.parseInt(parts[0]);

                        // second part is unit part (KEPT EXACT FORMAT, NO LOWERCASE)
                        if (parts.length > 1) {
                            residencyUnit = parts[1];
                        }

                    } catch (Exception e) {
                         // Handle invalid format (prevents crash)
                        System.out.println("Invalid residency format in DB: " + residencyRaw);
                    }
                }
                
                // Get arrival time from database timestamp
                LocalDateTime arrival = rs.getTimestamp("created_at").toLocalDateTime();
               
                // No departure column → use arrival as fallback
                LocalDateTime departure = arrival; // no DB column, fallback
              
                 // Schedule is unknown since DB does not track it
                boolean scheduleKnown = false;

                // Create and return a Vehicle object with all extracted data
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
              // Print error if something goes wrong (SQL issue, parsing, etc.)
            e.printStackTrace();
        }
        return null;
    }
    
    
    //Update Job and Vehicle modify existing SQL Data by finding job / vehicle ID in database
    //Updates established fields for only core attributes
    
    //Moontarin Worked On This
    // ================= ADMIN JOB EDIT UPDATE ====================
   
    // This method updates an existing job in the database
    public boolean updateJob(Job job) { //Called in Method updateApprovedJobFromDB() VCController under === Admin Fix Job ===
    
     // SQL query to update job details in the jobs table
    // It updates name, duration, deadline, and status based on job_id
     String sql = "UPDATE jobs SET job_name=?, job_duration=?, job_deadline_min=?, job_status=? WHERE job_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             
             // Set new job name from Job object
            ps.setString(1, job.getJobName());

             // Convert duration from hours/minutes object to total minutes
            ps.setInt(2, (int) job.getDuration().toMinutes());

             // Set job deadline in minutes
            ps.setInt(3, (int) job.getDeadlineMinutes());

            // Mark job as updated and approved in system
            ps.setString(4, "APPROVED (UPDATED)");

             //Specify which job to update using job ID
            ps.setString(5, job.getJobID());
            
            // Execute update query and get number of affected rows
            int rows = ps.executeUpdate();
            
            // Log update action in console for debugging
            System.out.println("[DB] UPDATE JOB: " + job.getJobID());

            // Return true if at least one row was updated successfully
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Avneet Worked On This
    // ================ ADMIN VEHICLE EDIT UPDATE ====================
    public boolean updateVehicle(Vehicle v) { //Called in Method updateApprovedVehicleFromDB() VCController under === Admin Fix Vehicle ===
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
    public void updateJobFIFO(Job job, int order, long startTime) { //Called in Method calculateCompletionTimes() VCController new Lock JOB After FIFO
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
    public void lockJobs(String jobId) { //Called in Method calculateCompletionTimes() VCController new Lock JOB After FIFO
        try {
            String sql = "UPDATE jobs SET job_locked = 1, job_status = ? WHERE job_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "APPROVED (LOCKED)");
            ps.setString(2, jobId);

            int rows = ps.executeUpdate();

            System.out.println("[DB] Locked rows updated = " + rows + " for " + jobId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Avneet + Subat Worked On This: 
    //Reads lock state if true = 1 or false = 0 that maps boolean value and if true admin can't edit later
    public boolean isJobLocked(String jobId) { //Called in Method canEditJob() VCController under === ADMIN FIFO DATABASE LOCK DECISION ===
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
   //----------------------------------------------------------------------------------------------------------------------------------------------------
    
    
    //Problem upon program restart approved Jobs without fifo calculations couldn't be retrieved from SQl
    //New Fix: get Job / Vehicle owner IDs from approved submission without fifo calculations in database then upon reload into memory
    //Admin now able to calculate fifo runtime form VCController since it was marked as false and then update database again which marks it as true
    
    //============= Reads SQL Owner Methods Upon Re-Loading Program =======================
    //Kendra Worked On This: 
    public String getJobOwner(String jobId) { //Called in Method restoreStateFromDatabase() VCController Class under === RESTORE UPON PROGRAM RE-OPEN ===

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT job_client FROM jobs WHERE job_id = ?"
            );
            ps.setString(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) 
            	return rs.getString("job_client");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    //Moontarin Worked On this:


// This method retrieves the owner of a vehicle from the database using the vehicle ID
// It is used during system restart to restore saved data (restoreStateFromDatabase)
    public String getVehicleOwner(String vehicleId) { //Called in Method restoreStateFromDatabase() VCController Class under === RESTORE UPON PROGRAM RE-OPEN ===

        try {

            // SQL query to get the vehicle owner for a specific vehicle ID
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT vehicle_owner FROM vehicles WHERE vehicle_id = ?"
            );
            
            // Insert the vehicle ID into the query safely
            ps.setString(1, vehicleId);

         // Execute query and store result
            ResultSet rs = ps.executeQuery();

             // If a matching record is found, return the owner name
            if (rs.next()) 
            	return rs.getString("vehicle_owner");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
 // ================= GET ALL APPROVED JOBS NO FIFO =================
    //Subat + Jaden + Avneet 
    public List<Job> getApprovedJobsNoFIFO() { //Called in Method restoreStateFromDatabase() VCController Class under === RESTORE UPON PROGRAM RE-OPEN ===
        
    	//Filters what FIFO is allowed to process and controls how method calculateCompletionTimes() work after restart
    	List<Job> jobs = new ArrayList<>();

        try {
            String sql = """ 
            		SELECT * FROM jobs 
            		WHERE job_status LIKE 'APPROVED%'  
            		AND (completion_time_min IS NULL OR completion_time_min = 0) 
            	""";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String jobID = rs.getString("job_id");
                String jobName = rs.getString("job_name");
                String clientID = rs.getString("job_clientid");
                Duration duration = Duration.ofMinutes(rs.getInt("job_duration"));
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                long deadlineMin = rs.getLong("job_deadline_min");

                //Rebuild Job Object that sets setCompletionTimeCalculated() false to restore fresh unscheduled jobs in memory
                //Upon reload active jobs now gets this job object to be processed for fifo from restoreStateFromDatabase() VCController
                
                Job job = new Job(
                    jobID,
                    jobName,
                    clientID,
                    duration,
                    createdAt,
                    deadlineMin,
                    1
                );

                //IMPORTANT: mark as NOT calculated
                job.setCompletionTimeCalculated(false);
                jobs.add(job);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }
}