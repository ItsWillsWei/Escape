/**
 * Handles everything that occurs on the game screen such as
 * graphics, game logic, user input etc.
 * @author Tilman and William
 * @version January 21, 2015
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class EscapeGrid extends JPanel implements MouseListener, KeyListener,
		ActionListener
{
	// Program constants usable by all methods
	private final int SCREEN_LENGTH = 1000; // 1024
	private final int SCREEN_HEIGHT = 700; // 768

	// Show screen has numbers corresponding to different screens
	private int showScreen, showInstruction;

	// Used to make it clear which screen is being shown in the paint component
	private final int MAIN_MENU = 0;
	private final int GAME_SCREEN = 1;
	private final int FINISH_SCREEN = 2;
	private final int LEVELS_SCREEN = 3;
	private final int INSTRUCTIONS = 4;
	private final int RECORDS = 5;
	private final int SETTINGS = 6;
	private final int CREDITS = 7;

	private final Dimension GAME_SIZE = new Dimension(1000, 500);
	private final Dimension INVENTORY_SIZE = new Dimension(500, 200);
	private final Dimension DISPLAY_SIZE = new Dimension(500, 200);
	public final Dimension SCREEN_SIZE = new Dimension(SCREEN_LENGTH,
			SCREEN_HEIGHT);

	// Locations of where description messages will occur
	private final Point DISPLAY_NAME = new Point(565, 570);
	private final Point DISPLAY_DESCRIPTION = new Point(565, 600);

	private final int NO_OF_LEVELS = 10;
	private final int NO_OF_INSTRUCTIONS = 6;

	// Game screen images
	private Image inventoryScreen, descriptionScreen, background,
			highlightImage, mainMenu, finalScreen,
			instructionScreen, recordsScreen, settingsScreen, creditsScreen,
			gameOverScreen, til, wil;

	// Game fonts
	private Font font;

	// Initialize all object oriented variables
	private ArrayList<EObject> inventoryObjects = new ArrayList<EObject>(0);
	private EObject[] backObjects, levelObjects;
	private EObject clickedObject;
	private EObject currentObject;

	// Initialize booleans that will be essential to the paint component
	private boolean highlight, showDescription, charDescription,
			outOfRange, somethingInteresting;

	private String levelDescription, playerName;
	private int currentLevel;

	// Initialize arrays that keep track of the record times and holders
	double[] timeRecords = new double[NO_OF_LEVELS + 1];
	String[] recordHolders = new String[NO_OF_LEVELS + 1];
	boolean[] unlockedLevels = new boolean[NO_OF_LEVELS + 1];

	ECharacter character;

	// All audio related stuff
	private boolean musicPlaying;
	private AudioClip backgroundSound, backgroundSound2, currentSound;
	private Icon musicIcon1, musicIcon2, musicIcon3, musicOffIcon1,
			musicOffIcon2, musicOffIcon3, buttonIcon, buttonIcon2, buttonIcon3,
			helpIcon1, helpIcon2, helpIcon3, homeIcon1, homeIcon2, homeIcon3;

	// Allows this class to access the timer and current time
	private Timer timer;
	private int time;

	// All JButtons are initialized here
	private JButton[] levelButtons = new JButton[NO_OF_LEVELS + 1];
	private JButton startButton, helpButton, homeButton,
			soundButton, nextButton, backButton, menuButton,
			instructionsButton, recordsButton, settingsButton,
			creditsButton, unlockLevelsButton;
	private JRadioButton queen, elvis;
	private ButtonGroup songs;

	/**
	 * Initializes needed variables, creates and adds buttons, sets up the main
	 * menu EscapeGrid constructor:
	 */
	public EscapeGrid()
	{
		setPreferredSize(SCREEN_SIZE);
		setBackground(new Color(200, 200, 200));
		setLayout(null);

		// Initial variables
		currentLevel = 1;
		clickedObject = null;
		currentObject = null;
		somethingInteresting = true;
		musicPlaying = true;

		initializeMechanisms();
		// Start the sound
		currentSound.loop();

		// Add the JButtons to the JPanel
		add(startButton);
		add(instructionsButton);
		add(recordsButton);
		add(settingsButton);
		add(creditsButton);
		add(unlockLevelsButton);

		add(nextButton);
		add(backButton);
		add(menuButton);

		add(soundButton);
		add(helpButton);
		add(homeButton);

		// Add actionListeners to buttons to listen for events
		helpButton.addActionListener(this);
		soundButton.addActionListener(this);
		homeButton.addActionListener(this);

		startButton.addActionListener(this);
		instructionsButton.addActionListener(this);
		recordsButton.addActionListener(this);
		settingsButton.addActionListener(this);
		unlockLevelsButton.addActionListener(this);
		creditsButton.addActionListener(this);

		nextButton.addActionListener(this);
		backButton.addActionListener(this);
		menuButton.addActionListener(this);

		// Add mouse listeners and Key Listeners to the game board
		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
		this.addKeyListener(new KeyHandler());
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());
		requestFocusInWindow();

		// Unlock the first level
		unlockedLevels[1] = true;
		showInstruction = 1;

		// Show the main menu
		mainMenu();
	}

	/**
	 * Initialize every object or mechanism essential to the game that only
	 * needs to be declared once
	 */
	private void initializeMechanisms()
	{
		// Initialize the main character
		character = new ECharacter(500, 500, this);

		// Sets game screen images
		inventoryScreen = new ImageIcon("GameScreens//Inventory.png")
				.getImage();
		descriptionScreen = new ImageIcon("GameScreens//Display.png")
				.getImage();
		finalScreen = new ImageIcon("GameScreens//BetweenLevels.png")
				.getImage();
		mainMenu = new ImageIcon("GameScreens//Main-Menu.png").getImage();
		instructionScreen = new ImageIcon("GameScreens//Instructions.png")
				.getImage();
		recordsScreen = new ImageIcon("GameScreens//Records.png").getImage();
		settingsScreen = new ImageIcon("GameScreens//Settings.png").getImage();
		creditsScreen = new ImageIcon("GameScreens//Credits.png").getImage();
		gameOverScreen = new ImageIcon("GameScreens//EndScreen.png")
				.getImage();
		til = new ImageIcon("til1.png").getImage();
		wil = new ImageIcon("wil1.png").getImage();

		highlightImage = new ImageIcon("Highlight.png").getImage();

		// Sets font
		try
		{
			font = Font.createFont(Font.TRUETYPE_FONT,
					new FileInputStream(new File("Fonts//Gunplay Rg.ttf")))
					.deriveFont(Font.PLAIN, 24);
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
			System.out.println("Invalid font");
		}

		// Keeps track of record times in a text file
		Scanner recordFile;
		try
		{
			recordFile = new Scanner(new File("records.txt"));
			for (int level = 1; level <= NO_OF_LEVELS; level++)
			{
				timeRecords[level] = recordFile.nextDouble();
				recordHolders[level] = recordFile.nextLine().trim();
			}
			recordFile.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("The record file has been corrupted.");
		}

		// Initialize Buttons and Icons
		musicIcon1 = new ImageIcon("Buttons//Sound1.png");
		musicIcon2 = new ImageIcon("Buttons//Sound2.png");
		musicIcon3 = new ImageIcon("Buttons//Sound3.png");
		musicOffIcon1 = new ImageIcon("Buttons//SoundOff1.png");
		musicOffIcon2 = new ImageIcon("Buttons//SoundOff2.png");
		musicOffIcon3 = new ImageIcon("Buttons//SoundOff3.png");
		soundButton = new JButton(musicIcon1);
		soundButton.setRolloverIcon(musicIcon2);
		soundButton.setPressedIcon(musicIcon3);
		soundButton.setBounds(850, 5, musicIcon1.getIconWidth(),
				musicIcon1.getIconHeight());

		helpIcon1 = new ImageIcon("Buttons//Question1.png");
		helpIcon2 = new ImageIcon("Buttons//Question2.png");
		helpIcon3 = new ImageIcon("Buttons//Question3.png");
		helpButton = new JButton(helpIcon1);
		helpButton.setRolloverIcon(helpIcon2);
		helpButton.setPressedIcon(helpIcon3);
		helpButton.setBounds(900, 5, 40, 40);

		homeIcon1 = new ImageIcon("Buttons//Home1.png");
		homeIcon2 = new ImageIcon("Buttons//Home2.png");
		homeIcon3 = new ImageIcon("Buttons//Home3.png");
		homeButton = new JButton(homeIcon1);
		homeButton.setRolloverIcon(homeIcon2);
		homeButton.setPressedIcon(homeIcon3);
		homeButton.setBounds(950, 5, 40, 40);

		buttonIcon = new ImageIcon("Buttons//Button1.png");
		buttonIcon2 = new ImageIcon("Buttons//Button2.png");
		buttonIcon3 = new ImageIcon("Buttons//Button3.png");
		startButton = new JButton("START");
		startButton.setFont(font);
		startButton.setIcon(buttonIcon);
		startButton.setRolloverIcon(buttonIcon2);
		startButton.setPressedIcon(buttonIcon3);
		startButton.setHorizontalTextPosition(JButton.CENTER);
		startButton.setVerticalTextPosition(JButton.CENTER);
		startButton.setBounds(200, 400, 200, 50);

		instructionsButton = new JButton("INSTRUCTION");
		instructionsButton.setFont(font);
		instructionsButton.setIcon(buttonIcon);
		instructionsButton.setRolloverIcon(buttonIcon2);
		instructionsButton.setPressedIcon(buttonIcon3);
		instructionsButton.setHorizontalTextPosition(JButton.CENTER);
		instructionsButton.setVerticalTextPosition(JButton.CENTER);
		instructionsButton.setBounds(200, 450, 200, 50);

		recordsButton = new JButton("RECORDS");
		recordsButton.setFont(font);
		recordsButton.setIcon(buttonIcon);
		recordsButton.setRolloverIcon(buttonIcon2);
		recordsButton.setPressedIcon(buttonIcon3);
		recordsButton.setHorizontalTextPosition(JButton.CENTER);
		recordsButton.setVerticalTextPosition(JButton.CENTER);
		recordsButton.setBounds(200, 500, 200, 50);

		settingsButton = new JButton("SETTINGS");
		settingsButton.setFont(font);
		settingsButton.setIcon(buttonIcon);
		settingsButton.setRolloverIcon(buttonIcon2);
		settingsButton.setPressedIcon(buttonIcon3);
		settingsButton.setHorizontalTextPosition(JButton.CENTER);
		settingsButton.setVerticalTextPosition(JButton.CENTER);
		settingsButton.setBounds(200, 550, 200, 50);

		creditsButton = new JButton("CREDITS");
		creditsButton.setFont(font);
		creditsButton.setIcon(buttonIcon);
		creditsButton.setRolloverIcon(buttonIcon2);
		creditsButton.setPressedIcon(buttonIcon3);
		creditsButton.setHorizontalTextPosition(JButton.CENTER);
		creditsButton.setVerticalTextPosition(JButton.CENTER);
		creditsButton.setBounds(200, 600, 200, 50);

		nextButton = new JButton("NEXT");
		nextButton.setFont(font);
		nextButton.setIcon(buttonIcon);
		nextButton.setRolloverIcon(buttonIcon2);
		nextButton.setPressedIcon(buttonIcon3);
		nextButton.setHorizontalTextPosition(JButton.CENTER);
		nextButton.setVerticalTextPosition(JButton.CENTER);
		nextButton.setBounds(500, 400, 200, 50);

		backButton = new JButton("PREVIOUS");
		backButton.setFont(font);
		backButton.setIcon(buttonIcon);
		backButton.setRolloverIcon(buttonIcon2);
		backButton.setPressedIcon(buttonIcon3);
		backButton.setHorizontalTextPosition(JButton.CENTER);
		backButton.setVerticalTextPosition(JButton.CENTER);
		backButton.setBounds(100, 600, 200, 50);

		menuButton = new JButton("MAIN MENU");
		menuButton.setFont(font);
		menuButton.setIcon(buttonIcon);
		menuButton.setRolloverIcon(buttonIcon2);
		menuButton.setPressedIcon(buttonIcon3);
		menuButton.setHorizontalTextPosition(JButton.CENTER);
		menuButton.setVerticalTextPosition(JButton.CENTER);
		menuButton.setBounds(400, 600, 200, 50);

		unlockLevelsButton = new JButton("Unlock Levels");
		unlockLevelsButton.setFont(font);
		unlockLevelsButton.setIcon(buttonIcon);
		unlockLevelsButton.setRolloverIcon(buttonIcon2);
		unlockLevelsButton.setPressedIcon(buttonIcon3);
		unlockLevelsButton.setHorizontalTextPosition(JButton.CENTER);
		unlockLevelsButton.setVerticalTextPosition(JButton.CENTER);
		unlockLevelsButton.setBounds(700, 600, 200, 50);

		// Declare the level buttons in sets of five to make them look organized
		// on the screen
		for (int level = 1; level <= NO_OF_LEVELS && level < 6; level++)
		{
			levelButtons[level] = new JButton("Level " + level);
			levelButtons[level].setBounds(level * 180 - 100, 300, 100, 100);
			levelButtons[level].addActionListener(this);
			add(levelButtons[level]);
			levelButtons[level].setVisible(false);
		}

		// Declare the next set of buttons
		for (int level = 6; level <= NO_OF_LEVELS; level++)
		{
			levelButtons[level] = new JButton("Level " + level);
			// Level -5 is used because the row is restarted
			levelButtons[level].setBounds((level - 5) * 180 - 100, 500, 100,
					100);
			levelButtons[level].addActionListener(this);
			add(levelButtons[level]);
			levelButtons[level].setVisible(false);
		}

		// Initialize radio buttons
		songs = new ButtonGroup();
		queen = new JRadioButton("Queen - I want to break free", true);
		queen.setBounds(550, 200, 400, 50);
		queen.addActionListener(this);
		add(queen);
		songs.add(queen);
		elvis = new JRadioButton("Elvis - Jailhouse Rock", false);
		elvis.setBounds(550, 250, 400, 50);
		elvis.addActionListener(this);
		add(elvis);
		songs.add(elvis);

		hideSongButtons();

		// Initialize sound and timer objects
		timer = new Timer(100, new TimerEventHandler());
		backgroundSound = Applet
				.newAudioClip(getCompleteURL("Sounds//Song.wav"));
		backgroundSound2 = Applet
				.newAudioClip(getCompleteURL("Sounds//Song2.wav"));
		currentSound = backgroundSound;

	}

	/**
	 * Starts up the main menu and changes which buttons are visible and which
	 * are not so that the user can choose what to do next
	 */
	private void mainMenu()
	{
		showScreen = MAIN_MENU;
		// Show main menu buttons
		startButton.setVisible(true);
		instructionsButton.setVisible(true);
		recordsButton.setVisible(true);
		settingsButton.setVisible(true);
		creditsButton.setVisible(true);

		// Hide all other buttons
		unlockLevelsButton.setVisible(false);
		nextButton.setVisible(false);
		backButton.setVisible(false);
		menuButton.setLocation(400, 600);
		menuButton.setVisible(false);
		hideLevelButtons();

		this.requestFocusInWindow();
		repaint(0);
	}

	/**
	 * Hides all buttons on the main menu screen
	 */
	private void hideMainMenuButtons()
	{
		startButton.setVisible(false);
		instructionsButton.setVisible(false);
		recordsButton.setVisible(false);
		settingsButton.setVisible(false);
		creditsButton.setVisible(false);
	}

	/**
	 * Hides the radio buttons for song selection in the settings menu
	 */
	private void hideSongButtons()
	{
		queen.setVisible(false);
		elvis.setVisible(false);
	}

	/**
	 * Shows the radio buttons for song selection in the settings menu
	 */
	private void showSongButtons()
	{
		queen.setVisible(true);
		elvis.setVisible(true);
	}

	/**
	 * Hides the level choice buttons in the start menu
	 */
	private void hideLevelButtons()
	{
		for (int level = 1; level <= NO_OF_LEVELS; level++)
		{
			levelButtons[level].setVisible(false);
		}
	}

	/**
	 * Displays the level screen and shows the level choice buttons in the start
	 * menu
	 */
	private void chooseLevel()
	{
		showScreen = LEVELS_SCREEN;
		for (int level = 1; level <= NO_OF_LEVELS; level++)
		{
			levelButtons[level].setVisible(true);
		}
	}

	/**
	 * Begins the chosen level by reading in the corresponding text file
	 */
	private void newLevel()
	{
		showScreen = GAME_SCREEN;
		time = 0;

		Scanner fileIn;
		try
		{
			// Reads in level information, such as level name, background image,
			// and number of objects
			fileIn = new Scanner(new File("Levels//Level" + currentLevel
					+ ".txt"));
			levelDescription = fileIn.nextLine(); // 1st line
			String backgroundName = fileIn.nextLine();
			background = new ImageIcon(backgroundName).getImage();
			backObjects = new EObject[fileIn.nextInt()];
			levelObjects = new EObject[fileIn.nextInt()];

			// Initialize the character's starting point
			character.addToStart(fileIn.nextInt(), fileIn.nextInt());

			fileIn.nextLine(); // Dummy statement

			// Create background Objects
			for (int object = 0; object < backObjects.length; object++)
			{
				fileIn.nextLine();
				backObjects[object] = new EObject(fileIn.nextLine().trim(),
						fileIn
								.nextLine().trim(), fileIn.nextLine().trim(),
						new Point(
								fileIn.nextInt(), fileIn.nextInt()));
				fileIn.nextLine();
			}

			// Create interactive Objects
			for (int object = 0; object < levelObjects.length; object++)
			{
				fileIn.nextLine(); // Dummy statement

				// Make the objects
				levelObjects[object] = new EObject(fileIn.nextLine().trim(), // Name
																				// of
																				// object
						fileIn.nextLine().trim(), // Description 1
						fileIn.nextLine().trim(), // Description 2
						fileIn.nextLine().trim(), // Description 3
						fileIn.nextLine().trim(), // Accompanying image1
						fileIn.nextLine().trim(), // Accompanying image2
						fileIn.nextLine().trim(), // Accompanying image3
						new Point(fileIn.nextInt(), fileIn.nextInt()), // Position
																		// of
																		// object
						fileIn.nextInt(), // host item number
						fileIn.nextInt(), // hidden item number
						fileIn.nextInt(), fileIn.nextBoolean(), // isItem
						fileIn.nextBoolean()); // isClickable
				fileIn.nextLine(); // Dummy statement

			}
			fileIn.close();
		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
			System.out.println("Invalid level file");
		}

		// Start the level timer
		timer.start();

	}// newLevel method

	/**
	 * Stops the current level, clears remaining inventory objects, Stops the
	 * timer, checks for new records
	 */
	private void levelOver()
	{

		timer.stop();

		inventoryObjects.clear();
		// Update the current records if necessary
		if (time / 10.0 < timeRecords[currentLevel])
		{
			// Get the player name and ensure it is valid
			do
			{
				playerName = JOptionPane
						.showInputDialog(
								this,
								"You beat the record time!\nWith a maximum of 5 characters,\nPlease enter your name/initials:",
								JOptionPane.INPUT_VALUE_PROPERTY);
			}
			while (playerName == null || playerName.equals("")
					|| playerName.length() > 5);
					
			// Overwrite the records text file
			PrintWriter overwriteRecord;
			try
			{
				// Need to adjust println statements to match a certain formats
				overwriteRecord = new PrintWriter(new FileWriter(
						"records.txt"));
				int levelIndex = 1;
				while (levelIndex < currentLevel)
				{
					overwriteRecord.println(timeRecords[levelIndex] + " "
							+ recordHolders[levelIndex]);
					levelIndex++;
				}
				overwriteRecord.println(time / 10.0 + " " + playerName);
				levelIndex++;
				while (levelIndex <= NO_OF_LEVELS)
				{
					overwriteRecord.println(timeRecords[levelIndex] + " "
							+ recordHolders[levelIndex]);
					levelIndex++;
				}
				overwriteRecord.close();
				timeRecords[currentLevel] = time / 10.0;
				recordHolders[currentLevel] = playerName;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("Record file corrupted.");
			}
			// Return the player's name to null so it will not carry over into
			// the next level
			playerName = null;
		}
		else
			JOptionPane.showMessageDialog(this, "You escaped level "
					+ currentLevel
					+ " in " + (time / 10.0) + " seconds", "Congratulations",
					JOptionPane.WARNING_MESSAGE);

		// Remove any messages from the last level and unlock the next level
		showDescription = false;

		showScreen = FINISH_SCREEN;
		currentLevel++;
		showEndLevelScreen();
	}

	/**
	 * Show the screen visible when a level is completed This could be an in
	 * between screen or the 'Game Over' screen
	 */
	private void showEndLevelScreen()
	{
		// If the previous level was not the last one, enable an option to go
		// directly to the next one
		if (currentLevel - 1 < NO_OF_LEVELS)
		{
			unlockedLevels[currentLevel] = true;
			nextButton.setVisible(true);
			menuButton.setLocation(300, 400);
		}
		else
			menuButton.setLocation(400, 500);
		menuButton.setVisible(true);
		this.requestFocusInWindow();
	}

	/**
	 * Adds a given object to the inventory array
	 * @param currentObject the currentObject
	 */
	private void addToInventory(EObject currentObject)
	{
		inventoryObjects.add(currentObject);
		// Set the x position of the last inventory object added to number
		inventoryObjects.get(inventoryObjects.size() - 1).x = (inventoryObjects
				.size() - 1) * 50 + 50;
		inventoryObjects.get(inventoryObjects.size() - 1).y = 550;
	}

	/**
	 * Gets the URL needed for audio clips
	 * @param fileName the name of the audio file
	 * @return the complete URL of the audio clip
	 */
	private URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Repaint the game screen's drawing panel
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Determines which screen to show
		switch (showScreen)
		{
		// Main menu
		case MAIN_MENU:
		{
			g.drawImage(mainMenu, 0, 0, this);
			break;
		}
		// Level (in-game)
		case GAME_SCREEN:
		{
			// Draws the game screen
			g.drawImage(background, 0, 0, this);

			// Draw the inventory screen
			g.clearRect(0, 500, 500, 0);
			g.drawImage(inventoryScreen, 0, 500, this);
			if (highlight)
				g.drawImage(highlightImage, clickedObject.x, clickedObject.y,
						this);

			// Draws inventory objects
			if (inventoryObjects != null)
			{
				for (int objectNo = 0; objectNo < inventoryObjects
						.size(); objectNo++)
					inventoryObjects.get(objectNo).draw(g);
			}

			// Draws all background objects
			for (int object = 0; object < backObjects.length; object++)
				backObjects[object].draw(g);

			// Draws all level objects provided they should be visible
			for (int object = 0; object < levelObjects.length; object++)
			{
				if (levelObjects[object].isVisible())
					levelObjects[object].draw(g);
			}

			// Draw everything contained in the descriptions screen
			g.drawImage(descriptionScreen, 500, 500, this);
			// Display Out Of Range message
			if (outOfRange)
			{
				g.setFont(font);
				g.clearRect(500, 500, 500, 200);
				g.drawImage(descriptionScreen, 500, 500, this);
				g.setColor(Color.black);
				g.drawString("Out of range...", DISPLAY_DESCRIPTION.x,
						DISPLAY_DESCRIPTION.y);
			}
			// Show the description of a currently clicked object
			if (showDescription && currentObject != null)
			{
				g.clearRect(500, 500, 500, 200);
				g.drawImage(descriptionScreen, 500, 500, this);
				g.setColor(Color.WHITE);
				g.setFont(font);
				currentObject.displayName(g, DISPLAY_NAME.x,
						DISPLAY_NAME.y);
				g.setFont(font);
				g.setColor(Color.BLACK);
				currentObject.displayDescription(g, DISPLAY_DESCRIPTION.x,
						DISPLAY_DESCRIPTION.y);
			}
			// When something in range that isn't an object is clicked, nothing
			// should be displayed in the description screen
			else if (charDescription)
			{
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString("You", DISPLAY_NAME.x,
						DISPLAY_NAME.y);
			}
			else if (!outOfRange)
			{
				g.clearRect(500, 500, 500, 200);
				g.drawImage(descriptionScreen, 500, 500, this);
			}

			// Display Nothing Interesting Happens message
			if (!somethingInteresting)
			{
				g.setFont(font);
				g.clearRect(500, 500, 500, 200);
				g.drawImage(descriptionScreen, 500, 500, this);
				g.setColor(Color.black);
				g.drawString("Nothing interesting happens...",
						DISPLAY_DESCRIPTION.x,
						DISPLAY_DESCRIPTION.y);
			}
			g.setFont(font);
			g.setColor(Color.black);
			g.drawString("Inventory", 197, 530);
			g.drawString("Description", 689, 530);

			// Draw the level number and the current time
			g.setFont(font);
			g.setColor(Color.YELLOW);
			g.drawString("Level: " + currentLevel, 475, 32);
			g.drawString("Time: " + (time / 10.0), 580, 32);
			g.drawString(levelDescription, 50, 32);

			// Draw the character on top of everything else
			character.draw(g);

			break;
		}
		// In between levels screen
		case FINISH_SCREEN:
		{
			// IF the previous completed level was not the last one,
			if (currentLevel - 1 < NO_OF_LEVELS)
				g.drawImage(finalScreen, 0, 0, this);
			else
				g.drawImage(gameOverScreen, 0, 0, this);
			// Display the record from the previous level, which is the level
			// that was just played
			g.setFont(font);
			g.setColor(Color.YELLOW);
			g.drawString("Record Time for Level " + (currentLevel - 1) + ": "
					+ timeRecords[currentLevel - 1] + " seconds by "
					+ recordHolders[currentLevel - 1], 200, 300);
			break;
		}
		// Choose your level screen
		case LEVELS_SCREEN:
		{
			// Display a message at the top
			g.drawImage(finalScreen, 0, 0, this);
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString("Please choose your level:", 100, 180);

			// If a level is not unlocked, it is outlined in red
			for (int level = 1; level <= NO_OF_LEVELS && level < 6; level++)
			{
				if (!unlockedLevels[level])
				{
					g.setColor(Color.RED);
					g.fillRect(180 * level - 110, 290, 120, 120);
				}
			}

			for (int level = 6; level <= NO_OF_LEVELS; level++)
			{
				if (!unlockedLevels[level])
				{
					g.setColor(Color.RED);
					g.fillRect(180 * (level - 5) - 110, 490, 120, 120);
				}
			}
			break;
		}
		// Instruction screen
		case INSTRUCTIONS:
		{
			g.drawImage(instructionScreen, 0, 0, this);
			g.setFont(font);
			g.setColor(Color.WHITE);

			// Show how to move
			if (showInstruction == 1)
			{
				g.drawString("Move using the arrow keys", 150, 300);
				g.drawImage((new ImageIcon("Instructions//Arrow-Keys.png")
						.getImage()), 500, 250, this);
			}
			// Show how to hover and view descriptions of objects
			else if (showInstruction == 2)
			{
				g.drawString(
						"For a brief description, hover over an object with your mouse",
						150, 300);
				g.drawImage((new ImageIcon("Instructions//Key-hover.png")
						.getImage()), 200, 390, this);
				g.drawImage((new ImageIcon("Instructions//Description.png")
						.getImage()), 450, 350, this);
			}
			// Show how to interact with objects
			else if (showInstruction == 3)
			{
				g.drawString(
						"Click on an object to interact with it", 50, 250);
				g.drawString(
						"You can pick up some objects by clicking on them",
						50, 300);
				g.drawString(
						"or by standing on them and pressing the space bar",
						50, 350);
				g.drawImage((new ImageIcon("Objects//Key.png").getImage()),
						675, 260,
						this);
				g.drawImage((new ImageIcon("Instructions//Cursor.png")
						.getImage()), 700, 275, this);
				g.drawImage((new ImageIcon("Instructions//Space.png")
						.getImage()), 450, 400, this);

			}
			// Show how to use inventory objects
			else if (showInstruction == 4)
			{
				g.drawString(
						"Use these types of items by clicking on them in the inventory",
						50, 250);
				g.drawString(
						"and then click on the next object or area to use it on",
						50, 300);
				g.drawImage((new ImageIcon("Instructions//Inventory.png")
						.getImage()), 100, 350, this);

				g.drawImage((new ImageIcon("Objects//Trapdoor1.png")
						.getImage()), 725, 400, this);
				g.drawImage((new ImageIcon("Instructions//Cursor.png")
						.getImage()), 800, 475, this);
			}
			// Show last messages
			else if (showInstruction == 5)
				g.drawString("Have fun and Escape!", 370, 250);
			break;
		}
		// Records screen
		case RECORDS:
		{
			g.drawImage(recordsScreen, 0, 0, this);

			// Display the record time and record holder of levels 1-5 if
			// they have been unlocked
			for (int level = 1; level <= NO_OF_LEVELS && level < 6; level++)
			{
				// Display a square
				g.setColor(Color.RED);
				g.fillRect(180 * level - 100, 250, 100, 100);
				// Display text indicating which level is shown (above the
				// square)
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString("Level " + level, 180 * level - 100, 240);

				// If the level has been unlocked, display record for it
				if (unlockedLevels[level])
				{
					g.drawString(timeRecords[level] + "s", 180 * level - 90,
							290);
					g.drawString("By " + recordHolders[level],
							180 * level - 134,
							332);
				}
				// If the level has not been unlocked records are not applicable
				else
					g.drawString("N/A", 180 * level - 90, 300);
			}

			for (int level = 6; level <= NO_OF_LEVELS; level++)
			{
				// Display a square
				g.setColor(Color.RED);
				g.fillRect(180 * (level - 5) - 100, 450, 100, 100);
				// Display text indicating which level is shown (above the
				// square)
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString("Level " + level, 180 * (level - 5) - 100, 440);
				// If the level has been unlocked, display record for it
				if (unlockedLevels[level])
				{
					g.drawString(timeRecords[level] + "s",
							180 * (level - 5) - 90, 490);
					g.drawString("By " + recordHolders[level],
							180 * (level - 5) - 134, 532);
				}
				// If the level has not been unlocked records are not applicable
				else
					g.drawString("N/A", 180 * (level - 5) - 90, 500);
			}
			break;
		}
		// Settings screen
		case SETTINGS:
		{
			g.drawImage(settingsScreen, 0, 0, this);
			g.setFont(font);
			g.setColor(Color.BLACK);
			g.drawString("Song Choices", 550, 185);
			break;
		}
		// Display credits in the credits screen
		case CREDITS:
		{
			g.drawImage(creditsScreen, 0, 0, this);
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawImage(til, 200, 175, this);
			g.drawImage(wil, 600, 150, this);
			g.drawString("Til 'n' Wil", 480, 350);
			g.drawString("Game by Tilman Lindigger and Will Wei", 400, 550);
			g.drawString("Inspiration from Mr. Ridout", 50, 50);
			g.drawString("Music by Queen and Elvis", 50, 100);
			g.drawString("\u00A9 2015", 700, 600);
			break;
		}
		}
	}// paint component method

	// Inner class to handle mouse events
	private class MouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent event)
		{
			Point clickedPoint = event.getPoint();

			if (showScreen == GAME_SCREEN)
			{
				repaint(0);
				showDescription = false;
				somethingInteresting = true;

				// if the mouse is in the description screen
				// this stops annoying out of range messages
				if (clickedPoint.y > 500 && clickedPoint.x > 500)
				{
					outOfRange = false;
					repaint(0);
					return;
				}
				// If the player clicks in the inventory
				else if (clickedPoint.y > 500 && clickedPoint.x < 500)
				{
					// if an inventory object is selected
					if (currentObject != null
							&& currentObject.contains(clickedPoint))
					{
						// De-select the object if it was what it was before
						if (clickedObject == currentObject)
						{
							clickedObject = null;
							highlight = false;
						}
						// Select an inventory item
						else
						{
							clickedObject = currentObject;
							showDescription = true;
							highlight = true;
						}
						return;
					}
				}
				// If what you clicked on it is in range of the character
				else if (character.isInRange(clickedPoint))
				{
					outOfRange = false;

					// If an object is clicked
					if (currentObject != null)
					{
						showDescription = true;
						// If the object is an item it will be added to the
						// inventory
						if (currentObject.isItem())
						{
							addToInventory(currentObject);
							currentObject.changeVisibility();
							currentObject = null;
						}
						// If the right inventory object that was clicked and
						// used on level object, something will happen
						else if (currentObject.toUseItemNo() >= 0
								&& ((clickedObject != null
								&& clickedObject == levelObjects[currentObject
										.toUseItemNo()])))
						{
							inventoryObjects.remove(clickedObject);
							clickedObject = null;
							highlight = false;

							// Reveal the hidden item if possible
							currentObject.revealSecrets(levelObjects);

							// If the last object is used, the level is finished
							// We must write the last level object last in the text file
							if (levelObjects[levelObjects.length - 1] == currentObject)
							{
								currentObject.changeImage(3);
								levelOver();
							}
						}
						// If the wrong item is selected
						else if (clickedObject != null)
						{
							somethingInteresting = false;
							clickedObject = null;
							highlight = false;
							return;
						}
						// If it can be changed just by clicking on it
						else if (currentObject
								.toUseItemNo() == -1)
							currentObject.revealSecrets(levelObjects);
						else
							currentObject.changeImage();
					}
					// If an empty space is clicked nothing interesting occurs
					else if (clickedObject != null)
					{
						clickedObject = null;
						highlight = false;
						somethingInteresting = false;
					}

					repaint(0);
					return;

				}
				// If the click occurred outside the character's range,
				// the description box will be cleared
				else
				{
					repaint(0);
					outOfRange = true;
				}
			}
		}
	}

	private class MouseMotionHandler extends MouseMotionAdapter
	{
		@Override
		public void mouseMoved(MouseEvent event)
		{
			if (showScreen == GAME_SCREEN)
			{
				Point selectedPoint = event.getPoint();

				somethingInteresting = true;
				outOfRange = false;
				charDescription = false;

				// If the mouse is in the game screen
				if (selectedPoint.x >= 0 && selectedPoint.x <= 1000
						&& selectedPoint.y >= 0 && selectedPoint.y <= 500)
				{

					// Goes through all background objects and return if over an
					// object
					for (int object = 0; object < backObjects.length; object++)
					{
						if (backObjects[object].contains(selectedPoint))
						{
							currentObject = backObjects[object];
							showDescription = true;
							repaint(0);
							return;
						}
					}

					// Goes through all level objects and return if over an
					// object
					for (int object =
							levelObjects.length - 1; object >= 0; object--)
					{
						// If the user hovers on a visible object
						if (levelObjects[object].contains(selectedPoint)
								&& levelObjects[object].isVisible())
						{

							currentObject = levelObjects[object];
							currentObject.changeDescription(1);
							showDescription = true;

							repaint(0);
							return;
						}
					}
				}
				// If the mouse isn't in the game screen (inventory or
				// description)
				else
				{
					// Display inventory hover messages and return if over
					// object
					for (int object = 0; object < inventoryObjects.size(); object++)
					{
						if (inventoryObjects.get(object)
								.contains(selectedPoint))
						{

							currentObject = inventoryObjects.get(object);
							showDescription = true;
							repaint(0);
							return;
						}
					}

				}

				// Add character hover text "You"
				if (character.contains(selectedPoint))
				{
					showDescription = false;
					charDescription = true;
					repaint(0);
					return;
				}

				// If hovering over nothing, nothing is displayed
				currentObject = null;
				showDescription = false;
				repaint(0);
			}
		}

	}

	private class KeyHandler extends KeyAdapter
	{
		/**
		 * Responds to a keyPressed event by moving the character in response
		 * @param event information about the key pressed event
		 */
		public void keyPressed(KeyEvent event)
		{
			// If the action occurred while a level is being played
			if (showScreen == GAME_SCREEN)
			{
				// Do not display any character message when movingg
				charDescription = false;

				// Move into the specified direction
				if (event.getKeyCode() == KeyEvent.VK_LEFT)
					character.moveLeft(levelObjects, backObjects);
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
					character.moveRight(levelObjects, backObjects);
				else if (event.getKeyCode() == KeyEvent.VK_DOWN)
					character.moveDown(levelObjects, backObjects);
				else if (event.getKeyCode() == KeyEvent.VK_UP)
					character.moveUp(levelObjects, backObjects);
				else if (event.getKeyCode() == KeyEvent.VK_SPACE)
				{
					for (int object = 0; object < levelObjects.length; object++)
						if (character.contains(levelObjects[object])
								&& levelObjects[object].isItem())
						{
							addToInventory(levelObjects[object]);
							levelObjects[object].changeVisibility();
							currentObject = null;
						}
				}
				repaint(0);
			}
		}
	}

	/**
	 * @param event information about button events
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();

		// Start the game by hiding menu buttons and advancing to the levels
		// screen
		if (source == startButton)
		{
			hideMainMenuButtons();
			chooseLevel();
		}
		// Tells the paintComponent to draw the next screen
		else if (source == nextButton)
		{
			// If in the instruction screen, draw the next instructions
			if (showScreen == INSTRUCTIONS)
			{
				showInstruction++;
				// Show next instruction screen
				if (showInstruction == NO_OF_INSTRUCTIONS - 1)
					nextButton.setVisible(false);
				// Show the backButton if going forwards from the first
				// instruction screen
				if (showInstruction > 1)
					backButton.setVisible(true);

			}
			// In in between games, proceed to the next level and hide the
			// buttons
			else
			{
				nextButton.setVisible(false);
				menuButton.setVisible(false);
				newLevel();
			}
		}
		// In the instruction screen, draw the previous instructions
		else if (source == backButton)
		{
			showInstruction--;
			// Cannot show previous screen if already at first instruction
			// screen
			if (showInstruction == 1)
				backButton.setVisible(false);
			// Show the nextButton if going back from the last instruction
			// screen
			if (showInstruction < NO_OF_INSTRUCTIONS)
				nextButton.setVisible(true);
		}
		// Displays the main menu
		else if (source == menuButton)
		{
			// Hides all non-menu buttons and set the instruction screen image
			// to 1
			showInstruction = 1;
			hideSongButtons();
			nextButton.setVisible(false);
			nextButton.setBounds(500, 400, 200, 50);

			mainMenu();
		}
		// Displays the instruction screen
		else if (source == instructionsButton)
		{
			showScreen = INSTRUCTIONS;
			// Position and display the nextButton
			nextButton.setBounds(700, 600, 200, 50);
			nextButton.setVisible(true);
			hideMainMenuButtons();
			menuButton.setVisible(true);
		}
		// Displays the records screen
		else if (source == recordsButton)
		{
			showScreen = RECORDS;
			hideMainMenuButtons();
			menuButton.setVisible(true);
		}
		// Displays the settings screen
		else if (source == settingsButton)
		{
			showScreen = SETTINGS;
			hideMainMenuButtons();
			// Allows players to unlock all levels(Ridout mode)
			if (!unlockedLevels[NO_OF_LEVELS])
				unlockLevelsButton.setVisible(true);
			menuButton.setVisible(true);
			// Allows players to choose the background song
			showSongButtons();

		}
		// Unlock all levels (Ridout mode)
		else if (source == unlockLevelsButton)
		{
			for (int level = 2; level <= NO_OF_LEVELS; level++)
				unlockedLevels[level] = true;
			remove(unlockLevelsButton);
		}
		// Displays the credits screen
		else if (source == creditsButton)
		{
			showScreen = CREDITS;
			hideMainMenuButtons();
			menuButton.setVisible(true);

		}
		// Toggles the background music
		else if (source == soundButton)
		{
			// Stop the music if it is already playing
			if (musicPlaying)
			{
				currentSound.stop();
				// Change images
				soundButton.setIcon(musicOffIcon1);
				soundButton.setRolloverIcon(musicOffIcon2);
				soundButton.setPressedIcon(musicOffIcon3);
				musicPlaying = false;
			}
			// Begin to loop the music if it is off
			else
			{
				currentSound.loop();
				// Change images
				soundButton.setIcon(musicIcon1);
				soundButton.setRolloverIcon(musicIcon2);
				soundButton.setPressedIcon(musicIcon3);
				musicPlaying = true;
			}
		}
		// Pops up a brief help dialog
		else if (source == helpButton)
		{
			if (showScreen != INSTRUCTIONS)
			{
				// Pause the in-game timer
				timer.stop();
				JOptionPane
						.showMessageDialog(
								this,
								"\n Move using the arrow keys\n"
										+ "Hover over an object for a brief description.\n"
										+ "Click on an object to interact with it (if possible)\n"
										+ "Some objects can be picked up into your inventory by clicking or pressing space\n"
										+ "Use them by clicking on them in the inventory (a highlight appears if successful),\n"
										+ "then click on the next object or area to use it on.\n"
										+ "(See the main menu instructions for more details)\n"
										+ "Have fun and Escape!",
								"Instructions",
								JOptionPane.INFORMATION_MESSAGE);
				// Resume the in-game timer
				if (showScreen == GAME_SCREEN)
					timer.start();
			}
		}
		// Displays the main menu
		else if (source == homeButton)
		{
			showInstruction = 1;
			nextButton.setVisible(false);
			nextButton.setBounds(500, 400, 200, 50);
			hideSongButtons();
			if (showScreen == GAME_SCREEN)
			{
				// Stop the timer if in-game
				timer.stop();
				// Display warning and receive input from it
				int choice = JOptionPane.showConfirmDialog(this,
						"All current level progress will be lost.",
						"WARNING", JOptionPane.OK_CANCEL_OPTION);
				// If the palyer wants to return to the main menu, we shall do
				// so
				if (choice == JOptionPane.OK_OPTION)
				{
					inventoryObjects.clear();
					mainMenu();
				}
				// If the player cancels or closes the return to menu box
				else
				{
					// Resume the timer
					timer.start();
					this.requestFocusInWindow();
					// Avoid unnecessary repainting and focus requests
					return;
				}
			}
			else
				mainMenu();
		}
		// Change the song to Queen - "I Want to Break Free"
		else if (source == queen)
		{
			currentSound.stop();
			soundButton.setIcon(musicIcon1);
			soundButton.setRolloverIcon(musicIcon2);
			soundButton.setPressedIcon(musicIcon3);
			currentSound = backgroundSound;
			currentSound.loop();
			musicPlaying = true;
		}
		// Change the song to Elvis - "Jailhouse Rock"
		else if (source == elvis)
		{
			currentSound.stop();
			soundButton.setIcon(musicIcon1);
			soundButton.setRolloverIcon(musicIcon2);
			soundButton.setPressedIcon(musicIcon3);
			currentSound = backgroundSound2;
			currentSound.loop();
			musicPlaying = true;
		}
		// If a level selection button is clicked (Level 1 - 10)
		else
		{
			// Go through each available button that leads to a level
			for (int level = 1; level <= NO_OF_LEVELS; level++)
			{
				if (levelButtons[level] == source)
				{
					// If this level has been unlocked, start the level
					if (unlockedLevels[level])
					{
						currentLevel = level;
						// Clear all buttons
						for (int levelButton = 1; levelButton <= NO_OF_LEVELS; levelButton++)
							levelButtons[levelButton].setVisible(false);
						newLevel();
					}
					// If this level is locked, return a message that the level
					// has not been unlocked
					else
					{
						JOptionPane
								.showMessageDialog(
										this,
										"Level " + level
												+ " has not been unlocked!",
										"Error",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
		// Update the paint component and request focus so that
		// other components can be accessed
		repaint(0);
		this.requestFocusInWindow();
	}

	/**
	 * Handles all timing events that include counting time
	 */
	private class TimerEventHandler implements ActionListener
	{
		// The following method is called every 100 miliseconds
		/**
		 * Count the number of deci-seconds
		 * @param event contains information about timer events
		 */
		public void actionPerformed(ActionEvent event)
		{
			// Add to the current time
			time++;
			repaint(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{

	}
}