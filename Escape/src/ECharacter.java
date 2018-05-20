/**
 * Keeps track and handles all aspects of the in-game character
 * Functions include movement, visuals, checking whether moves are valid
 * @author Tilman and Will
 * @version January 21, 2015
 */

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class ECharacter extends Rectangle
{
	// constant number of pixels the character moves
	private final int MOVE_PIXELS = 10;

	private Image image =
			new ImageIcon("Character.png").getImage();
	private int angle = 0; // Affects the rotation of the image
	private Container container; // Necessary for rotation
	// Enables us to keep track of whether objects are in the specified range
	private Rectangle range;

	/**
	 * Constructor for the character
	 * @param x the x coordinate of the position the character will first assume
	 * @param y the y coordinate of the position the character will first assume
	 * @param container the container
	 */
	public ECharacter(int x, int y, Container container)
	{
		// Matches the size of the character image
		super(x, y, 90, 90);
		angle = 0;
		this.container = container;

		// Create the range rectangle
		range = new Rectangle(this.x - 120, this.y - 120, 300, 300);
	}

	/**
	 * Draws this image in the given Graphics context
	 * @param g the graphics object to draw this image in
	 */
	public void draw(Graphics g)
	{
		// We need a Graphics2D object for rotate
		Graphics2D g2D = (Graphics2D) g;

		// Find the angle in radians and the x and y position of the centre of
		// the object
		double angleInRadians = Math.toRadians(angle);
		int centreX = x + image.getWidth(container) / 2;
		int centreY = y + image.getHeight(container) / 2;

		// Rotate the graphic context, draw the image and then rotate back
		g2D.rotate(angleInRadians, centreX, centreY);
		g.drawImage(image, x, y, container);
		g2D.rotate(-angleInRadians, centreX, centreY);
	}

	/**
	 * Checks whether a given click was in range of the range rectangle
	 * @param point the clicked point
	 * @return whether a click was in range
	 */
	public boolean isInRange(Point point)
	{
		if (range.contains(point))
			return true;
		else
			return false;
	}

	/**
	 * Adds the character into a level at the given coordinates
	 * @param x the given x position
	 * @param y the given y position
	 */
	public void addToStart(int x, int y)
	{
		this.x = x;
		this.y = y;
		// Adjusted to match the range specifications
		range.x = x - 120;
		range.y = y - 120;
	}

	/**
	 * Checks whether the character is able to move
	 * @param levelObjects the array of all usable objects in the level
	 * @param backObjects the array of all background objects in the level
	 * @return whether the character can move
	 */
	public boolean canMove(EObject[] levelObjects, EObject[] backObjects)
	{
		// Checks for any intersections with visible objects
		for (int object = 0; object < levelObjects.length; object++)
		{
			if (levelObjects[object].isVisible()
					// Items can be picked up and therefore walked over
					&& !levelObjects[object].isItem()
					&& this.intersects(levelObjects[object]))
				return false;
		}
		//Checks for any intersections with unusable objects
		for (int object = 0; object < backObjects.length; object++)
		{
			if (this.intersects(backObjects[object]))
				return false;
		}
		return true;
	}

	/**
	 * Move the character left if possible
	 * @param levelObjects array of all usable objects in the current level
	 * @param backObjects array of all unusable objects in the current level
	 */
	public void moveLeft(EObject[] levelObjects, EObject[] backObjects)
	{
		// Sets the angle that specifies where the character faces and
		// moves the character
		angle = 270;
		x -= MOVE_PIXELS;
		range.x -= MOVE_PIXELS;

		// If this was an invalid move, return the object to its previous
		// position before it is appears to be drawn incorrectly
		if (!this.canMove(levelObjects, backObjects))
		{
			x += MOVE_PIXELS;
			range.x += MOVE_PIXELS;
		}
	}

	/**
	 * Move the character right if possible
	 * @param levelObjects array of all usable objects in the current level
	 * @param backObjects array of all unusable objects in the current level
	 */
	public void moveRight(EObject[] levelObjects, EObject[] backObjects)
	{
		// Sets the angle that specifies where the character faces
		angle = 90;
		x += MOVE_PIXELS;
		range.x += MOVE_PIXELS;

		// If this was an invalid move, return the object to its previous
		// position before it is redrawn incorrectly
		if (!this.canMove(levelObjects, backObjects))
		{
			x -= MOVE_PIXELS;
			range.x -= MOVE_PIXELS;
		}
	}

	/**
	 * Move the character up if possible
	 * @param levelObjects array of all usable objects in the current level
	 * @param backObjects array of all unusable objects in the current level
	 */
	public void moveUp(EObject[] levelObjects, EObject[] backObjects)
	{
		// Sets the angle that specifies where the character faces
		angle = 0;

		y -= MOVE_PIXELS;
		range.y -= MOVE_PIXELS;

		// If this was an invalid move, return the object to its previous
		// position before it is redrawn incorrectly
		if (!this.canMove(levelObjects, backObjects))
		{
			y += MOVE_PIXELS;
			range.y += MOVE_PIXELS;
		}
	}

	/**
	 * Move the character down if possible
	 * @param levelObjects array of all usable objects in the current level
	 * @param backObjects array of all unusable objects in the current level
	 */
	public void moveDown(EObject[] levelObjects, EObject[] backObjects)
	{
		// Sets the angle that specifies where the character faces
		angle = 180;
		y += MOVE_PIXELS;
		range.y += MOVE_PIXELS;
		// If this was an invalid move, return the object to its previous
		// position before it is redrawn incorrectly
		if (!this.canMove(levelObjects, backObjects))
		{
			y -= MOVE_PIXELS;
			range.y -= MOVE_PIXELS;
		}

	}
}
