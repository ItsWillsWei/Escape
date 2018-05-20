/**
 * Handles all aspects of all objects including
 * position, image, and other information
 * @author Tilman and William
 * @version January 21, 2015
 */
import java.awt.*;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

public class EObject extends Rectangle implements ImageObserver
{
	private String name, hoverDescription, clickDescription, useDescription;
	private Image image1, image2, image3;
	private int hostItemNo, hiddenItemNo, toUseItemNo;
	private boolean isUsableItem; // is this object an item?
	private boolean isClickableToChangeImage; // is it clickable?
	private boolean isVisible;

	private int drawingState;
	private int descriptionState;//

	/**
	 * Constructor for background objects
	 */
	public EObject(String name, String description, String image1,
			Point position)
	{
		// We will set its size later once we know the size of the image
		super(position.x, position.y, 0, 0);

		this.name = name;
		this.hoverDescription = description;
		clickDescription = description;
		useDescription = description;
		this.image1 = new ImageIcon("Objects//" + image1).getImage();
		hostItemNo = -1;
		hiddenItemNo = -1;
		toUseItemNo = -1;
		isUsableItem = false;
		isClickableToChangeImage = false;
		isVisible = true;

		this.drawingState = 1;

		// Sets the size of the rectangle created above
		setSize(this.image1.getWidth(this), this.image1.getHeight(this));
	}

	/**
	 * Constructor for regular objects
	 * @overloads the previous Constructor
	 */
	public EObject(String name, String description, String description2,
			String description3, String image1,
			String image2,
			String image3, Point position, int hostItemNumber,
			int hiddenItemNumber, int useInventory,
			boolean isItem, boolean isClickable)
	{
		// We will set its size later once we know the size of the image
		super(position.x, position.y, 0, 0);

		this.name = name;
		this.hoverDescription = description;
		this.clickDescription = description2;
		this.useDescription = description3;
		this.image1 = new ImageIcon("Objects//" + image1).getImage();
		this.image2 = new ImageIcon("Objects//" + image2).getImage();
		this.image3 = new ImageIcon("Objects//" + image3).getImage();
		hostItemNo = hostItemNumber;
		hiddenItemNo = hiddenItemNumber;
		toUseItemNo = useInventory;
		this.isUsableItem = isItem;
		isClickableToChangeImage = isClickable;

		// If the hidden item is not hidden nor a special exception it will be
		// visible
		if (hostItemNo == -1 || name.equals("Wall"))
			isVisible = true;
		else
			isVisible = false;

		this.drawingState = 1;

		// Sets the size of the rectangle created above
		setSize(this.image1.getWidth(this), this.image1.getHeight(this));

	}

	/**
	 * Draws the image in whatever state it is currently in
	 * @param g the graphical component
	 */
	public void draw(Graphics g)
	{
		if (drawingState == 1)
			g.drawImage(image1, x, y, this);
		else if (drawingState == 2)
			g.drawImage(image2, x, y, this);
		else
			g.drawImage(image3, x, y, this);

	}

	/**
	 * Changes the image to be displayed (if possible)
	 */
	public void changeImage()
	{
		// check to see if it can be changed and if it's not
		// a backobject
		if (image2 != null && image3 != null && drawingState == 1)
			drawingState++;
		// if display is 1 or 2, hover is always 1
		if (drawingState <= 2)
			descriptionState = 2;
		// If the drawing state was the last one (if the item has been used),
		// the original description is shown to avoid unrealistic descriptions
		else
			descriptionState = 1;
	}

	/**
	 * Overloads to change the image to a specified image
	 * Only used to change to the last 
	 * @param imageNo the given image number
	 */
	public void changeImage(int imageNo)
	{
		drawingState = imageNo;
		if (drawingState >= 3)
			descriptionState = 3;
	}

	/**
	 * Changes the applicable description
	 * @param descNo the given description index
	 */
	public void changeDescription(int descNo)
	{
		descriptionState = descNo;
	}

	/**
	 * Display the object's name
	 * @param g the graphical component
	 * @param x the x position
	 * @param y the y position
	 */
	public void displayName(Graphics g, int x, int y)
	{
		g.drawString(name, x, y);
	}

	/**
	 * Displays the current description of the selected object
	 * @param g the graphical component
	 * @param x the x position
	 * @param y the y position
	 */
	public void displayDescription(Graphics g, int x, int y)
	{
		if (descriptionState == 1)
			g.drawString(hoverDescription, x, y);
		else if (descriptionState == 2)
			g.drawString(clickDescription, x, y);
		else
			g.drawString(useDescription, x, y);
	}

	/**
	 * Check whether any items are to be revealed upon the click and if so,
	 * reveal them
	 * @param levelObjects the array of interactive objects in the level
	 */
	public void revealSecrets(EObject[] levelObjects)
	{
		// Any hidden item will be released unless it is non-existent
		// or has already been released (indicated by drawing state)
		if (hiddenItemNo != -1 && drawingState != 3)
		{
			changeImage(3);
			levelObjects[hiddenItemNo].changeVisibility();
		}
		// If the image can be changed with a mere click, it will be
		else if (isClickableToChangeImage)
		{
			changeImage();
		}
	}

	/**
	 * Returns the index of the item that can be used on the current item
	 * @return the number of the item that can be used on this item (-1 if N/A)
	 */
	public int toUseItemNo()
	{
		return toUseItemNo;
	}

	/**
	 * Checks whether this object is a usable item
	 * @return whether this object is a usable item
	 */
	public boolean isItem()
	{
		return isUsableItem;
	}

	/**
	 * Change the visibility of the specified object
	 */
	public void changeVisibility()
	{
		this.isVisible = !isVisible;
	}

	/**
	 * Checks whether this object is currently visible
	 * @return whether this object is currently visible
	 */
	public boolean isVisible()
	{
		return isVisible;
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5)
	{
		return false;
	}

}
