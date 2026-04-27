import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/*=====================
Vehicle Owner Frame - Subat + Avneet
======================*/

 class VehicleOwnerFrame extends JFrame{

	 //Subat Wrote This
	 //creates window frame for vehicle info
	 
	 //text fields for users to type in info
	 	private JTextField ownerIDField = new JTextField(10);
	 	private JTextField makeField = new JTextField(10);
	    private JTextField modelField = new JTextField(10);
	    private JTextField vinField = new JTextField(10);

	    private VCController vcController;
	    private RoleSelectionFrame roleFrame; //reference to RoleSelectionFrame
	    
	 //labels for each of the text fields 
	    private JLabel ownerIDLabel;
	    private JLabel makeLabel;
	    private JLabel modelLabel;
	    private JLabel vinLabel;
	    private JLabel residencyLabel;

	 //dropdown menu for user
	    private JTextField residencyField = new JTextField(10);
	    private JComboBox<String> residencyBox;
	    
	    public VehicleOwnerFrame(VCController vcController, RoleSelectionFrame roleFrame) {
	    	this.vcController = vcController;
	    	this.roleFrame = roleFrame;
	    	
	    	//sets title and size of window
	        setTitle("Vehicle Owner Information");
	        setSize(600,400);
	        setLocationRelativeTo(null);
	        //grid for organization purposes
	        JPanel panel = new JPanel(new GridLayout(7,2,10,10));
	        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	        UIStyling.stylePanel(panel);
	      
	        //owner ID + input
	        ownerIDLabel = new JLabel("Owner ID:");
	        UIStyling.styleLabel(ownerIDLabel);
	        panel.add(ownerIDLabel);

	        ownerIDField = new JTextField();
	        UIStyling.styleTextField(ownerIDField);
	        panel.add(ownerIDField);
	        
	        //Make label + input
	        makeLabel = new JLabel("Year Made:");
	        UIStyling.styleLabel(makeLabel);
	        panel.add(makeLabel);
	       
	        makeField = new JTextField();
	        UIStyling.styleTextField(makeField);
	        panel.add(makeField);

	        //Model label + input
	        modelLabel = new JLabel("Car Model:");
	        UIStyling.styleLabel(modelLabel);
	        panel.add(modelLabel);
	        
	        modelField = new JTextField();
	        UIStyling.styleTextField(modelField);
	        panel.add(modelField);


	        //vehicle identifcation number label + input
	        vinLabel = new JLabel("17 Character (VIN):");
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


	        String[] residencyUnits = {
	        		"Minute(s)",
	        		"Hour(s)",
	        	    "Day(s)",
	              	"Week(s)",
	                "Month(s)",
	                "Year(s)",
	         };
	        
	        residencyBox = new JComboBox<>(residencyUnits);
	        
	        JPanel residencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	        UIStyling.stylePanel(residencyPanel); // dark background for panel
	        residencyPanel.add(residencyField); //user input
	        residencyPanel.add(residencyBox); // user selection
	        
	        residencyField.setPreferredSize(new Dimension(150, 30)); // width 150px, height 28px
	        residencyField.setForeground(UIStyling.TEXT);
	        residencyField.setBackground(UIStyling.BG_DARK);
	        residencyField.setCaretColor(UIStyling.TEXT);
	        residencyField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(UIStyling.BORDER, 2), // outer border
	                BorderFactory.createEmptyBorder(2, 4, 2, 4)          // padding inside so cursor starts
	        ));

	        
	        panel.add(residencyPanel);
	        
	        JButton submitButton = new JButton("Submit");
	        JButton backButton = new JButton("Back");
	        UIStyling.styleButton(submitButton);
	        UIStyling.styleButton(backButton);
	        panel.add(submitButton);
	        panel.add(backButton);

	        JLabel titleLabel = UIStyling.createTitleLabel("Vehicle Owner Form");
	        UIStyling.setupFrame(this, panel, titleLabel, "Vehicle Owner Information");

	      //makes labels + component to enter text for all of the required information needed
	        
	        //subat
	        //creates button for submit
	        submitButton.addActionListener(e -> saveVehicle());
	        //when user presses back, closes frame and returns to role select page
	        backButton.addActionListener(e -> {
	            dispose(); //closes frame
	            if (roleFrame != null) {
	                roleFrame.setVisible(true); // show existing RoleSelectionFrame
	            } else {
	                new RoleSelectionFrame(vcController, "User");
	            }
	    	});

	    }
     
     
     //Avneet- I enhanced the submit button functionality to make sure the code runs when the user clicks the "Submit" button
     //the code includes validation, VIN check, timestamp, and auto-clear
     //it allows us to collect all the entered data and saves it to the text file

    	 private void saveVehicle() {
   	    	//this code receives the user input from the text fields
   	        String ownerID = ownerIDField.getText().trim();
   	        String make = makeField.getText().trim();
   	        String model = modelField.getText().trim();
   	        String vin = vinField.getText().trim();
   	        
   	        //allows us to get the selected value from Residency Time dropdown
   	        String residencyInput = residencyField.getText().trim();
   	        String residencyUnit = (String) residencyBox.getSelectedItem();
   	       
   	        //M5 change gets from system user for notification logic
   			String systemID = vcController.getCurrentUserID(); //(!!!) root cause for notification link

   	        // first i checked for empty fields
   	        if (ownerID.isEmpty() || make.isEmpty() || model.isEmpty() || vin.isEmpty() || residencyInput.isEmpty()) {
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
   	        	
   	      // Validate residency input: must be positive number
   	         int residencyValue;
   	         try {
   	             residencyValue = Integer.parseInt(residencyInput);
   	             if (residencyValue <= 0) {
   	                 throw new NumberFormatException();
   	             }
   	         } catch (NumberFormatException ex) {
   	             JOptionPane.showMessageDialog(this, "Residency value must be a positive number!", "Input Error",
   	                     JOptionPane.ERROR_MESSAGE);
   	             return;
   	         }

   	         
   	         
   	         //-------- M5 Implementation: Create VehicleOwner and Vehicle objects ------- 
   	         //Subat Wrote This-
   	         //Responsible for core logic when creating vehicle submission and sending it to the system

   	         //Gets data from user input from vehicleownerframe textfield boxes and submit button triggers saveVehicle()
   			//created new owner to be derived (Overloaded for GUI use)
   		
   			VehicleOwner owner = new VehicleOwner( //Vehicle Owner
   				    systemID,
   				    "Client " + systemID,
   				    "client@email.com",
   				    "password123",
   		            residencyValue+ " " + residencyUnit
   				);
   			
   			
   			
   			Vehicle vehicle = new Vehicle(	//Vehicle Object
   		            vin,                      // vehicleID
   		            ownerID,                 // ownerID
   		            model,					//name of car
   		            Integer.parseInt(make),	//year
   		            2.5,  // compute power default
   		            residencyValue,
   		         	residencyUnit,
   		            LocalDateTime.now(),
   		            LocalDateTime.now().plusHours(2),
   		            true
   		    );
   		      	        
   	        //M5 change here that calls VehicleOwner Class method here
   	        owner.submitVehicleToController(vehicle);
   	       //------------------------------------------------------------------------*/        
   	         
   	        
   	        
   	        try {
   	        	//allows us to create the FileWriter in append mode (if it is true then we don't have to overwrite the previous data
   	            FileWriter writer = new FileWriter("vehicle_owner_data.txt", true);
   	            
   	            //i wrote each field into the files with labels
   	            writer.write("Timestamp: " + LocalDateTime.now() + "\n");//adds the time stamp
   	            writer.write("Owner ID: " + ownerID + "\n");
   	            writer.write("Make: " + make + "\n");
   	            writer.write("Model: " + model + "\n");
   	            writer.write("VIN: " + vin + "\n");
   	            writer.write("Residency Time: " + residencyValue + " " + residencyUnit + "\n");
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
   	            residencyField.setText("");
   	            residencyBox.setSelectedIndex(0);

   	        } catch (IOException ex) {
   	        	//if the file fails to save, then this message will pop up
   	            JOptionPane.showMessageDialog(this,
   	                    "Error saving file!");
   	        }
    	 }
 }