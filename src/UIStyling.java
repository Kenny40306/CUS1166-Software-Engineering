import javax.swing.*;
import java.awt.*;

// =====================
// Styling and Layout - Moontarin
// UIstyling was utilzed in Role selection frame, Job Owner Frame and Vehical Owner Frame.
// UI styling was used becuase repeated code everywhere makes styling easy to forget and hard to change later.
// =====================


public class UIStyling {
	
	public static void stylePanel(JPanel panel) {
		  // Set panel background- navy blue color
		  panel.setBackground(new Color(0, 0, 128));
	}

	  // Font styling for labels
	public static void styleLabel(JLabel label) {
		Font labelFont = new Font("Georgia", Font.PLAIN, 16);
		label.setFont(labelFont);
		label.setForeground(Color.WHITE);
	}
	  // Button styling
	public static void styleButton(JButton button) {
	button.setFont(new Font("Georgia", Font.BOLD, 14));
	button.setForeground(Color.BLACK);
	}
	  //Style text fields black text on white background
	 public static void styleTextField(JTextField field) {
	 field.setFont(new Font("Georgia", Font.PLAIN, 16));
	 field.setForeground(Color.BLACK);
	 field.setBackground(Color.WHITE);
	 }
	    
	 // Creates and styles a title label
	 public static JLabel createTitleLabel(String text) {
	  // Title styling
	  JLabel titleLabel = new JLabel(text, JLabel.CENTER);
	  titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
	  titleLabel.setForeground(Color.BLACK);
	  return titleLabel;
	  }
	 
	// Apply styling to the frame and panel
	 public static void setupFrame(JFrame frame, JPanel panel, JLabel titleLabel, String frameTitle) {
	  // Frame layout
	  frame.setLayout(new BorderLayout());
	  frame.add(titleLabel, BorderLayout.NORTH);
	  frame.add(panel, BorderLayout.CENTER);

	  frame.setSize(700, 500);
	  frame.setTitle(frameTitle);
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  frame.setVisible(true);
	  }
	}

