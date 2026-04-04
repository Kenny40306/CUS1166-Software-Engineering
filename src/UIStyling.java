import javax.swing.*;
import java.awt.*;

// =====================
// Styling and Layout - Moontarin
//UIstyling was utilized in Role selection frame, Job Owner Frame and Vehical Owner Frame.
//UI styling was used because repeated code everywhere makes styling easy to forget and hard to change later.
// =====================

public class UIStyling {

		//Color elements
	private static final Color NAVY = new Color(0, 41, 111);// deep navy blue alternative (0, 34, 68)
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    
    // ── NEW Colors Dashboard Theme──
    public static final Color BG_DARK    = new Color(18, 22, 36);   // main background
    public static final Color BG_PANEL   = new Color(26, 32, 52);   // text area / panel background
    public static final Color ACCENT     = new Color(64, 156, 255); // blue title/accent
    public static final Color TEXT       = new Color(220, 230, 255);// primary text
    public static final Color BORDER     = new Color(45, 58, 90);   // subtle border


    // ── NEW Fonts Dashboard Theme──
    public static final Font FONT_TITLE  = new Font("Georgia", Font.BOLD, 20);
    public static final Font FONT_BUTTON = new Font("Courier New", Font.BOLD, 11);
    public static final Font FONT_BODY   = new Font("Courier New", Font.PLAIN, 12);
    
    
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
		
		//New: MacOS -----
		button.setOpaque(true);              // REQUIRED 
	    button.setContentAreaFilled(true);   // REQUIRED 
	    button.setBorderPainted(true); //keep border visible
		//----------------------------------
	    
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
	 
	

	 /* =====================================================
	NEW: DASHBOARD STYLING METHODS (UIDashboardStyling)
	===================================================== */

	// Styles a JPanel with dark background
	 public static void styleFrameDark(JFrame frame) {
		 frame.getContentPane().setBackground(BG_DARK);
	 }

	// Styles a JTextArea with dark theme
	 public static void styleTextAreaDark(JTextArea area) {
		 if (area == null) return; /* FIX: prevents NullPointerException */
		 area.setBackground(BG_PANEL);
		 area.setForeground(TEXT);
		 area.setFont(FONT_BODY);
		 area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
	 }

	 // Styles a JScrollPane with dark border
	 public static void styleScrollPaneDark(JScrollPane scrollPane) {
		 scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		 scrollPane.setBackground(BG_DARK);
	 }

	// Creates and returns a styled dashboard title label
	 public static JLabel createDashboardTitle(String text) {
		 JLabel title = new JLabel(text, SwingConstants.CENTER);
		 title.setFont(FONT_TITLE);
		 title.setForeground(ACCENT);
		 title.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
		 return title;
	 }

	 // Styles a button with dark dashboard theme
	 public static void styleDashboardButton(JButton btn) {
		 btn.setBackground(new Color(34, 42, 68));
		 btn.setForeground(TEXT);
		 btn.setFont(FONT_BUTTON);
		 btn.setFocusPainted(false);
		 btn.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(BORDER, 1),
         BorderFactory.createEmptyBorder(4, 10, 4, 10)
				 ));
		 btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	 }

	 public static void styleDashboardPanel(JPanel panel) {
		 panel.setBackground(BG_DARK);
	 }

	 public static void stylePanelDeep(JPanel panel) {
		 panel.setBackground(BG_DARK);
		 for (Component c : panel.getComponents()) {
			 if (c instanceof JPanel) {
				 stylePanelDeep((JPanel) c);
			 }
		 }
	 }
}
