import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


/*=====================
  Client Job Owner Frame - Kendra + Jaden
 ======================*/

//Kendra Wrote This:
//GUI window for Job Owner to submit computational jobs to vehicular cloud systems
class JobOwnerFrame extends JFrame{ //this class inherits GUI window with extended JFrame for button, layout and open/close behaviors
	
	//all private fields to prevent other classes form modifying the fields
	private JTextField clientIDField = new JTextField(10); //identify client
	private JTextField jobNameField = new JTextField(10); //identify job description
	private JTextField durationField = new JTextField(10); //identify execution time minutes
	private JTextField deadlineField = new JTextField(5); //identify completion limit minutes
	   
	//labels for corresponding text fields
    private JLabel clientIDLabel;
    private JLabel jobNameLabel;
    private JLabel durationLabel;
    private JLabel deadlineLabel;
    
    //------M4 Implementation: shared controller with job owner client--------
    private VCController vcController; //back end sever handles jobs
    private RoleSelectionFrame roleFrame; //reference to RoleSelectionFrame to return to it

	public JobOwnerFrame(VCController vcController, RoleSelectionFrame roleFrame){ //Method for GUI setup
		 this.vcController = vcController;
		 this.roleFrame = roleFrame;

		setTitle("Job Owner Information"); //text for window identification
		setSize(600,400); //size dimension for window components to fit in and avoids resizing issues
		setLocationRelativeTo(null); //allows for window to be centered on the screen 
		JPanel panel = new JPanel(new GridLayout(7,2,10,10)); //grid allows for alignments (7 rows, 2 columns, 10 pixel spacing horizontal and vertical)
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // allows for proper boarder
		UIStyling.stylePanel(panel); //UIStyling allows for custom coloring
        
        //Client ID
        clientIDLabel = new JLabel("Client ID:"); //creates label for object client ID text
        UIStyling.styleLabel(clientIDLabel); //UIStyling method called for font and color
        panel.add(clientIDLabel); //attach label to JPanel

        clientIDField = new JTextField(); //create interactive box for user to type in ID
        UIStyling.styleTextField(clientIDField); //UIStyling method called for font and color
        panel.add(clientIDField); //attach input box to JPanel
        
        
        //Job Name
        jobNameLabel = new JLabel("Job Name:");
        UIStyling.styleLabel(jobNameLabel);
        panel.add(jobNameLabel);

        jobNameField = new JTextField();
        UIStyling.styleTextField(jobNameField);
        panel.add(jobNameField);

        //Job Duration
        durationLabel = new JLabel("Job Duration (Minutes):");
        UIStyling.styleLabel(durationLabel);
        panel.add(durationLabel);

        durationField = new JTextField();
        UIStyling.styleTextField(durationField);
        panel.add(durationField);

        //Deadline
        deadlineLabel = new JLabel("Deadline (Minutes):");
        UIStyling.styleLabel(deadlineLabel);
        panel.add(deadlineLabel);

        deadlineField = new JTextField();
        UIStyling.styleTextField(deadlineField);
        panel.add(deadlineField);

        //Buttons
		JButton submit = new JButton("Submit"); //create submit action button to save data
		JButton back= new JButton("Back"); //create back action button for navigation control
		UIStyling.styleButton(submit);
        UIStyling.styleButton(back);
       
        //Add buttons to panel
		panel.add(submit);
		panel.add(back);
	
		//Title Label
		JLabel titleLabel = UIStyling.createTitleLabel("Job Owner Form"); //creates title label for the form
        UIStyling.setupFrame(this, panel, titleLabel, "Job Owner Information"); 

        //Button Actions
		submit.addActionListener(e -> saveJobData()); //event button runs saveJob method
		back.addActionListener(e-> {
			this.setVisible(false);   //event button close current window for role selection
			  if (roleFrame != null) {
	                roleFrame.setVisible(true); // show existing RoleSelectionFrame
	            } else {
	                new RoleSelectionFrame(vcController, "User");
	            }			
		});//returns user to main menu
		
		setVisible(true); //always display GUI window 
	}
	

	
	//Jaden Wrote This
	//Needs save Job method for handling validation requests and storage
	private void saveJobData() {
		// get input from user 
		
		String id = clientIDField.getText().trim(); //gets from user input for file
		String jName = jobNameField.getText().trim();
		String durText = durationField.getText().trim();
		String ddlText = deadlineField.getText().trim();
		//M5 change gets from system user for notification logic
		String systemID = vcController.getCurrentUserId(); //(!!!) root cause for notification link
		
		// validates that there are no empty fields
		if (id.isEmpty() || jName.isEmpty() || durText.isEmpty() || ddlText.isEmpty()) {
		JOptionPane.showMessageDialog(this,
		"All fields must be filled out!",
		"Input Error",
		JOptionPane.ERROR_MESSAGE);
		return;
		}

		//Valid inputs for duration and deadline fields
		int durationMin;
		int deadlineMin;

		try {
		    durationMin = Integer.parseInt(durText);
		    deadlineMin = Integer.parseInt(ddlText);

		    if (durationMin <= 0 || deadlineMin <= 0) {
		        throw new NumberFormatException();
		    }

		} catch (NumberFormatException ex) {
		    JOptionPane.showMessageDialog(this,
		            "Duration and Deadline must be minutes.",
		            "Invalid Input",
		            JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//Kendra Wrote This-
		//-------- M4 Implementation: Create JobOwner and Job objects ------- 
		//Responsible for core logic when creating job submission and sending it to the system
		
		//Gets data from user input from jobownerframe textfield boxes
		//created new client id to be derived (Overloaded for GUI use)
	
		JobOwner client = new JobOwner(
			    systemID,
			    "Client " + systemID,
			    "client@email.com",
			    "password123"
			);
         
        String jobID = String.format("JOB-%08d", (int)(Math.random()* 100_000_000)); // generate unique jobID 8 digit
        
        Job job = new Job(		// created new job object that builds the actual job for submission    
        		jobID,
                jName,
                systemID,				//M5 change matches login user job knows which client submitted it via systemID
                Duration.ofMinutes(durationMin),     // Duration of the object
                LocalDateTime.now().plusMinutes(deadlineMin), // deadline
                1     // default redundancy value
                
        );
        
        //M5 change here that calls JobOwner Class
        client.submitJobToServer(job); // Socket Call
       //------------------------------------------------------------------------*/
        
        
		//needs fileutil.writer and time stamp
		//creates file reader 
		try (FileWriter writer = new FileWriter("job_owner_data.txt", true)) {
		writer.write("Timestamp: " + LocalDateTime.now() + "\n");
		writer.write("Client ID: " + id + "\n");
		writer.write("Job Name: " + jName + "\n");
		writer.write("Job Duration: " + durText + " Minutes\n");
		writer.write("Job Deadline: " + ddlText + " Minutes\n");
		writer.write("---------------------------------\n");

		JOptionPane.showMessageDialog(this, "Job saved successfully!");

		// clear fields after done 
		clientIDField.setText("");
		jobNameField.setText("");
		durationField.setText("");
		deadlineField.setText("");

		} catch (IOException ex) {
		JOptionPane.showMessageDialog(this,
		"Error saving file: " + ex.getMessage(),
		"Save Error",
		JOptionPane.ERROR_MESSAGE);
		}
	}
}