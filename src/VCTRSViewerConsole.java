import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/*========================
Main Class & Description Frame - Kendra
==========================*/

//Main class to run program for execution
public class VCTRSViewerConsole {

    public static void main(String[] args) {
    	
    	SwingUtilities.invokeLater(() -> { //lambda implementation for new instance of Description Frame to show console information
    		new DescriptionFrame(); //Initiates GUI frame for window to show options and reduce race conditions / shared thread data
    	});
    }
}

//Description frame for the user's understanding of what VCTRSconsole is
class DescriptionFrame extends JFrame { //inherits JFrame GUI windows for button and panel layouts

	//method
    public DescriptionFrame() {
        setTitle("Welcome - Vehicular Cloud Console"); //main title 
        setSize(600, 400); //set window size
        setLocationRelativeTo(null); //center screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10)); //
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));//proper boarder layout
        UIStyling.stylePanel(panel); 

        // Title
        JLabel title = UIStyling.createTitleLabel("Welcome To The Vehicular Cloud Console");
        UIStyling.styleLabel(title); 
        title.setFont(new Font("Georgia", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // Description text
        JTextArea description = new JTextArea(
                "This application allows clients to submit computational jobs to available vehicles " +
        		"and vehicle owners to rent out their vehicles for processing tasks within the cloud.\n\n" +
                
                "Click Continue To Login."
        );
        
        description.setEditable(false); //prevents user from changing text
        description.setLineWrap(true); //prevents edge casing for text
        description.setFocusable(false); //prevents blinking cursor
        description.setWrapStyleWord(true); //Waits for spacing for line for readability
        description.setFont(new Font("Georgia", Font.PLAIN, 16));
        description.setBackground(new Color(0, 0, 128)); //blue background
        description.setForeground(Color.WHITE); //white text against dark background

        panel.add(description, BorderLayout.CENTER); //center text

        // Continue button
        JButton continueBtn = new JButton("Continue");
        UIStyling.styleButton(continueBtn);
        continueBtn.setFont(new Font("Georgia", Font.BOLD, 18)); //new size button
        continueBtn.addActionListener(e -> {
            dispose();        // Close the description frame
            new RoleSelectionFrame(); // Open role selection frame next
        });

        JPanel buttonPanel = new JPanel(); //
        buttonPanel.setBackground(new Color(0, 0, 128));
        buttonPanel.add(continueBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}