import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// =====================
// Styling and Layout - Moontarin + Subat (added new UI updates for aesthetics)
//UIstyling was utilized in Role selection frame, Job Owner Frame and Vehical Owner Frame.
//UI styling was used because repeated code everywhere makes styling easy to forget and hard to change later.
// =====================

public class UIStyling {
	// ── Original Colors ──
	private static final Color NAVY = new Color(15, 25, 50); // updated to dark navy
	private static final Color BLACK = Color.BLACK;
	private static final Color WHITE = Color.WHITE;
	// ── New Dashboard Theme Colors ──
	public static final Color BG_DARK = new Color(15, 25, 50); // main background
	public static final Color BG_PANEL = new Color(25, 40, 75); // text area / panel background
	public static final Color ACCENT = new Color(76, 201, 240); // light blue accent
	public static final Color TEXT = new Color(200, 220, 255);// primary text
	public static final Color BORDER = new Color(76, 201, 240); // light blue border
	// ── Fonts ──
	public static final Font FONT_TITLE = new Font("Georgia", Font.BOLD, 24);
	public static final Font FONT_BUTTON = new Font("Georgia", Font.BOLD, 13);
	public static final Font FONT_BODY = new Font("Georgia", Font.PLAIN, 13);
	
	// sets panel background to dark navy
	public static void stylePanel(JPanel panel) {
		panel.setBackground(BG_DARK);
	}

	// font styling for labels, white text on dark background
	public static void styleLabel(JLabel label) {
		label.setFont(new Font("Georgia", Font.PLAIN, 18));
		label.setForeground(TEXT);
	}

	// button styling, light blue background with dark text
	public static void styleButton(JButton button) {
		button.setFont(FONT_BUTTON);
		button.setForeground(BG_DARK);
		button.setBackground(ACCENT);
		// required for MacOS
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// hover effect
		button.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				button.setBackground(new Color(56, 181, 220));
			}

			public void mouseExited(java.awt.event.MouseEvent e) {
				button.setBackground(ACCENT);
			}
		});
	}

	// style text fields, dark background with white text and light blue border
	public static void styleTextField(JTextField field) {
		field.setFont(FONT_BODY);
		field.setForeground(WHITE);
		field.setBackground(BG_DARK);
		field.setCaretColor(WHITE);
		field.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
	}

	// creates and styles a title label
	public static JLabel createTitleLabel(String text) {
		// creates a centered label with bold Georgia font in white
		JLabel titleLabel = new JLabel(text, JLabel.CENTER);
		titleLabel.setFont(FONT_TITLE);
		titleLabel.setForeground(BLACK);
		return titleLabel;
	}

	// apply styling to the frame and panel, configures and displays the frames
	public static void setupFrame(JFrame frame, JPanel panel, JLabel titleLabel, String frameTitle) {
		// frame layout - organizes frames into sections, frame title front and main
		// panel center
		frame.setLayout(new BorderLayout());
		frame.add(titleLabel, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.setTitle(frameTitle);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/*
	 * ===================================================== DASHBOARD STYLING
	 * METHODS These methods apply a consistent dark theme across the UI
	 * =====================================================
	 */
	// styles the entire JFrame with a dark background color
	public static void styleFrameDark(JFrame frame) {
		// access the content pane and set background color
		frame.getContentPane().setBackground(BG_DARK);
	}

	// styles a JTextArea with dark theme settings
	public static void styleTextAreaDark(JTextArea area) {
		// prevent crash if JTextArea is not initialized
		if (area == null)
			return;
		// set background color of text area
		area.setBackground(BG_PANEL);
		// set text color for readability on dark background
		area.setForeground(TEXT);
		// apply consistent font style for body text
		area.setFont(FONT_BODY);
		// add padding inside the text area
		area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
	}

	// styles a JScrollPane to match dark theme
	public static void styleScrollPaneDark(JScrollPane scrollPane) {
		// add a subtle border around the scroll pane
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		// set background color to match main frame
		scrollPane.setBackground(BG_DARK);
	}

	// creates and returns a styled title label for dashboard headings
	public static JLabel createDashboardTitle(String text) {
		// center-align the title text
		JLabel title = new JLabel(text, SwingConstants.CENTER);
		// apply larger bold font for titles
		title.setFont(FONT_TITLE);
		// set accent color for highlighting
		title.setForeground(ACCENT);
		// add spacing above and below the title
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
		return title;
	}

	// styles a button with dark dashboard theme
	public static void styleDashboardButton(JButton btn) {
		// set button background color
		btn.setBackground(new Color(34, 42, 68));
		// set text color
		btn.setForeground(TEXT);
		// apply consistent button font
		btn.setFont(FONT_BUTTON);
		// remove default focus border
		btn.setFocusPainted(false);
		// compound border: outer = line border, inner = padding
		btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(4, 10, 4, 10)));
		// change cursor to hand when hovering
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	// styles a single panel with dark background
	public static void styleDashboardPanel(JPanel panel) {
		// set panel background color
		panel.setBackground(BG_DARK);
	}

	// recursively styles a panel and nested panels inside it
	public static void stylePanelDeep(JPanel panel) {
		// apply background color to current panel
		panel.setBackground(BG_DARK);
		// loop through all child components inside the panel
		for (Component c : panel.getComponents()) {
			// if the component is also a panel, apply styling recursively
			if (c instanceof JPanel) {
				stylePanelDeep((JPanel) c);
			}
		}
	}
	
	public static JButton createIconButton(String symbol, Color baseColor, Color hoverColor) {

	    JButton btn = new JButton(symbol);

	    btn.setPreferredSize(new Dimension(45, 25));
	  	    
	    //cross-platform font 
	    Font font = new Font(Font.DIALOG, Font.BOLD, 14);
	    btn.setFont(font);

	    //fallback for unsupported glyphs (macOS safe)
	    if (!font.canDisplay(symbol.charAt(0))) {

	        if ("\u2714".equals(symbol)) {
	            btn.setText("Accept");
	        } else if ("\u2716".equals(symbol)) {
	            btn.setText("Reject");
	        } else {
	            btn.setText(symbol);
	        }
	    }

	    btn.setFocusPainted(false);
	    btn.setBorderPainted(false);
	    btn.setOpaque(true);

	    btn.setForeground(Color.WHITE);
	    btn.setBackground(baseColor);
	    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

	    // hover effect
	    btn.addMouseListener(new MouseAdapter() {

	        @Override
	        public void mouseEntered(MouseEvent e) {
	            btn.setBackground(hoverColor);
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	            btn.setBackground(baseColor);
	        }
	    });
	    return btn;
	}
	
	
	
	// ================= SCROLL BAR STYLING =================	
	
	
	public static void styleScrollPaneCompact(JScrollPane scrollPane) {
		
		// smooth, smaller scroll steps
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
	    scrollPane.getHorizontalScrollBar().setUnitIncrement(10);

	    // optional: make scrollbar slimmer
	    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
	    scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 10));

	    // keep consistent dark styling
	    scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
	    scrollPane.setBackground(BG_DARK);
	
	}
}
