import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/*=====================
Vehicle Owner Frame
======================*/

 class VehicleOwnerFrame extends JFrame{

	 //Subat Wrote This
	 //jframe creates new window frame for our vehicle owner class
	 	
	 	private JTextField ownerIDField = new JTextField(10);
	 	private JTextField makeField = new JTextField(10);
	    private JTextField modelField = new JTextField(10);
	    private JTextField vinField = new JTextField(10);

	    //Label display 
	    private JLabel ownerIDLabel;
	    private JLabel makeLabel;
	    private JLabel modelLabel;
	    private JLabel vinLabel;
	    private JLabel residencyLabel;

	    private JComboBox<String> residencyBox;

	    public VehicleOwnerFrame() {

	        setTitle("Vehicle Owner Information");
	        setSize(600,400);
	        setLocationRelativeTo(null);
	        JPanel panel = new JPanel(new GridLayout(7,2,10,10));
	        UIStyling.stylePanel(panel);
	      
	        //Owner ID
	        ownerIDLabel = new JLabel("Owner ID:");
	        UIStyling.styleLabel(ownerIDLabel);
	        panel.add(ownerIDLabel);

	        ownerIDField = new JTextField();
	        UIStyling.styleTextField(ownerIDField);
	        panel.add(ownerIDField);
	        
	        //Make
	        makeLabel = new JLabel("Make:");
	        UIStyling.styleLabel(makeLabel);
	        panel.add(makeLabel);
	       
	        makeField = new JTextField();
	        UIStyling.styleTextField(makeField);
	        panel.add(makeField);

	        //Model
	        modelLabel = new JLabel("Model:");
	        UIStyling.styleLabel(modelLabel);
	        panel.add(modelLabel);
	        
	        modelField = new JTextField();
	        UIStyling.styleTextField(modelField);
	        panel.add(modelField);


	        //Vin
	        vinLabel = new JLabel("VIN:");
	        UIStyling.styleLabel(vinLabel);
	        panel.add(vinLabel);
	       
	        vinField = new JTextField();
	        UIStyling.styleTextField(vinField);
	        panel.add(vinField);

	        //Avneet
	        //created the Residency Time Dropdown
	        residencyLabel = new JLabel("Residency Time:");
	        UIStyling.styleLabel(residencyLabel);
	        panel.add(residencyLabel);


	        String[] residencyOptions = {
	        		"1 hour",
	                "2 hours",
	                "3 hours",
	                "6 hours",
	                "12 hours",
	                "1 day",
	                "2 days",
	                "3 days"	        
	         };

	        residencyBox = new JComboBox<>(residencyOptions);
	        panel.add(residencyBox);

	        JButton submitButton = new JButton("Submit");
	        JButton backButton = new JButton("Back");
	        UIStyling.styleButton(submitButton);
	        UIStyling.styleButton(backButton);

	        panel.add(submitButton);
	        panel.add(backButton);

	        JLabel titleLabel = UIStyling.createTitleLabel("Vehicle Owner Form");
	        UIStyling.setupFrame(this, panel, titleLabel);

	      //makes labels + component to enter text for all of the required information needed
	        
	        //subat
	        submitButton.addActionListener(e -> saveVehicle());
	        //creates button for submit
	        backButton.addActionListener(e -> {
	            dispose(); //closes frame
	            new RoleSelectionFrame();
	        });

	        setVisible(true);
	    }
     
     //adding each of the labels, buttons, and text fields to panel
     
     //Avneet- I enhanced the submit button functionality to make sure the code runs when the user clicks the "Submit" button
     //the code includes validation, VIN check, timestamp, and auto-clear
     //it allows us to collect all the entered data and saves it to the text file
     


    	 private void saveVehicle() {
   	    	//this code receives the user input from the text fields
   	        String ownerID = ownerIDField.getText();
   	        String make = makeField.getText();
   	        String model = modelField.getText();
   	        String vin = vinField.getText();
   	      //allows us to get the selected value from Residency Time dropdown
   	        String residency = (String) residencyBox.getSelectedItem();
   	        
   	        // first i checked for empty fields
   	        if (ownerID.isEmpty() || make.isEmpty() || model.isEmpty() || vin.isEmpty()) {
   	        	JOptionPane.showMessageDialog(this,
   	        			"All fields must be filled out!",
   	        			"Input Error",
   	        			JOptionPane.ERROR_MESSAGE);
   	        	return;
   	        }
   	        //Then i validated the VIN length (the length should be 17 characters long)
   	        	if (vin.length() != 17) {
   	                JOptionPane.showMessageDialog(this,
   	                        "VIN must be exactly 17 characters.",
   	                        "VIN Error",
   	                        JOptionPane.ERROR_MESSAGE);
   	                return;
   	        	
   	        }

   	        try {
   	        	//allows us to create the FileWriter in append mode (if it is true then we don't have to overwrite the previous data
   	            FileWriter writer = new FileWriter("vehicle_owner_data.txt", true);
   	            
   	            //i wrote each field into the files with labels
   	            writer.write("Timestamp: " + LocalDateTime.now() + "\n");//adds the time stamp
   	            writer.write("Owner ID: " + ownerID + "\n");
   	            writer.write("Make: " + make + "\n");
   	            writer.write("Model: " + model + "\n");
   	            writer.write("VIN: " + vin + "\n");
   	            writer.write("Residency Time: " + residency + "\n");
   	            writer.write("---------------------------------\n");
   	            
   	            //this closes the file to save changes
   	            writer.close();
   	            
   	            //this shows the success message
   	            JOptionPane.showMessageDialog(this,
   	                    "Information saved successfully!");
   	            
   	            //this will clear the fields after the submission if it is successful
   	            ownerIDField.setText("");
   	            makeField.setText("");
   	            modelField.setText("");
   	            vinField.setText("");
   	            residencyBox.setSelectedIndex(0);

   	        } catch (IOException ex) {
   	        	//if the file fails to save, then this message will pop up
   	            JOptionPane.showMessageDialog(this,
   	                    "Error saving file!");
   	        }
    	 }
 }