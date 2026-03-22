import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

//Main Frame 
public class MainControllerFrame extends JFrame{
	        
	  // text fields - Moontarin
    /*private JTextField timeEstField;
    private JTextField ownerSubmissionField;
    private JTextField overallProgressField;
    private JTextField arrayListRuntimeField;
    private JTextField cancelledJobField;*/

    // labels
    private JLabel timeEstLabel;
    private JLabel ownerSubmissionLabel;
    private JLabel overallProgressLabel;
    private JLabel arrayListRuntimeLabel;
    private JLabel cancelledJobLabel;

    public MainControllerFrame() {
        setTitle("VCRTS - Main Controller");
        setSize(600, 400);
        setLocationRelativeTo(null); // centers window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // title at the top
        JLabel title = UIStyling.createTitleLabel("VCRTS Main Controller");
        add(title, BorderLayout.NORTH);

        // center panel
        JPanel centerPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        UIStyling.stylePanel(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Time Estimation
        timeEstLabel = new JLabel("Time Estimation for Jobs and Vehicles:");
        UIStyling.styleLabel(timeEstLabel);
        
      /*  timeEstField = new JTextField();
        UIStyling.styleTextField(timeEstField);*/

        // Owner Submission
        ownerSubmissionLabel = new JLabel("Owner Submission:");
        UIStyling.styleLabel(ownerSubmissionLabel);
       
      /*  ownerSubmissionField = new JTextField();
        UIStyling.styleTextField(ownerSubmissionField);*/

        // Overall Progress
        overallProgressLabel = new JLabel("Overall Progress:");
        UIStyling.styleLabel(overallProgressLabel);
        
       /* overallProgressField = new JTextField();
        UIStyling.styleTextField(overallProgressField);*/

        // ArrayList Runtime
        arrayListRuntimeLabel = new JLabel("ArrayList Runtime for Specific Job:");
        UIStyling.styleLabel(arrayListRuntimeLabel);
       
       /* arrayListRuntimeField = new JTextField();
        UIStyling.styleTextField(arrayListRuntimeField);*/

        // Cancelled Active Job
        cancelledJobLabel = new JLabel("Cancelled Active Job:");
        UIStyling.styleLabel(cancelledJobLabel);
       
        /*cancelledJobField = new JTextField();
        UIStyling.styleTextField(cancelledJobField);*/

        // add to panel
        centerPanel.add(timeEstLabel);
       // centerPanel.add(timeEstField);
        
        centerPanel.add(ownerSubmissionLabel);
       // centerPanel.add(ownerSubmissionField);
       
        centerPanel.add(overallProgressLabel);
       // centerPanel.add(overallProgressField);
        
        centerPanel.add(arrayListRuntimeLabel);
        //centerPanel.add(arrayListRuntimeField);
       
        centerPanel.add(cancelledJobLabel);
        //centerPanel.add(cancelledJobField);

        add(centerPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainControllerFrame();
    }
}






