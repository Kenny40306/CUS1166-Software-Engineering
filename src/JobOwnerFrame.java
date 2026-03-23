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
	private JTextField deadlineField = new JTextField(5); //identify completion limit hours
	   
	//labels for corresponding text fields
    private JLabel clientIDLabel;
    private JLabel jobNameLabel;
    private JLabel durationLabel;
    private JLabel deadlineLabel;
            
    //------M4 Implementation: shared controller with job owner client--------
    private VCController vcController;
    private MainControllerFrame mainFrame; //single instance
    private JButton calculate; //create calculate button

	public JobOwnerFrame(VCController vcController){ //Method for GUI setup
		 this.vcController = vcController;

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
        deadlineLabel = new JLabel("Deadline (Hours):");
        UIStyling.styleLabel(deadlineLabel);
        panel.add(deadlineLabel);

        deadlineField = new JTextField();
        UIStyling.styleTextField(deadlineField);
        panel.add(deadlineField);
        
        //Buttons
		JButton submit = new JButton("Submit"); //create submit action button to save data
		JButton back= new JButton("Back"); //create back action button for navigation control
		calculate= new JButton("Calculate"); //create calculate button that goes to VC MainControllerFrame
		
		UIStyling.styleButton(submit);
        UIStyling.styleButton(back);
        UIStyling.styleButton(calculate);

        //Add buttons to panel
		panel.add(submit);
		panel.add(back);
		panel.add(calculate);
		
		//Title Label
		JLabel titleLabel = UIStyling.createTitleLabel("Job Owner Form"); //creates title label for the form
        UIStyling.setupFrame(this, panel, titleLabel, "Job Owner Information"); 

        //Button Actions
		submit.addActionListener(e -> saveJobData()); //event button runs saveJob method
		back.addActionListener(e-> {
			dispose(); //event button close current window for role selection
			new RoleSelectionFrame(vcController);
			
		});//returns user to main menu
		
		//Jaden Wrote This
		//------- M4 Implementation --------- 
		//calculate button for controller to run back end calculations
		calculate.addActionListener(e-> {	
			if (mainFrame == null) { // only create frame once
			mainFrame = new MainControllerFrame(vcController,this);
			}
			mainFrame.setVisible(true);
			mainFrame.clearOutput(); //clear previous display
			mainFrame.displayCurrentJobs();     // shows only current batch
			mainFrame.displayCompletionTimes();	//shows current batch
			mainFrame.displayQueue();
			mainFrame.displayServerStatus();
			
		});
		setVisible(true); //always display GUI window 
	}
	
	//Jaden Wrote This
	//Needs save Job method for handling validation requests and storage
	private void saveJobData() {
		// get input from user 
		String id = clientIDField.getText();
		String jName = jobNameField.getText();
		String durText = durationField.getText();
		String ddlText = deadlineField.getText();

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
		int deadlineHr;

		try {
		    durationMin = Integer.parseInt(durText);
		    deadlineHr = Integer.parseInt(ddlText);

		    if (durationMin <= 0 || deadlineHr <= 0) {
		        throw new NumberFormatException();
		    }

		} catch (NumberFormatException ex) {
		    JOptionPane.showMessageDialog(this,
		            "Duration must be minutes and Deadline must be hours.",
		            "Invalid Input",
		            JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//Kendra Wrote This-
		//-------- M4 Implementation: Create JobOwner and Job objects ------- 
		//Responsible for core logic when creating job submission and sending it to the system
        
		//created new client id to be derived (Overloaded for GUI use)
		JobOwner client = new JobOwner(
			    id,
			    "Client " + id,
			    "client@email.com",
			    "password123"
			);
		
        String jobID = String.format("JOB-%08d", (int)(Math.random()* 100_000_000)); // generate unique jobID 8 digit
        
        Job job = new Job(									// created new job object that builds the actual job for submission    
        		jobID,
                jName,
                Duration.ofMinutes(durationMin),     // Duration of the object
                LocalDateTime.now().plusHours(deadlineHr), // deadline
                1                                        // default redundancy value
        );
        
        client.submitJob(job); // add job to client's personal job list

        // Send job and client to VCController if it exists so it can process information
        if (vcController != null) {
            vcController.receiveJob(job, client);
        }
		//------------------------------------------------------------------------*/
        
        
        
		//needs fileutil.writer and time stamp
		//creates file reader 
		 try (FileWriter writer = new FileWriter("job_owner_data.txt", true)) {
		writer.write("Timestamp: " + LocalDateTime.now() + "\n");
		writer.write("Client ID: " + id + "\n");
		writer.write("Job Name: " + jName + "\n");
		writer.write("Job Duration: " + durText + "Minutes\n");
		writer.write("Job Deadline: " + ddlText + "Hours\n");
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