import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;


/*Main viewer class to launch program and display GUI window*/
public class VCRTSViewer{
    public static void main(String[]args){
        Jframe frame = new JFrame(); //jframe creates new window fame to display info on screen

        VCRTSPanel Panel = new VCRTSPanel(); //panel creates buttons and text fields in the window
        frame.add(panel);

        final int FRAME_WIDTH=400; //fixed setting so both height and width are constant pixels for how large GUI appears
        final int FRAME_HEIGHT=200;

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Vehicular Cloud Console (VCRTS)") 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //stops program when window is closed
        frame.setVisible(true;) //allows window to be shown on screen 
    }
}