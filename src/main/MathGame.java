package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
	
public class MathGame implements KeyListener, ActionListener, MouseListener {
	
	class ProblemSpawner extends Thread {
		
		@Override
		public void run() {
			try {
				while(true) {
					pause(5000 - 100*correct);
					if(checkBoardIfFull() == false) {
						invokeInsertProblem();
					}
					else {
						System.out.println("Game Over");
						isGameOver = true;
						invokeGameOver();
						return;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	int iThing = 0;
	int jThing = 0;
	int boo = 0;
	
	int YSize = 4;
	int XSize = 4;
	int XPadding = 10;
	int YPadding = 10;
	int outerXPadding = 10;
	int outerYPadding = 10;
	int windowX = 500;
	int windowY = 700;
	
	int score = 0;
	int correct = 0;
	boolean isGameOver = false;
	
	JButton[][] btnLayout = new JButton[XSize][YSize];
	tile[][] tileLayout = new tile[XSize][YSize];
	int difficulty = 1;
	int enteredValue = 0;
	
	//global variables
	JPanel gamePanel;
	JButton btnConfirm;
	JLabel lblScore;
	JPanel gameOverPanel;
	JLabel gameOverMessage;
	JLabel gameOverMessage2;
	JPanel textPanel;
	
	//colours
	Color green = new Color(0, 225, 0);
	Color white = new Color(255, 255, 255);
	
	/** 
	 *  A tile
	 */
	public class tile {
		private int val1;
		private int val2;
		private String operator;
		private boolean isActive;
		
		public tile(int val1, int val2, String operator) {
			this.val1 = val1;
			this.val2 = val2;
			this.operator = operator;
		}
		
		private void setVal1(int val1) {
			this.val1 = val1;
		}
		private void setVal2(int val2) {
			this.val2 = val2;
		}
		private void setOperator(String operator) {
			this.operator = operator;
		}
		private void setActive(boolean isActive) {
			this.isActive = isActive;
		}
		
		private int getVal1() {
			return this.val1;
		}
		private int getVal2() {
			return this.val2;
		}
		private String getOperator() {
			return this.operator;
		}
		private boolean getIsActive() {
			return this.isActive;
		}
		
	}
	
	tile selectedTile;
	JButton selectedBtn;
	
	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MathGame window = new MathGame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MathGame() {
		initialize();
		addComponents();
		play();
		startSpawner();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void addComponents() {
		
		gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(XSize, YSize, XPadding, YPadding));
		gamePanel.setBounds(50, 100, (int)(windowX*0.8), (int)(windowX*0.8));
		addBtnsToPanel();
		frame.getContentPane().add(gamePanel, 0);
		
		textField = new JTextField();
		textField.setBounds(67, 556, 247, 42);
		frame.getContentPane().add(textField, 0);
		textField.setColumns(10);
		textField.addActionListener(this);
		btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener(this);
		btnConfirm.setBackground(green);
		btnConfirm.setBounds(324, 556, 107, 42);
		frame.getContentPane().add(btnConfirm, 0);
		
		lblScore = new JLabel("Score: 0");
		lblScore.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblScore.setBounds(67, 38, 152, 37);
		frame.getContentPane().add(lblScore, 0);
		
		gameOverPanel = new JPanel();
		gameOverPanel.setLayout(new BorderLayout());
		gameOverPanel.setBounds(0, 125, windowX, (windowY/2));
		frame.getContentPane().add(gameOverPanel, 1);
		
		textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setBackground(new Color(100, 200, 100, 200));
		
		gameOverMessage = new JLabel("Game Over");
		gameOverMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		gameOverMessage.setFont(new Font("Tahoma", Font.PLAIN, 40));
		
		gameOverMessage2 = new JLabel("press 'r' to restart", SwingConstants.CENTER);
		gameOverMessage2.setAlignmentX(Component.CENTER_ALIGNMENT);
		gameOverMessage2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		textPanel.add(Box.createRigidArea(new Dimension(0,130)));
		textPanel.add(gameOverMessage);
		textPanel.add(gameOverMessage2);
		
		gameOverPanel.add(textPanel, BorderLayout.CENTER);
		
		gameOverPanel.setVisible(false);
	}
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, windowX, windowY);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		createBtnLayout();
		createTileLayout();
		
		frame.addWindowListener(new WindowAdapter() { //focus cursor to textField when window opens
			public void windowOpened(WindowEvent e) {
				textField.requestFocusInWindow();
			}
		});

		frame.getContentPane().addKeyListener(this);
		addMouseListener();
	}
	
	private void play() {
		invokeInsertProblem();
	}
	
	private void startSpawner() {
		ProblemSpawner spawner = new ProblemSpawner();
		spawner.start();
	}
	
	public void createBtnLayout() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				btnLayout[i][j] = new JButton();
				btnLayout[i][j].setBackground(white);
			}
		}
	}
	
	public void createTileLayout() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				tileLayout[i][j] = new tile(0, 0, null);
				tileLayout[i][j].setActive(false);
			}
		}
	}
	
	public void addBtnsToPanel() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				gamePanel.add(btnLayout[i][j], 0);
			}
		}
	}
	
	public void clearBoardAndTiles() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				createTileLayout();
			}
		}
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				btnLayout[i][j].setText(null);
			}
		}
	}
	
	public void addMouseListener() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				btnLayout[i][j].addMouseListener(this);
			}
		}
	}

	public void updateSelection(int i, int j, boolean clear) {
		if(clear == true) {
			selectedBtn.setBackground(white);
			selectedTile = null;
		}
		else if(selectedTile == null) {
			selectedTile = tileLayout[i][j];
			selectedBtn = btnLayout[i][j];
			selectedBtn.setBackground(green);
			System.out.println("Tile " + (j+1) + ", " + (4-i) + " selected");
		}
		else if(selectedTile != tileLayout[i][j] && selectedTile != null) {
			selectedBtn.setBackground(white);
			selectedTile = tileLayout[i][j];
			selectedBtn = btnLayout[i][j];
			selectedBtn.setBackground(green);
			System.out.println("Tile " + (j+1) + ", " + (4-i) + " selected");
		}
	}
	
	public void answerIsCorrect() {
		System.out.println("Correct!");
		textField.setText(null);
		textField.requestFocus();
		selectedBtn.setText(null);
		selectedBtn.setBackground(white);
		selectedTile.setActive(false);
		selectedTile = null;
		score += 10;
		correct += 1;
		lblScore.setText("Score: " + score );
		//invokeInsertProblem();
	}
	
	public void invokeInsertProblem() {
		insertProblem(0, 100, 0, 100, 0, 15, 0, 250, 0, 0, 1, 0);
	}
	
	public void invokeGameOver() {
		for(int i=0;i<btnLayout.length;i++) {
			for(int j=0;j<btnLayout.length;j++) {
				btnLayout[i][j].setEnabled(false);
				textField.setText("");
				textField.setEnabled(false);
				btnConfirm.setEnabled(false);
			}
		}
		gameOverPanel.setVisible(true);
		frame.getContentPane().requestFocusInWindow();
	}
	
	public boolean checkBoardIfFull() {
		for(int i=0;i<tileLayout.length;i++) {
			for(int j=0;j<tileLayout.length;j++) {
				if(tileLayout[i][j].getIsActive() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void pause(int ms) throws InterruptedException {
		Thread.sleep(ms);
	}
	
	public void insertProblem(int addMin, int addMax, int subMin, int subMax, 
							  int multiMin, int multiMax , int divMin, int divMax, 
							  int addFreq, int subFreq, int multiFreq, int divFreq) {
		Random rand1 = new Random();
		int i = rand1.nextInt(4);
		int j = rand1.nextInt(4);
		
		int val1;
		int val2;
		
		int freqSum = (addFreq+subFreq+multiFreq+divFreq);
		int chosen = rand1.nextInt(freqSum);
		
		while(true) {
			if(tileLayout[i][j].getIsActive() == false) {
				if(chosen >= 0 && chosen < addFreq) {
					tileLayout[i][j].setOperator("+");
					
					val1 = rand1.nextInt(addMax-addMin) + addMin;
					val2 = rand1.nextInt(addMax-addMin) + addMin;
					
					tileLayout[i][j].setVal1(val1);
					tileLayout[i][j].setVal2(val2);
					
					tileLayout[i][j].setActive(true);
				}
				else if(chosen >= addFreq && chosen < (addFreq + subFreq)) {
					tileLayout[i][j].setOperator("-");
					
					val1 = rand1.nextInt(subMax-subMin) + subMin;
					val2 = rand1.nextInt(subMax-subMin) + subMin;
					
					tileLayout[i][j].setVal1(val1);
					tileLayout[i][j].setVal2(val2);
					
					tileLayout[i][j].setActive(true);
				}
				else if(chosen >= (addFreq + subFreq) && chosen < (addFreq + subFreq + multiFreq)) {
					tileLayout[i][j].setOperator("*");
					
					val1 = rand1.nextInt(multiMax-multiMin) + multiMin;
					val2 = rand1.nextInt(multiMax-multiMin) + multiMin;
					
					tileLayout[i][j].setVal1(val1);
					tileLayout[i][j].setVal2(val2);
					
					tileLayout[i][j].setActive(true);
				}
				else if(chosen >= (addFreq + subFreq + multiFreq) && chosen < freqSum) { 
					tileLayout[i][j].setOperator("/");
					
					val1 = rand1.nextInt(divMax-divMin) + divMin;
					val2 = rand1.nextInt(divMax-divMin) + divMin;
					
					while(true) {
						if(val2 == 0) {
							val2 = rand1.nextInt(divMax-divMin) + divMin;
						}
						else if( (val1 % val2 == 0) & (val1 != val2 & val1 != 1 & val2 != 1 & val1 != 0) ) {
								tileLayout[i][j].setVal1(val1);
								tileLayout[i][j].setVal2(val2);
								break;
							}
						else {
							val1 = rand1.nextInt(divMax-divMin) + divMin;
							val2 = rand1.nextInt(divMax-divMin) + divMin;
						}
					}
					tileLayout[i][j].setActive(true);
				}
				break;
			}
			else {
				i = rand1.nextInt(4);
				j = rand1.nextInt(4);
			}
		}
		
		btnLayout[i][j].setText(tileLayout[i][j].getVal1() + " " + tileLayout[i][j].getOperator() + " " + tileLayout[i][j].getVal2());
		}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R) {
			if(isGameOver == true) {
				clearBoardAndTiles();
				lblScore.setText("Score: 0");
				if(selectedTile != null) {
					updateSelection(0, 0, true);
				}
				for(int i=0;i<btnLayout.length;i++) {
					for(int j=0;j<btnLayout.length;j++) {
						btnLayout[i][j].setEnabled(true);
					}
				}
				startSpawner();
				gameOverPanel.setVisible(false);
				textField.setEnabled(true);
				btnConfirm.setEnabled(true);
				textField.requestFocusInWindow();
				isGameOver = false;
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		
		if(source instanceof JButton && isGameOver == false) {
			for(int i=0;i<btnLayout.length;i++) {
				for(int j=0;j<btnLayout.length;j++) {
					if(source == btnLayout[i][j]) {
						if(tileLayout[i][j].getIsActive() == true) {
							updateSelection(i, j, false);
							textField.requestFocusInWindow();
						}
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		String enteredString = textField.getText();
		
		try {
		int enteredValue = Integer.parseInt(enteredString);
		
		if(selectedTile.getOperator().equals("+")) {
			if( ( selectedTile.getVal1() + selectedTile.getVal2() ) == enteredValue ) {
				answerIsCorrect();
			}
			else {
				System.out.println("Try Again!");
			}
		}
		else if(selectedTile.getOperator().equals("-")) {
			if( ( selectedTile.getVal1() - selectedTile.getVal2() ) == enteredValue ) {
				answerIsCorrect();
			}
			else {
				System.out.println("Try Again!");
			}
		}
		else if(selectedTile.getOperator().equals("/")) {
			if( ( selectedTile.getVal1() / selectedTile.getVal2() ) == enteredValue ) {
				answerIsCorrect();
			}
			else {
				System.out.println("Try Again!");
			}
		}
		else if(selectedTile.getOperator().equals("*")) {
			if( ( selectedTile.getVal1() * selectedTile.getVal2() ) == enteredValue ) {
				answerIsCorrect();
			}
			else {
				System.out.println("Try Again!");
			}
		}	
	}
		catch (NullPointerException e1) {
			if (selectedTile == null) {
				throw new NullPointerException("No tile selected");
			}
		}
		catch (NumberFormatException e2) {
			throw new NumberFormatException("Improper number formatting or textField value is null");
		}
	}
}
