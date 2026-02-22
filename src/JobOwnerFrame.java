import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/*=====================
  Client Frame JOB OWNER
 ======================*/

//Kendra Wrote This
//GUI window for Job Owner to submit computational jobs to vehicular cloud systems
class JobOwnerFrame extends JFrame{ //this class inherits GUI window with extended JFrame for button, layout and open/close behaviors
	
	//all private to prevent other classes form modifying the fields
	private JTextField clientID = new JTextField(); //identify client
	private JTextField jobName = new JTextField(); //identify job description
	private JTextField duration = new JTextField(); //identify execution time
	private JTextField deadline = new JTextField(); //identify completion limit
	
	public JobOwnerFrame(){
		
		setTitle("Job Owner Information"); //text for window identification
		setSize(600,400); //size dimension for window components to fit in and avoids resizing issues
		setLocationRelativeTo(null); //allows for window to be centered on the screen
		setLayout(new  BorderLayout()); 
		JPanel panel = new JPanel(new GridLayout(5,2,10,10)); //grid allows for alignments (5 rows, 2 columns, 10 pixel spacing horizontal and vertical)
        UIStyling.styleVehiclePanel(panel);
        
		JLabel clientLabel = new JLabel("Client ID:");
		UIStyling.styleLabel(clientLabel);
		panel.add(clientLabel);
		panel.add(clientID);

		JLabel jobLabel = new JLabel("Job Name:");
		UIStyling.styleLabel(jobLabel);
		panel.add(jobLabel);
		panel.add(jobName);

		JLabel durationLabel = new JLabel("Job Duration:");
		UIStyling.styleLabel(durationLabel);
		panel.add(durationLabel);
		panel.add(duration);

		JLabel deadlineLabel = new JLabel("Job Deadline:");
		UIStyling.styleLabel(deadlineLabel);
		panel.add(deadlineLabel);
		panel.add(deadline);

		JButton submit = new JButton("Submit"); //create submit action button to save data
		JButton back= new JButton("Back"); //create back action button for navigation control
		UIStyling.styleButton(submit);
        UIStyling.styleButton(back);

		add(submit);
		add(back);
		
		JLabel titleLabel = UIStyling.createTitleLabel("Job Owner Form");
        UIStyling.setupFrame(this, panel, titleLabel);

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
		String id = clientID.getText();;
		String name = jobName.getText();
		String durText = duration.getText();
		String ddlText = deadline.getText();

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
		writer.write("Timestamp: " + LocalDateTime.now() + "\n");
		writer.write("Client ID: " + clientID + "\n");
		writer.write("Job Name: " + jobName + "\n");
		writer.write("Job Duration: " + duration + "\n");
		writer.write("Job Deadline: " + deadline + "\n");
		writer.write("---------------------------------\n");

		JOptionPane.showMessageDialog(this, "Job saved successfully!");

		// clear fields after done 
		clientID.setText("");
		jobName.setText("");
		duration.setText("");
		deadline.setText("");

		} catch (IOException ex) {
		JOptionPane.showMessageDialog(this,
		"Error saving file: " + ex.getMessage(),
		"Save Error",
		JOptionPane.ERROR_MESSAGE);
		}
	}
}