import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.*;
import java.net.*;
import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ClientFrame extends JFrame implements EelsAndEscalatorsInterface, Runnable {

	// GUI ELEMENTS
	private JPanel panel;
	private JLabel contentPane, playerSprite, playerSprite2, playerSprite3, playerSprite4, lblDi, lblDi_1;
	private JTextArea outputText;
	private JScrollPane scrollPane;
	private DataInputStream inputStream; //fromServer
	private DataOutputStream outputStream; //toServer
	private JButton btnRoll;
	
	// BOOLEAN ELEMENTS
	private boolean waiting = true;
	private boolean myTurn = false;
	
	// SERVER CONNECTION INFO
	private String host = "127.0.0.1";
	private int port = 9001, playerID;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ClientFrame frame = new ClientFrame();
						frame.connectToServer();
				} catch (Exception e) {
					e.printStackTrace();
				}	
				}
			});
	}

	/**
	 * Create the frame.
	 */
	public ClientFrame() {
		// BEGIN PLAYER SPRITE CREATION
		playerSprite = new JLabel(rescaleSprite("src/123.png"));
		playerSprite2 = new JLabel(rescaleSprite("src/124.png"));
		playerSprite3 = new JLabel(rescaleSprite("src/125.png"));
		playerSprite4 = new JLabel(rescaleSprite("src/126.png"));
		// END PLAYER SPRITE CREATION
		
		// CREATE OUR MAIN WINDOW AND BACKGROUND
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1844, 1080);
		setResizable(false);
		contentPane = new JLabel(new ImageIcon("src/EelsAndEscalatorsYouLose.jpg"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		
		// DICE WINDOW
		panel = new JPanel();
		panel.setBackground(new Color(18, 148, 203));
		contentPane.add(panel);
		panel.setSize(50,80);
		panel.setLocation(50,900);
		btnRoll = new JButton("Roll");
		panel.add(btnRoll);
		lblDi = new JLabel("0");
		lblDi_1 = new JLabel("0");
		panel.add(lblDi_1);
		panel.add(lblDi);
		
		// TEXT AREA
		outputText = new JTextArea();
		outputText.setWrapStyleWord(true);
        scrollPane = new JScrollPane(outputText);
        contentPane.add(scrollPane);
        scrollPane.setSize(200,800);
        scrollPane.setLocation(1600,100);
		
		
		
		// ADD PLAYER SPRITE
		// Player 1
		contentPane.add(playerSprite);
		playerSprite.setLocation(400, 687);
		playerSprite.setSize(40,40);
		
		// Player 2
		contentPane.add(playerSprite2);
		playerSprite2.setLocation(400, 730);
		playerSprite2.setSize(40,40);
		
		// Player 3
		contentPane.add(playerSprite3);
		playerSprite3.setLocation(400, 772);
		playerSprite3.setSize(40,40);
		
		// Player 4
		contentPane.add(playerSprite4);
		playerSprite4.setLocation(400, 813);
		playerSprite4.setSize(40,40);
		
		// DISPLAY OUR GUI
		setVisible(true);

		// JBUTTON BTNROLL ACTION LISTENER
		btnRoll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { 
					if(myTurn) {
						// REQUEST DICE FROM THE SERVER AND DISPLAY THEM ON THE CLIENT
						outputStream.writeInt(SEND_ROLL_REQUEST);
						lblDi.setText(Integer.toString(inputStream.readInt()));
						lblDi_1.setText(Integer.toString(inputStream.readInt()));
						
						// RESUME LISTENING FROM THE SERVER
						// OUR TURN IS OVER
						waiting = false;
					}
					else
						outputText.append("\n" + WAIT_MESSAGE);
					
				} catch (Exception err) {
					outputText.append(err.toString());
				}
			}
		});
		
		/*contentPane.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				outputText.append("X: " + x + ", Y: " + y + "\n");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

		});*/
		
	}
	
	// HELPER FUNCTION FOR RESIZING SPRITES
	public ImageIcon rescaleSprite(String path) {
		ImageIcon plSprite = new ImageIcon(path); 
		Image image = plSprite.getImage(); 
		image = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
	
	//@param1 Host of server
	//@param2 Port of server
	//@return Server connected status
	public void connectToServer() {
		
		try {
			// Create our socket to connect to the server
			Socket socket = new Socket(host, port);
			
			// Client in and out streams to and from the server
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			
			// CONNECTED - START GAME LOOP
			Thread thread = new Thread(this);
		    thread.start();
			
		}
		catch (Exception e) {
			System.out.println("Problem with connectToServer()");
			
		}	
	}
	

	public void run() {
		try {
		playerID = inputStream.readInt();
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		while(true) {
			try {
				switch(inputStream.readInt()) {
					
					// NOT OUR TURN
					case PLAYER_WAIT:
						myTurn = false;
						break;
					
					// ALLOW PLAYER TO MOVE
					case PLAYER_GO:
						myTurn = true;
						waitForPlayer();
						break;
					
					// UPDATE GAME BOARD
					case END_PLAYER_TURN:
						receiveMove(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
						break;
					
					// PLAYER HAS WON THE GAME
					case PLAYER_WON:
						win(inputStream.readInt());
						break;
						
					case PLAYER_LOST:
						lost(inputStream.readInt());
						break;
				}
				
			}
			catch (Exception ex) {
				System.out.println(ex.toString());
			}
			}
		}
	
	// WAIT FOR PLAYER TO ROLL/ACTION
	private void waitForPlayer() throws InterruptedException {
		    while (waiting) {
		      Thread.sleep(100);
		    }
		    waiting = true;
		  }

	// END GAME IF CALLED
	private void win(int player) {
		switch (player) {
			case PLAYER1_WIN:
				outputText.append("PLAYER 1 HAS WON THE GAME!\n");
				break;
			case PLAYER2_WIN:
				outputText.append("PLAYER 2 HAS WON THE GAME!\n");
				break;
			case PLAYER3_WIN:
				outputText.append("PLAYER 3 HAS WON THE GAME!\n");
				break;
			case PLAYER4_WIN:
				outputText.append("PLAYER 4 HAS WON THE GAME!\n");
				break;
		}
		panel.remove(btnRoll);
		panel.remove(lblDi_1);
		lblDi.setText("GAME OVER");
	}
	
	private void lost(int player) {
		switch (player) {
		case PLAYER1_LOSE:
			outputText.append("PLAYER 1 HAS LOST THE GAME!\n");
			break;
		case PLAYER2_LOSE:
			outputText.append("PLAYER 2 HAS LOST THE GAME!\n");
			break;
		case PLAYER3_LOSE:
			outputText.append("PLAYER 3 HAS LOST THE GAME!\n");
			break;
		case PLAYER4_LOSE:
			outputText.append("PLAYER 4 HAS LOST THE GAME!\n");
			break;
	}
		if (player == playerID) {
			panel.remove(btnRoll);
			panel.remove(lblDi_1);
			lblDi.setText("GAME OVER");
		}
	}
	
	// UPDATE GAMEBOARD WITH NEW POSITIONS
	private void receiveMove(int currentPlayer, int x ,int y){
        switch (currentPlayer){
            case 1:
            	playerSprite.setLocation(x, y); 
                break;
            case 2:
            	playerSprite2.setLocation(x, y);
                break;
            case 3:
            	playerSprite3.setLocation(x, y); 
                break;
            case 4:
            	playerSprite4.setLocation(x, y); 
                break;
                
        } 
	}
}
