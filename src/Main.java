import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements ActionListener, KeyListener {
	
	static JFrame frame;
	
	static final int SQUARE_SIZE = 100;
	static final int SPACING = (int)Math.round(SQUARE_SIZE * 2 / 15.0);
	static final int GRID_SIZE = SQUARE_SIZE * 4 + SPACING * 5;
	static final int TOP_BORDER = 120;
	static final int BORDER = 30;
	
	static final Color[] TILE_COLOURS = new Color[18];
	
	static int[][] grid;
	
	boolean gameIsOver;
	
	JButton newGameButton;
	
	static Image offScreenImage;
	static Graphics offScreenBuffer;
	
	public Main() {
		TILE_COLOURS[0] = new Color(0xeee4da);
		TILE_COLOURS[1] = new Color(0xede0c7);
		TILE_COLOURS[2] = new Color(0xf2b17a);
		TILE_COLOURS[3] = new Color(0xf59563);
		TILE_COLOURS[4] = new Color(0xff7455);
		TILE_COLOURS[5] = new Color(0xf55f3a);
		TILE_COLOURS[6] = new Color(0xf1cf5f);
		TILE_COLOURS[7] = new Color(0xf3cb49);
		TILE_COLOURS[8] = new Color(0xf3c82c);
		TILE_COLOURS[9] = new Color(0xf3c300);
		TILE_COLOURS[10] = new Color(0xebb800);
		TILE_COLOURS[11] = new Color(0x33b4a9);
		TILE_COLOURS[12] = new Color(0x27a59a);
		TILE_COLOURS[13] = new Color(0x12998d);
		TILE_COLOURS[14] = new Color(0x24a7f5);
		TILE_COLOURS[15] = new Color(0x0099f4);
		TILE_COLOURS[16] = new Color(0x0084d2);
		TILE_COLOURS[17] = new Color(0x3c3a32);
		
		newGameButton = new JButton();
		newGameButton.setBounds(BORDER, TOP_BORDER - 45 - SPACING, SQUARE_SIZE + SPACING * 3 / 2, 45);
		newGameButton.setActionCommand("New Game");
		newGameButton.addActionListener(this);
		newGameButton.setBackground(new Color(0x8f7865));
		newGameButton.setOpaque(false);
		newGameButton.setBorder(null);
		newGameButton.setFocusPainted(false);
		newGameButton.setContentAreaFilled(false);
		newGameButton.setVisible(true);
		add(newGameButton);
		
		setLayout(null);
		
		gameIsOver = false;
		
		grid = new int[6][6];
		
		for (int i = 0; i < 5; i++)
			grid[0][i] = grid[i][5] = grid[5][5 - i] = grid[5 - i][0] = -1;
		
		for (int i = 1; i <= 4; i++)
			if (i % 2 == 0)
				for (int j = 1; j <= 4; j++)
					grid[i][j] = (int)Math.pow(2, (i - 1) * 4 + j);
			else
				for (int j = 4; j >= 1; j--)
					grid[i][j] = (int)Math.pow(2, (i - 1) * 4 - j + 5);
		
		grid[1][4] = 4;
		
		setFocusable(true);
		addKeyListener(this);
	}
	
	public void paintComponent(Graphics g) {
		// Sets up the off-screen buffer the first time paint() is called
		if (offScreenBuffer == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
			offScreenBuffer = offScreenImage.getGraphics();
		}

		// Clears the off-screen buffer
		offScreenBuffer.clearRect (0, 0, this.getWidth(), this.getHeight());
		
		drawGrid();
		
		for (int row = 1; row <= 4; row++)
			for (int col = 1; col <= 4; col++)
				if (grid[row][col] != 0) {
					int x = SQUARE_SIZE * (col - 1) + SPACING * col + BORDER;
					int y = SQUARE_SIZE * (row - 1) + SPACING * row + TOP_BORDER;
					drawTile(grid[row][col], x, y);
				}
		
		offScreenBuffer.setColor(new Color(0x8f7865));
		int width = SQUARE_SIZE + SPACING * 3 / 2;
		int height = 45;
		int x = BORDER;
		int y = TOP_BORDER - height - SPACING;
		offScreenBuffer.fillRoundRect(x, y, width, height, 10, 10);
		
		offScreenBuffer.setColor(new Color(0xf9f6f2));
		offScreenBuffer.setFont(new Font("Roboto", Font.BOLD, 18));
		FontMetrics fm = offScreenBuffer.getFontMetrics();
		offScreenBuffer.drawString("New Game", x + (width - fm.stringWidth("New Game")) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
		
		g.drawImage(offScreenImage, 0, 0, this);
	}

	public void paint() {
		// Clears the off-screen buffer
		offScreenBuffer.clearRect (0, 0, this.getWidth(), this.getHeight());

		drawGrid();

		for (int row = 1; row <= 4; row++)
			for (int col = 1; col <= 4; col++)
				if (grid[row][col] != 0) {
					int x = SQUARE_SIZE * (col - 1) + SPACING * col + BORDER;
					int y = SQUARE_SIZE * (row - 1) + SPACING * row + TOP_BORDER;
					drawTile(grid[row][col], x, y);
				}

		offScreenBuffer.setColor(new Color(0x8f7865));
		int width = SQUARE_SIZE + SPACING * 3 / 2;
		int height = 45;
		int x = BORDER;
		int y = TOP_BORDER - height - SPACING;
		offScreenBuffer.fillRoundRect(x, y, width, height, 10, 10);
		
		offScreenBuffer.setColor(new Color(0xf9f6f2));
		offScreenBuffer.setFont(new Font("Roboto", Font.BOLD, 18));
		FontMetrics fm = offScreenBuffer.getFontMetrics();
		offScreenBuffer.drawString("New Game", x + (width - fm.stringWidth("New Game")) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
		
		Graphics g = getGraphics();
		g.drawImage(offScreenImage, 0, 0, this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("New Game"))
			reset();
	}
	
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (gameIsOver)
			return;
		
		if (e.getKeyCode() == KeyEvent.VK_UP && canMoveUp()) {
			moveUp();
			spawnTile();
			if (!(canMoveUp() || canMoveDown() || canMoveLeft() || canMoveRight()))
				gameOver();
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN && canMoveDown()) {
			moveDown();
			spawnTile();
			if (!(canMoveUp() || canMoveDown() || canMoveLeft() || canMoveRight()))
				gameOver();
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT && canMoveLeft()) {
			moveLeft();
			spawnTile();
			if (!(canMoveUp() || canMoveDown() || canMoveLeft() || canMoveRight()))
				gameOver();
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT && canMoveRight()) {
			moveRight();
			spawnTile();
			if (!(canMoveUp() || canMoveDown() || canMoveLeft() || canMoveRight()))
				gameOver();
		}
	}

	public void keyReleased(KeyEvent e) {
	}
	
	public boolean canMoveUp() {
		for (int row = 2; row <= 4; row++)
			for (int col = 1; col <= 4; col++)
				if (grid[row][col] != 0 && (grid[row - 1][col] == 0 || grid[row - 1][col] == grid[row][col]))
					return true;
		return false;
	}
	
	public boolean canMoveDown() {
		for (int row = 3; row >= 1; row--)
			for (int col = 1; col <= 4; col++)
				if (grid[row][col] != 0 && (grid[row + 1][col] == 0 || grid[row + 1][col] == grid[row][col]))
					return true;
		return false;
	}
	
	public boolean canMoveLeft() {
		for (int col = 2; col <= 4; col++)
			for (int row = 1; row <= 4; row++)
				if (grid[row][col] != 0 && (grid[row][col - 1] == 0 || grid[row][col - 1] == grid[row][col]))
					return true;
		return false;
	}
	
	public boolean canMoveRight() {
		for (int col = 3; col >= 1; col--)
			for (int row = 1; row <= 4; row++)
				if (grid[row][col] != 0 && (grid[row][col + 1] == 0 || grid[row][col + 1] == grid[row][col]))
					return true;
		return false;
	}
	
	public void moveUp() {
		int[][] oldGrid = new int[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				oldGrid[i][j] = grid[i][j];
		
		int[][] tgt = new int[5][5];
		
		boolean[][] merged = new boolean[5][5];
		
		for (int srcRow = 1; srcRow <= 4; srcRow++) {
			for (int col = 1; col <= 4; col++) {
				if (grid[srcRow][col] != 0) {
					int value = grid[srcRow][col];
					grid[srcRow][col] = 0;
					
					int tgtRow = srcRow;
					while (grid[tgtRow - 1][col] == 0)
						tgtRow--;
					
					if (grid[tgtRow - 1][col] == value && !merged[tgtRow - 1][col]) {
						tgtRow--;
						value *= 2;
						merged[tgtRow][col] = true;
					}
					
					grid[tgtRow][col] = value;
					
					tgt[srcRow][col] = (tgtRow - 1) * 4 + col - 1;
				}
			}
		}
		
		animate(oldGrid, tgt);
	}
	
	public void moveDown() {
		int[][] oldGrid = new int[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				oldGrid[i][j] = grid[i][j];
		
		int[][] tgt = new int[5][5];
		
		boolean[][] merged = new boolean[5][5];
		
		for (int srcRow = 4; srcRow >= 1; srcRow--) {
			for (int col = 1; col <= 4; col++) {
				if (grid[srcRow][col] != 0) {
					int value = grid[srcRow][col];
					grid[srcRow][col] = 0;
					
					int tgtRow = srcRow;
					while (grid[tgtRow + 1][col] == 0)
						tgtRow++;
					
					if (grid[tgtRow + 1][col] == value && !merged[tgtRow + 1][col]) {
						tgtRow++;
						value *= 2;
						merged[tgtRow][col] = true;
					}
					
					grid[tgtRow][col] = value;
					
					tgt[srcRow][col] = (tgtRow - 1) * 4 + col - 1;
				}
			}
		}
		
		animate(oldGrid, tgt);
	}
	
	public void moveLeft() {
		int[][] oldGrid = new int[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				oldGrid[i][j] = grid[i][j];
		
		int[][] tgt = new int[5][5];
		
		boolean[][] merged = new boolean[5][5];
		
		for (int srcCol = 1; srcCol <= 4; srcCol++) {
			for (int row = 1; row <= 4; row++) {
				if (grid[row][srcCol] != 0) {
					int value = grid[row][srcCol];
					grid[row][srcCol] = 0;
					
					int tgtCol = srcCol;
					while (grid[row][tgtCol - 1] == 0)
						tgtCol--;
					
					if (grid[row][tgtCol - 1] == value && !merged[row][tgtCol - 1]) {
						tgtCol--;
						value *= 2;
						merged[row][tgtCol] = true;
					}
					
					grid[row][tgtCol] = value;
					
					tgt[row][srcCol] = (row - 1) * 4 + tgtCol - 1;
				}
			}
		}
		
		animate(oldGrid, tgt);
	}
	
	public void moveRight() {
		int[][] oldGrid = new int[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				oldGrid[i][j] = grid[i][j];
		
		int[][] tgt = new int[5][5];
		
		boolean[][] merged = new boolean[5][5];
		
		for (int srcCol = 4; srcCol >= 1; srcCol--) {
			for (int row = 1; row <= 4; row++) {
				if (grid[row][srcCol] != 0) {
					int value = grid[row][srcCol];
					grid[row][srcCol] = 0;
					
					int tgtCol = srcCol;
					while (grid[row][tgtCol + 1] == 0)
						tgtCol++;
					
					if (grid[row][tgtCol + 1] == value && !merged[row][tgtCol + 1]) {
						tgtCol++;
						value *= 2;
						merged[row][tgtCol] = true;
					}
					
					grid[row][tgtCol] = value;
					
					tgt[row][srcCol] = (row - 1) * 4 + tgtCol - 1;
				}
			}
		}
		
		animate(oldGrid, tgt);
	}

	public void spawnTile() {
		ArrayList<Integer> emptySquares = new ArrayList<Integer>();
		
		for (int i = 1; i <= 4; i++)
			for (int j = 1; j <= 4; j++)
				if (grid[i][j] == 0)
					emptySquares.add((i - 1) * 4 + j - 1);
		
		int index = (int)(Math.random() * emptySquares.size());
		int row = emptySquares.get(index) / 4 + 1;
		int col = emptySquares.get(index) % 4 + 1;
		int value = Math.random() < 0.9 ? 2 : 4;
		
		Graphics g = getGraphics();
		
		for (int size = 2; size <= SQUARE_SIZE; size += 2) {
			int x = SQUARE_SIZE * (col - 1) + SPACING * col + BORDER + (SQUARE_SIZE - size) / 2;
			int y = SQUARE_SIZE * (row - 1) + SPACING * row + TOP_BORDER + (SQUARE_SIZE - size) / 2;
			drawTile(value, x, y, size);
			g.drawImage(offScreenImage, 0, 0, this);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		
		grid[row][col] = value;
		
		paint();
	}

	public void reset() {
		for (int i = 1; i <= 4; i++)
			for (int j = 1; j<= 4; j++)
				grid[i][j] = 0;
		
		gameIsOver = false;
		
		paint();
		
		spawnTile();
		spawnTile();
	}
	
	public void gameOver() {
		gameIsOver = true;
	}
	
	public void animate(int[][] oldGrid, int[][] tgt) {
		Graphics g = getGraphics();
		
		final int speed = 40;
		
		for (int i = 0; i < speed; i++) {
			
			drawGrid();
			
			for (int srcRow = 1; srcRow <= 4; srcRow++)
				for (int srcCol = 1; srcCol <= 4; srcCol++) {
					if (oldGrid[srcRow][srcCol] != 0) {
						int tgtRow = tgt[srcRow][srcCol] / 4 + 1;
						int tgtCol = tgt[srcRow][srcCol] % 4 + 1;
						
						int x = SQUARE_SIZE * (srcCol - 1) + SPACING * srcCol + BORDER + (SQUARE_SIZE + SPACING) * (tgtCol - srcCol) * i / speed;
						int y = SQUARE_SIZE * (srcRow - 1) + SPACING * srcRow + TOP_BORDER + (SQUARE_SIZE + SPACING) * (tgtRow - srcRow) * i / speed;
						
						drawTile(oldGrid[srcRow][srcCol], x, y);
					}
				}
			
			g.drawImage(offScreenImage, 0, 0, this);
		}
		
		paint();
	}
	
	public void drawGrid() {
		offScreenBuffer.setColor(new Color(0xbbada0));
		offScreenBuffer.fillRoundRect(BORDER, TOP_BORDER, GRID_SIZE, GRID_SIZE, 10, 10);
		
		offScreenBuffer.setColor(new Color(0xcdc1b5));
		for (int row = 1; row <= 4; row++)
			for (int col = 1; col <= 4; col++) {
				int x = SQUARE_SIZE * (col - 1) + SPACING * col + BORDER;
				int y = SQUARE_SIZE * (row - 1) + SPACING * row + TOP_BORDER;
				offScreenBuffer.fillRoundRect(x, y, SQUARE_SIZE, SQUARE_SIZE, 10, 10);
			}
	}
	
	public static void drawTile(int value, int x, int y) {
		int index = 0;
		while (value / Math.pow(2, index + 1) > 1)
			index++;
		offScreenBuffer.setColor(TILE_COLOURS[Math.min(index, 18)]);
		offScreenBuffer.fillRoundRect(x, y, SQUARE_SIZE, SQUARE_SIZE, 10, 10);
		
		Color textColour;
		if (value == 2)
			textColour = new Color(0x776e65);
		else if (value == 4) 
			textColour = new Color(0x786e65);
		else if (value <= 131072)
			textColour = new Color(0xf8f6f2);
		else
			textColour = new Color(0xf9f6f2);
		
		int fontSize;
		if (value <= 64)
			fontSize = (int)Math.round(SQUARE_SIZE * 34 / 60.0) / 2 * 2;
		else if (value <= 512)
			fontSize = (int)Math.round(SQUARE_SIZE * 28 / 60.0) / 2 * 2;
		else if (value <= 8192)
			fontSize = (int)Math.round(SQUARE_SIZE * 20 / 60.0) / 2 * 2;
		else if (value <= 65536)
			fontSize = (int)Math.round(SQUARE_SIZE * 16 / 60.0) / 2 * 2;
		else if (value <= 131072)
			fontSize = (int)Math.round(SQUARE_SIZE * 14 / 60.0) / 2 * 2;
		else
			fontSize = (int)Math.round(SQUARE_SIZE * 18 / 60.0) / 2 * 2;
		
		offScreenBuffer.setColor(textColour);
		offScreenBuffer.setFont(new Font("Clear Sans", Font.BOLD, fontSize));
		
		FontMetrics fm = offScreenBuffer.getFontMetrics();
		offScreenBuffer.drawString(String.valueOf(value), x + (SQUARE_SIZE - fm.stringWidth(String.valueOf(value))) / 2, y + (SQUARE_SIZE - fm.getHeight()) / 2 + fm.getAscent());
	}
	
	public static void drawTile(int value, int x, int y, int size) {
		int index = 0;
		while (value / Math.pow(2, index + 1) > 1)
			index++;
		offScreenBuffer.setColor(TILE_COLOURS[Math.min(index, 18)]);
		offScreenBuffer.fillRoundRect(x, y, size, size, 10, 10);
		
		Color textColour;
		if (value == 2)
			textColour = new Color(0x776e65);
		else if (value == 4) 
			textColour = new Color(0x786e65);
		else if (value <= 131072)
			textColour = new Color(0xf8f6f2);
		else
			textColour = new Color(0xf9f6f2);
		
		int fontSize;
		if (value <= 64)
			fontSize = (int)Math.round(size * 34 / 60.0) / 2 * 2;
		else if (value <= 512)
			fontSize = (int)Math.round(size * 28 / 60.0) / 2 * 2;
		else if (value <= 8192)
			fontSize = (int)Math.round(size * 20 / 60.0) / 2 * 2;
		else if (value <= 65536)
			fontSize = (int)Math.round(size * 16 / 60.0) / 2 * 2;
		else if (value <= 131072)
			fontSize = (int)Math.round(size * 14 / 60.0) / 2 * 2;
		else
			fontSize = (int)Math.round(size * 18 / 60.0) / 2 * 2;
		
		offScreenBuffer.setColor(textColour);
		offScreenBuffer.setFont(new Font("Clear Sans", Font.BOLD, fontSize));
		
		FontMetrics fm = offScreenBuffer.getFontMetrics();
		offScreenBuffer.drawString(String.valueOf(value), x + (size - fm.stringWidth(String.valueOf(value))) / 2, y + (size - fm.getHeight()) / 2 + fm.getAscent());
	}
	
	public static void main(String[] args) {
		frame = new JFrame("2048");
		Main panel = new Main();
		panel.setPreferredSize(new Dimension(GRID_SIZE + 2 * BORDER, GRID_SIZE + TOP_BORDER + BORDER));
		frame.setBackground(new Color(0xfaf8ef));
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


}
