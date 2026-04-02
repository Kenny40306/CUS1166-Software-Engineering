import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/*========================
Main Class & Description Frame - Kendra  + Subat (added new UI updates for aesthetics)
==========================*/

//Main class to run program for executions
public class VCTRSViewerConsole {

    public static void main(String[] args) {
    	
    	//----M4 Implementation: uses both server and VCController for all frames to interact with each other as central source----
    	Server server = new Server("S001"); //server is needed to store and manage jobs and results
    	VCController vcController = new VCController("VC01", server); //main controller manages vehicle, jobs and communication with server
    	
        //--------------Start socket server using thread in the console------
        new Thread(() -> {
            new VCControllerServer(5000, vcController).start();
        }).start();
        //-------------------------------------------------------------------
        
        //Launch GUI
        SwingUtilities.invokeLater(() -> { //lambda implementation for new instance of Description Frame to show console information
    		new DescriptionFrame(vcController); //Initiates GUI frame for window to show options and reduce race conditions / shared thread data
    	});
    }
}

	class DescriptionFrame extends JFrame {
	   //----- M4 Implementations: all frames will use VCController vcController that shares instance of job / vehicle list and server connection
	   // Ensures all parts of GUI see the same data and back end logic from one existing controller
	   private VCController vcController;
	   // method
	   public DescriptionFrame(VCController vcController) {
	       this.vcController = vcController;
	       setTitle("Welcome - Vehicular Cloud Console"); // main title
	       setSize(600, 400); // set window size
	       setLocationRelativeTo(null); // center screen
	       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       // main panel with dark background using UIStyling
	       JPanel panel = new JPanel(new BorderLayout(15, 15));
	       UIStyling.styleDashboardPanel(panel);
	       panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
	       // top title area
	       JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
	       UIStyling.styleDashboardPanel(titlePanel);
	       // small subtitle above title
	       JLabel subtitle = new JLabel("VEHICULAR CLOUD SYSTEM", SwingConstants.CENTER);
	       subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
	       subtitle.setForeground(UIStyling.ACCENT);
	       // main welcome title
	       JLabel title = UIStyling.createDashboardTitle("Welcome To The Vehicular Cloud Console");
	       titlePanel.add(subtitle, BorderLayout.NORTH);
	       titlePanel.add(title, BorderLayout.CENTER);
	       panel.add(titlePanel, BorderLayout.NORTH);
	       // divider line below title
	       JSeparator separator = new JSeparator();
	       separator.setForeground(UIStyling.ACCENT);
	       separator.setBackground(UIStyling.ACCENT);
	       // wrap separator, description, and prompt together in center panel
	       JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
	       UIStyling.styleDashboardPanel(centerPanel);
	       centerPanel.add(separator, BorderLayout.NORTH);
	       // description text area
	       JTextArea description = new JTextArea(
	           "\nThis application allows clients to submit computational jobs to available vehicles " +
	           "and vehicle owners to rent out their vehicles for processing tasks within the cloud."
	       );
	      
	       description.setEditable(false); // prevents user from changing text
	       description.setLineWrap(true); // prevents edge casing for text
	       description.setFocusable(false); // prevents blinking cursor
	       description.setWrapStyleWord(true); // waits for spacing for line for readability
	       UIStyling.styleTextAreaDark(description); // apply dark theme styling
	       description.setFont(new Font("Georgia", Font.PLAIN, 18)); // override font size to be bigger
	       centerPanel.add(description, BorderLayout.CENTER);
	      
	       // prompt label above the button
	       JLabel promptLabel = new JLabel("Click Continue to get started", SwingConstants.CENTER);
	       promptLabel.setFont(new Font("Arial", Font.ITALIC, 13));
	      
	       promptLabel.setForeground(UIStyling.ACCENT);
	       promptLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
	       centerPanel.add(promptLabel, BorderLayout.SOUTH);
	       panel.add(centerPanel, BorderLayout.CENTER);
	       
	       // bottom button panel
	       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	       UIStyling.styleDashboardPanel(buttonPanel);
	       buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	      
	       // continue button using UIStyling
	       JButton continueBtn = new JButton("Continue");
	       continueBtn.setFont(new Font("Georgia", Font.BOLD, 18));
	       continueBtn.setPreferredSize(new Dimension(160, 42));
	       UIStyling.styleButton(continueBtn);
	       // close this frame and open login frame
	       continueBtn.addActionListener(e -> {
	           dispose(); // close the description frame
	           new UserLoginFrame(vcController); // open role selection frame next
	       });
	      
	      buttonPanel.add(continueBtn);
	      panel.add(buttonPanel, BorderLayout.SOUTH);
	       
	      add(panel);
	      setVisible(true);
	  }
}