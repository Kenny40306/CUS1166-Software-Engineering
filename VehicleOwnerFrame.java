import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class VehicleOwnerFrame
{
   public static void main(String[] args)
   {
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
      panel.add(submitButton);
      //adding each of the labels, buttons, and text fields to panel

      frame.add(panel);

      final int FRAME_WIDTH = 600;
      final int FRAME_HEIGHT = 500;
      //made GUI 600 by 500 so labels fit in properly with their text fields

      frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
      frame.setTitle("Vehicle Owner Information");
      //title of the frame is vehicle owner information now
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //program closes when exit
      frame.setVisible(true);
      //window can show on screen 
   }
}
