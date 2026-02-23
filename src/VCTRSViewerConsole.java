import javax.swing.SwingUtilities;

/*=====================
Main Class
======================*/

//Kendra Wrote This:
//Main class to run program for execution
public class VCTRSViewerConsole {

    public static void main(String[] args) {
    	
    	SwingUtilities.invokeLater(() -> { //lambda implementation for new instance of RoleSelectionFrame to select either Job or Vehicle Owner
    		new RoleSelectionFrame(); //Initiates GUI frame for window to show options and reduce race conditions / shared thread data
    	});
    }
}
