import java.awt.*;
import java.awt.event.*;

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

	// Declare Vars
	private JLabel contentPane;
	private JTextArea outputText;
	private boolean myTurn = false;
	private boolean wait = true;
	private String host = "127.0.0.1";
	private int port = 9001;
	private DataInputStream inputStream; //fromServer
	private DataOutputStream outputStream; //toServer
	private Image img;
	
	private boolean connected;
	private String myToken;
	private Player myPlayer, player1, player2, player3, player4;
	private boolean continueToPlay = true;
	private JLabel statusIndicator = new JLabel();
	private int player;
	int numOfPlayers, currentPlayerTurn;
	

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1844, 1080);
		setResizable(false);
		contentPane = new JLabel(new ImageIcon("src/EelsAndEscalatorsYouLose.jpg"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(18, 148, 203));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JTextArea outputText = new JTextArea(30, 30);
        JScrollPane scrollPane = new JScrollPane(outputText);
        contentPane.add(scrollPane, BorderLayout.EAST);
		
        statusIndicator.setBorder(new LineBorder(Color.black, 1)); //TODO - implement
        add(statusIndicator, BorderLayout.NORTH);
        
		JButton btnRoll = new JButton("Roll");
		panel.add(btnRoll);
		
		btnRoll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { //TODO - finish
					if(myTurn)
						outputStream.writeInt(SEND_ROLL_REQUEST);
					else if(!connected){
						outputText.append("\n" + CONNECTING_MESSAGE);
						connectToServer();
					}
					else
						outputText.append("\n" + WAIT_MESSAGE);
					
				} catch (Exception err) {
					System.out.println(err.toString());
				}
			}
		});
		
		JLabel lblDi = new JLabel("Dice 1");
		panel.add(lblDi);
		
		JLabel lblDi_1 = new JLabel("Dice 2");
		panel.add(lblDi_1);
		
		
		//TODO - Add JTextArea hider/opener
		//outputText.addComponentListener(new addKeyListener(T) {	
		//});
		
	}
	
	public void getServerInfo() {
		// Enter Server IP
		// Enter Server Port
		// Check format of IP & Port
		System.out.println("IP: " + host);
		System.out.println("Port: " + port);
		System.out.println("Connection: " + connected);
	}
	
	//@param1 Host of server
	//@param2 Port of server
	//@return Server connected status
	//TODO - bool return != necessary
	public boolean connectToServer() {
		
		try {
			// Create our socket to connect to the server
			Socket socket = new Socket(host, port);
			
			// Client in and out streams to and from the server
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			
		}
		catch (Exception e) {
			connected = false;
		}
		// Attempt to connect to server
		// If connection fails, return false
		// else
		Thread thread = new Thread(this);
	    thread.start();
		connected = true;
	    getServerInfo();
		return true;
	}

	//TODO -need to fix
	public void run() {

		try{
			player = inputStream.readInt();
			//outputText.append("Player: " + player);
			//String input;
			/*
			if(player == PLAYER1){
				outputText.append("\n" + CHARSELECTMESSAGE +
						"\nEnter <1> for Mr. Krabs" +
						"\nEnter <2> for Squidward" +
						"\nEnter <3> for SpongeBob" +
						"\nEnter <4> for Patrick\n");
				//input = outputText.getText(outputText.getLineCount(), 1); //has not been tested
				
				outputText.append("\nPlayer 1 connected: " + connected +
						"\n" + ROLLDICEMSG + "/n");

			}
			
			else if(player == PLAYER2){
				outputText.append("\n" + CHARSELECTMESSAGE +
						"\nEnter <1> for Mr. Krabs" +
						"\nEnter <2> for Squidward" +
						"\nEnter <3> for SpongeBob" +
						"\nEnter <4> for Patrick\n");
				//input = outputText.getText(); //has not been tested
				
				outputText.append("\nPlayer 2 connected: " + connected +
						"\n" + WAIT_MESSAGE + "\n");
			}
			
			else if(player == PLAYER3){
				outputText.append("\n" + CHARSELECTMESSAGE +
						"\nEnter <1> for Mr. Krabs" +
						"\nEnter <2> for Squidward" +
						"\nEnter <3> for SpongeBob" +
						"\nEnter <4> for Patrick\n");
				//input = outputText.getText(); //has not been tested
				
				outputText.append("\nPlayer 3 connected: " + connected +
						"\n" + WAIT_MESSAGE + "\n" );
				
			}
			
			else{
				outputText.append("\n" + CHARSELECTMESSAGE +
						"\nEnter <1> for Mr. Krabs" +
						"\nEnter <2> for Squidward" +
						"\nEnter <3> for SpongeBob" +
						"\nEnter <4> for Patrick\n");
				//input = player; //has not been tested
				
				outputText.append("\nPlayer 4 connected: " + connected +
						"\n" + WAIT_MESSAGE + "\n" );
			}
			*/
			switch(player){
				case 1:
					myToken =  MRKRABS_TOKEN;
					myPlayer = new Player(player, MRKRABS_TOKEN);
					player1 = new Player(player, MRKRABS_TOKEN);
					break;
			
				case 2:
					myToken = SQUIDWARD_TOKEN;
					myPlayer = new Player(player, SQUIDWARD_TOKEN);
					player2 = new Player(player, SQUIDWARD_TOKEN);
					break;
				
				case 3:
					myToken = SPONGEBOB_TOKEN;
					myPlayer = new Player(player, SPONGEBOB_TOKEN);
					player3 = new Player(player, SPONGEBOB_TOKEN);
					break;
				
				case 4:
					myToken = PATRICK_TOKEN;
					myPlayer = new Player(player, PATRICK_TOKEN);
					player4 = new Player(player, PATRICK_TOKEN);
					break;
			}
			
			while(continueToPlay){
				if(player == PLAYER1){
					receiveInfoFromServer();
					waitForPlayerAction();
					sendMove();
				}
				
				else if(player == PLAYER2){
					receiveInfoFromServer();
					waitForPlayerAction();
					sendMove();
				}
				
				else if(player == PLAYER3){
					receiveInfoFromServer();
					waitForPlayerAction();
					sendMove();
				}
				else{
					receiveInfoFromServer();
					waitForPlayerAction();
					sendMove();
				}
			}
		}
		catch(Exception ex){
			outputText.append("Error with run()");
		}
	}

	private void sendMove() throws IOException { //TODO - check
		outputStream.writeInt(SEND_ROLL_REQUEST);
	}
	
	private void waitForPlayerAction()throws InterruptedException{ //TODO - check
		while(wait){
				Thread.sleep(100);
		}	
		wait = true;
	}
	
	private void receiveInfoFromServer() throws IOException { //TODO - check
		int currentStatus = inputStream.readInt();
		int x = inputStream.readInt();
		int y = inputStream.readInt();

		switch(currentStatus){
			case PLAYER1_WIN:
				continueToPlay = false;
				outputText.append("\nPlayer 1 wins!");
				if(player != PLAYER1)  //if not me
					receiveMove(PLAYER1, x, y);
				break;
				
			case PLAYER2_WIN:
				continueToPlay = false;
				outputText.append("\nPlayer 2 wins!");
				if(player != PLAYER2)  //if not me
					receiveMove(PLAYER2, x, y);
				break;
				
			case PLAYER3_WIN:
				continueToPlay = false;
				outputText.append("\nPlayer 3 wins!");
				if(player != PLAYER3)  //if not me
					receiveMove(PLAYER3, x, y);
				break;
				
			case PLAYER4_WIN:
				continueToPlay = false;
				outputText.append("\nPlayer 4 wins!");
				if(player != PLAYER4)  //if not me
					receiveMove(PLAYER4, x, y);
				break;
				
			case PLAYER1_LOSE: //TODO - add more losing characteristics
				outputText.append("\nPlayer 1 loses!");
				if(player != PLAYER1)  //if not me
					receiveMove(PLAYER1, x, y);
				break;
				
			case PLAYER2_LOSE:
				outputText.append("\nPlayer 2 loses!");
				if(player != PLAYER2)  //if not me
					receiveMove(PLAYER2, x, y);
				break;
				
			case PLAYER3_LOSE:
				outputText.append("\nPlayer 3 loses!");
				if(player != PLAYER3)  //if not me
					receiveMove(PLAYER3, x, y);
				break;
				
			case PLAYER4_LOSE:
				outputText.append("\nPlayer 4 loses!");
				if(player != PLAYER4) //if not me
					receiveMove(PLAYER4, x, y);
				break;
				
			case PLAYER1:
				receiveMove(PLAYER1, x, y);
				break;
				
			case PLAYER2:
				receiveMove(PLAYER2, x, y);
				break;
				
			case PLAYER3:
				receiveMove(PLAYER3, x, y);
				break;
				
			case PLAYER4:
				receiveMove(PLAYER4, x, y);
				break;		
		}
	}

	
	private void receiveMove(int currentPlayer, int x ,int y){
        switch (currentPlayer){
            case 1:
                player1.setXLocation(x);
                player1.setYLocation(y);
                break;
            case 2:
                player2.setXLocation(x);
                player2.setYLocation(y);
                break;
            case 3:
                player3.setXLocation(x);
                player3.setYLocation(y);
                break;
            case 4:
                player4.setXLocation(x);
                player4.setYLocation(y);
                break;
                
        } 
	}
}
