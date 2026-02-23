import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/*=====================
  Client Frame JOB OWNER
 ======================*/

//Kendra Wrote This:
//GUI window for Job Owner to submit computational jobs to vehicular cloud systems
class JobOwnerFrame extends JFrame{ //this class inherits GUI window with extended JFrame for button, layout and open/close behaviors
	
	//all private to prevent other classes form modifying the fields
	private JTextField clientIDField = new JTextField(); //identify client
	private JTextField jobNameField = new JTextField(); //identify job description
	private JTextField durationField = new JTextField(); //identify execution time
	private JTextField deadlineField = new JTextField(); //identify completion limit
	    
	//labels for corresponding text fields
    private JLabel clientIDLabel;
    private JLabel jobNameLabel;
    private JLabel durationLabel;
    private JLabel deadlineLabel;
    
	public JobOwnerFrame(){ //Method for GUI setup
		
		setTitle("Job Owner Information"); //text for window identification
		setSize(600,400); //size dimension for window components to fit in and avoids resizing issues
		setLocationRelativeTo(null); //allows for window to be centered on the screen 
		JPanel panel = new JPanel(new GridLayout(7,2,10,10)); //grid allows for alignments (5 rows, 2 columns, 10 pixel spacing horizontal and vertical)
        UIStyling.stylePanel(panel); //UIStyling allows for custom coloring
        
     // Client ID
        clientIDLabel = new JLabel("Client ID:");
        UIStyling.styleLabel(clientIDLabel);
        panel.add(clientIDLabel);

        clientIDField = new JTextField();
        UIStyling.styleTextField(clientIDField);
        panel.add(clientIDField);

        // Job Name
        jobNameLabel = new JLabel("Job Name:");
        UIStyling.styleLabel(jobNameLabel);
        panel.add(jobNameLabel);

        jobNameField = new JTextField();
        UIStyling.styleTextField(jobNameField);
        panel.add(jobNameField);

        // Job Duration
        durationLabel = new JLabel("Job Duration:");
        UIStyling.styleLabel(durationLabel);
        panel.add(durationLabel);

        durationField = new JTextField();
        UIStyling.styleTextField(durationField);
        panel.add(durationField);

        // Deadline
        deadlineLabel = new JLabel("Deadline:");
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
		
		JLabel titleLabel = UIStyling.createTitleLabel("Job Owner Form"); //creates title label for the form
        UIStyling.setupFrame(this, panel, titleLabel, "Job Owner Information"); 

		submit.addActionListener(e -> saveJobData()); //event button runs saveJob method
		back.addActionListener(e-> {
			dispose(); //event button close current window for role selection
			new RoleSelectionFrame();
			
		});//returns user to main menu
		
		setVisible(true); //always display GUI window 
	}
	
	//Jaden Wrote this
	//Needs save Job method for handling validation requests and storage
	private void saveJobData() {
		// get input from user 
		String id = clientIDField.getText();
		String name = jobNameField.getText();
		String durText = durationField.getText();
		String ddlText = deadlineField.getText();

		// validates that there are no empty fields
		if (id.isEmpty() || name.isEmpty() || durText.isEmpty() || ddlText.isEmpty()) {
		JOptionPane.showMessageDialog(this,
		"All fields must be filled out!",
		"Input Error",
		JOptionPane.ERROR_MESSAGE);
		return;
		}

		
		//needs fileutil.writer and time stamp
		//creates file reader 
		 try (FileWriter writer = new FileWriter("job_owner_data.txt", true)) {
		writer.write("Client ID: " + id + "\n");
		writer.write("Job Name: " + name + "\n");
		writer.write("Job Duration: " + durText + "\n");
		writer.write("Job Deadline: " + ddlText + "\n");
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

