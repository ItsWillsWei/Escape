/**
 * Escape-
 * 
 * Establishes the JFrame and begins the game
 * @author Will and Tilman
 * @version January 21, 2015
 * 
 * Music:
 * Queen - I Want to Break Free
 * Elvis - Jailhouse Rock
 * 
 * Font: 
 * GunPlay RG - http://www.dafont.com/gunplay.font
 * NoHardEvidence - http://www.dafont.com/no-hard-evidence.font
 * 
 * Troll face:
 * Eduard Khil
 */

//Import the required classes
import java.awt.*;
import javax.swing.*;

public class EscapeMain extends JFrame
{
	/**
	 * Constructor to begin the game
	 */
	public EscapeMain()
	{
		// Sets up the frame for the game
		super("Escape!");
		setResizable(false);

		// Load up the icon image
		setIconImage(Toolkit.getDefaultToolkit().getImage("EscapeIcon.png"));

		// Sets up the JPanel that plays most of the game
		EscapeGrid escapeLayout = new EscapeGrid();
		add(escapeLayout, BorderLayout.CENTER);
	}

	public static void main(String[] escape)
	{
		// Create a new frame
		EscapeMain frame = new EscapeMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// frame should adapt to the panel size
		frame.pack();
		frame.setVisible(true);
	}

}
