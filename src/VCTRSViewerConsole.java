import javax.swing.SwingUtilities;

/*=====================
Main Class
======================*/
public class VCTRSViewerConsole {

    public static void main(String[] args) {
    	
    	SwingUtilities.invokeLater(() -> {
    		new RoleSelectionFrame();
    	});
    }
}
