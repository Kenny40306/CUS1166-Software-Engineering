import javax.swing.*;
import java.awt.*;

// =====================
// Styling and Layout - Moontarin
//UIstyling was utilized in Role selection frame, Job Owner Frame and Vehical Owner Frame.
//UI styling was used because repeated code everywhere makes styling easy to forget and hard to change later.
// =====================

public class UIStyling {

		//Color elements
	private static final Color NAVY = new Color(0, 0, 128);      // deep navy blue alternative (0, 34, 68)
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    
	public static void stylePanel(JPanel panel) {
		// Set panel background- navy blue color
		panel.setBackground(NAVY);
	}

	  // Font styling for labels
	public static void styleLabel(JLabel label) {
		Font labelFont = new Font("Georgia", Font.PLAIN, 16);
		label.setFont(labelFont);
		label.setForeground(WHITE);
	}
	  // Button styling
	public static void styleButton(JButton button) {
		button.setFont(new Font("Georgia", Font.BOLD, 14));
		button.setForeground(BLACK);
		button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BLACK,2)); //light brown borders
	}
	  //Style text fields black text on white background
	public static void styleTextField(JTextField field) {
		field.setFont(new Font("Georgia", Font.PLAIN, 16));
		field.setForeground(BLACK);
		field.setBackground(WHITE); 
		field.setCaretColor(NAVY); // cursor color
        field.setBorder(BorderFactory.createLineBorder(BLACK,2));
    }
	    
	 // Creates and styles a title label
	public static JLabel createTitleLabel(String text) {
	  // Title styling - // This method creates a centered label, applies a larger bold Georgia font, 
	  // sets the text color to black, and then returns the styled label so I can reuse it across different frames.
		JLabel titleLabel = new JLabel(text, JLabel.CENTER);
		titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
		titleLabel.setForeground(BLACK);
		return titleLabel;
	  }
	 
	// Apply styling to the frame and panel -  configures and displays the frames 
	 public static void setupFrame(JFrame frame, JPanel panel, JLabel titleLabel, String frameTitle) {
	  // Frame layout - organizes frames into sections, frame title front and main panel center.
	  frame.setLayout(new BorderLayout());
	  frame.add(titleLabel, BorderLayout.NORTH);
	  frame.add(panel, BorderLayout.CENTER);
	  frame.setSize(600, 400);
	  frame.setTitle(frameTitle);
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  frame.setVisible(true);
	  }
	}
