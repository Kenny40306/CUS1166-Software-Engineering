import javax.swing.*;

import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class VehicleOwnerFrame
{
   public VehicleOwnerFrame(){
   
	  //Subat
	   
      JFrame frame = new JFrame(); 
      //jframe creates new window frame for our vehicle owner class

      JLabel ownerIDLabel = new JLabel("Owner ID:");
      JTextField ownerIDField = new JTextField(10);

      JLabel makeLabel = new JLabel("Make:");
      JTextField makeField = new JTextField(10);

      JLabel modelLabel = new JLabel("Model:");
      JTextField modelField = new JTextField(10);
      
      JLabel vinLabel = new JLabel("Vehicle Identification Number: ");
      JTextField vinField = new JTextField(10);
      //makes labels + component to enter text for all of the required information needed
      
      //Avneet
      //created the Residency Time Dropdown
      JLabel residencyLabel = new JLabel("Residency Time:");

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

      JComboBox<String> residencyBox =
              new JComboBox<>(residencyOptions);
      
      //subat
      JButton submitButton = new JButton("Submit");
      //creates button for submit

      JPanel panel = new JPanel();
      panel.add(ownerIDLabel);
      panel.add(ownerIDField);
      panel.add(makeLabel);
      panel.add(makeField);
      panel.add(modelLabel);
      panel.add(modelField);
      panel.add(vinLabel);
      panel.add(vinField);
      panel.add(residencyLabel);
      panel.add(residencyBox);
      panel.add(submitButton);
      
      //adding each of the labels, buttons, and text fields to panel
      
      //Avneet- I enhanced the submit button functionality to make sure the code runs when the user clicks the "Submit" button
      //the code includes validation, VIN check, timestamp, and auto-clear
      //it allows us to collect all the entered data and saves it to the text file
      
      submitButton.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    	    	//this code receives the user input from the text fields
    	        String ownerID = ownerIDField.getText();
    	        String make = makeField.getText();
    	        String model = modelField.getText();
    	        String vin = vinField.getText();
    	      //allows us to get the selected value from Residency Time dropdown
    	        String residency = (String) residencyBox.getSelectedItem();
    	        
    	        // first i checked for empty fields
    	        if (ownerID.isEmpty() || make.isEmpty() || model.isEmpty() || vin.isEmpty()) {
    	        	JOptionPane.showMessageDialog(frame,
    	        			"All fields must be filled out!",
    	        			"Input Error",
    	        			JOptionPane.ERROR_MESSAGE);
    	        	return;
    	        }
    	        //Then i validated the VIN length (the length should be 17 characters long)
    	        	if (vin.length() != 17) {
    	                JOptionPane.showMessageDialog(frame,
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
    	            JOptionPane.showMessageDialog(frame,
    	                    "Information saved successfully!");
    	            
    	            //this will clear the fields after the submission if it is successful
    	            ownerIDField.setText("");
    	            makeField.setText("");
    	            modelField.setText("");
    	            vinField.setText("");
    	            residencyBox.setSelectedIndex(0);

    	        } catch (IOException ex) {
    	        	//if the file fails to save, then this message will pop up
    	            JOptionPane.showMessageDialog(frame,
    	                    "Error saving file!");
    	        }
    	        }
    	    
    	    
    	    });
    
  // =====================
   // Styling and Layout - Moontarin
   // =====================

   // Set panel background
   panel.setBackground(new Color(0, 0, 128));

   // Font styling for labels
   Font labelFont = new Font("Georgia", Font.PLAIN, 16);
   ownerIDLabel.setFont(labelFont);
   ownerIDLabel.setForeground(Color.WHITE);
   makeLabel.setFont(labelFont);
   makeLabel.setForeground(Color.WHITE);
   modelLabel.setFont(labelFont);
   modelLabel.setForeground(Color.WHITE);
   vinLabel.setFont(labelFont);
   vinLabel.setForeground(Color.WHITE);
   residencyLabel.setFont(labelFont);
   residencyLabel.setForeground(Color.WHITE);

   // Button styling
   submitButton.setFont(new Font("Georgia", Font.BOLD, 14));
   submitButton.setForeground(Color.BLACK);

   // Title styling
   JLabel titleLabel = new JLabel("Vehicle Owner Form", JLabel.CENTER);
   titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
   titleLabel.setForeground(Color.BLACK);

   // Frame layout
   frame.setLayout(new BorderLayout());
   frame.add(titleLabel, BorderLayout.NORTH);
   frame.add(panel, BorderLayout.CENTER);

   frame.setSize(600, 500);
   frame.setTitle("Vehicle Owner Information");
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.setVisible(true);
   }
}
