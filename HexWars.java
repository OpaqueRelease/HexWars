import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HexWar extends JFrame implements MouseMotionListener, MouseListener{
static final long serialVersionUID = 12; 
static int xWindow = 1000;
static int yWindow = 800;
static int nFields = 4;
static int[] stacks1 = new int[69]; //Player 1 stacks per tile
static int[] stacks2 = new int[69]; //Player 2 stacks per tile
static int[] oldStacks1 = new int[69];
static int[] oldStacks2 = new int[69];
static int availableStacks1 = 5; //Player 1 stacks available
static int availableStacks2 = 7; //Player 2 stacks available
static int[] maxStacks = new int[69]; //max stacks per tile
static Polygon[] polygons = new Polygon[70];
static int hoveredPoly = -1;
static int turnPlayer = 0;
static int reds = 0;
static int blues = 0;
static boolean redWin = false;
static boolean blueWin = false;
static boolean draw = false;

	public HexWar(){
		//create a window
		this.setBackground(Color.WHITE);
		setSize(new Dimension(xWindow, yWindow));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		JPanel p = new JPanel() {
			
			public void paint(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					int x = this.getWidth();
					int y = this.getHeight();
					int xPadding = x/10;
					int yPadding = y/10;
					int bit = (y - 2*yPadding) / (11 * 3) + 1;
					int diagonal = (x - 2*xPadding) / (2*nFields + 1);
					
					//Write stack sizes
					if(turnPlayer == 0) {
						g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
					}else {
						g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					}
					g2.setColor(Color.RED);
					g2.drawString("Player 1 stack size: " + availableStacks1, xPadding / 3, yPadding);
					g2.setColor(Color.BLUE);
					if(turnPlayer == 1) {
						g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
					}else {
						g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					}
					g2.drawString("Player 2 stack size: " + availableStacks2, xPadding / 3, y - yPadding);
					
					//skip button hex
					int xS = 1 * diagonal / 4 ;
					int yS = 3 * bit * 5 + yPadding;
					int[] xp = new int[6];
					int[] yp = new int[6];
					xp[0] = xS;
					yp[0] = yS + bit;
					xp[1] = xS + diagonal / 2;
					yp[1] = yS;
					xp[2] = xS + diagonal;
					yp[2] = yS + bit;
					xp[3] = xS + diagonal;
					yp[3] = yS + 3 * bit;
					xp[4] = xS + diagonal/2 ;
					yp[4] = yS + 4*bit;
					xp[5] = xS;
					yp[5] = yS + 3*bit;
					Polygon skip = new Polygon();
					skip.npoints = 6;
					skip.xpoints = xp;
					skip.ypoints = yp;
					polygons[69] = skip;
					if(hoveredPoly == 69) {
						g2.setColor(Color.GREEN.darker());
					}else {
						g2.setColor(Color.GREEN);
					}
					g2.fillPolygon(xp, yp, 6);
					g2.setColor(Color.BLACK);
					g2.drawPolygon(xp, yp, 6);
					g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));
					g2.drawString("skip", xS + diagonal / 4, yS + 2 * bit);
						
					//draw hexagon grid
					int counter = 0;
					for (int i = 0; i < 11; i++) {
						if (i <= 5) {
							for (int j = 0; j < i + 4; j++) {
								int xStart = (8 - i) * diagonal / 2 ;
								int yStart = 3 * bit * i + yPadding;
								int[] xPoints = new int[6];
								int[] yPoints = new int[6];
								xPoints[0] = xStart + j * diagonal;
								yPoints[0] = yStart + bit;
								xPoints[1] = xStart + diagonal / 2 + j * diagonal;
								yPoints[1] = yStart;
								xPoints[2] = xStart + diagonal + j * diagonal;
								yPoints[2] = yStart + bit;
								xPoints[3] = xStart + diagonal + j * diagonal;
								yPoints[3] = yStart + 3 * bit;
								xPoints[4] = xStart + diagonal/2 + j * diagonal;
								yPoints[4] = yStart + 4*bit;
								xPoints[5] = xStart + j * diagonal;
								yPoints[5] = yStart + 3*bit;
								Polygon n = new Polygon();
								n.npoints = 6;
								n.xpoints = xPoints;
								n.ypoints = yPoints;
								polygons[counter] = n;
								if(hoveredPoly == counter) {
									g2.setColor(Color.GRAY.brighter());
								}else {
									g2.setColor(Color.GRAY);
								}
								g2.fillPolygon(xPoints, yPoints, 6);
								g2.setColor(Color.BLACK);
								boolean red = stacks1[counter] > 0;
								if (stacks1[counter] == maxStacks[counter]) { //full tile
									g2.setColor(Color.RED);
									g2.fillPolygon(xPoints, yPoints, 6);
									g2.setColor(Color.BLACK);
								}
								if (stacks2[counter] == maxStacks[counter]) { //full tile
									g2.setColor(Color.BLUE);
									g2.fillPolygon(xPoints, yPoints, 6);
									g2.setColor(Color.BLACK);
								}
								for (int k = 0; k < maxStacks[counter]; k++) { //draw Stacks on the board
									int pad = diagonal/8;
									int the_rest = diagonal - 2*pad;
									int p = the_rest/maxStacks[counter];
									if (red) {
											if(stacks1[counter] > k) {
												g2.setColor(Color.RED);
												g2.fillRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
												g2.setColor(Color.BLACK);
											}
										
									}else {
											if(stacks2[counter] > k) {
												g2.setColor(Color.BLUE);
												g2.fillRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
												g2.setColor(Color.BLACK);
											}
									}
									g2.setColor(Color.BLACK);
									g2.drawRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
								}
								counter++;
								g2.drawPolygon(xPoints, yPoints, 6);
							}
						}else {
							for (int j = 0; j < 14 - i; j++) {
								int xStart = (i - 2) * diagonal / 2 ;
								int yStart = 3 * bit * i + yPadding;
								int[] xPoints = new int[6];
								int[] yPoints = new int[6];
								xPoints[0] = xStart + j * diagonal;
								yPoints[0] = yStart + bit;
								xPoints[1] = xStart + diagonal / 2 + j * diagonal;
								yPoints[1] = yStart;
								xPoints[2] = xStart + diagonal + j * diagonal;
								yPoints[2] = yStart + bit;
								xPoints[3] = xStart + diagonal + j * diagonal;
								yPoints[3] = yStart + 3 * bit;
								xPoints[4] = xStart + diagonal/2 + j * diagonal;
								yPoints[4] = yStart + 4*bit;
								xPoints[5] = xStart + j * diagonal;
								yPoints[5] = yStart + 3*bit;
								Polygon n = new Polygon();
								n.npoints = 6;
								n.xpoints = xPoints;
								n.ypoints = yPoints;
								polygons[counter] = n;
								if(hoveredPoly == counter) {
									g2.setColor(Color.GRAY.brighter());
								}else {
									g2.setColor(Color.GRAY);
								}
								g2.fillPolygon(xPoints, yPoints, 6);
								g2.setColor(Color.BLACK);
								boolean red = stacks1[counter] > 0;
								if (stacks1[counter] == maxStacks[counter]) { //full tile red
									g2.setColor(Color.RED);
									g2.fillPolygon(xPoints, yPoints, 6);
									g2.setColor(Color.BLACK);
								}
								if (stacks2[counter] == maxStacks[counter]) { //full tile black
									g2.setColor(Color.BLUE);
									g2.fillPolygon(xPoints, yPoints, 6);
									g2.setColor(Color.BLACK);
								}
								for (int k = 0; k < maxStacks[counter]; k++) {
									int pad = diagonal/8;
									int the_rest = diagonal - 2*pad;
									int p = the_rest/maxStacks[counter];
									if (red) {
											if(stacks1[counter] > k) {
												g2.setColor(Color.RED);
												g2.fillRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
												g2.setColor(Color.BLACK);
											}
									}else {
											if(stacks2[counter] > k) {
												g2.setColor(Color.BLUE);
												g2.fillRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
												g2.setColor(Color.BLACK);
											}
										
									}
									g2.setColor(Color.BLACK);
									g2.drawRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
								}
								counter++;
								g2.drawPolygon(xPoints, yPoints, 6);
							}	
						}
				} //for			
				
					if(redWin) {
						g2.setColor(Color.GREEN);
						g2.setFont(new Font("TimesRoman", Font.BOLD, 60));
						g2.drawString("Red Wins!", x / 3, y / 3);
					}
					if (blueWin){
						g2.setColor(Color.GREEN);
						g2.setFont(new Font("TimesRoman", Font.BOLD, 60));
						g2.drawString("Blue Wins!", x / 3, y / 3);
					}
					if(draw) {
						g2.setColor(Color.GREEN);
						g2.setFont(new Font("TimesRoman", Font.BOLD, 60));
						g2.drawString("It's a draw!", x / 3, y / 3);
					}
			}
		};
		
		setTitle("HexWar");
		Container c = this.getContentPane();
		c.add(p);
		c.addMouseMotionListener(this);
		c.addMouseListener(this);
	}
	
	public void mouseMoved(MouseEvent e){ //track the mouse and repaint
		boolean foundHex = false;
		for (int i = 0; i<70; i++) {
			if(polygons[i].contains(new Point(e.getX(), e.getY()))) {
				hoveredPoly = i;
				foundHex = true;
				break;
			}
		}
		if(!foundHex) {
			hoveredPoly = -1;
		}
		repaint();
	}
	
	public void mouseDragged(MouseEvent e){
		//do nothing
	}
	public void mousePressed(MouseEvent e){
		//do nothing
	}
	public void mouseReleased(MouseEvent e){
		if (hoveredPoly != -1) { //check if mouse is on a polygon
			if (hoveredPoly == 69) { //skip button -> give the turn to the other player
				changeTurnPlayer();
			}else { //click on a tile
				infect(); //perform action and change turn player if the action is completed
			}
		}
		repaint();
	}
	public void mouseClicked(MouseEvent e) {
		//do nothing
	}
	public void mouseExited(MouseEvent e) {
		//do nothing
	}
	public void mouseEntered(MouseEvent e) {
		//do nothing
	}
	public void changeTurnPlayer() { // performs end of turn and gives the turn to the other player
		if(turnPlayer == 0) {
			turnPlayer = 1;
		}else {
			turnPlayer = 0;
		}
		//end of turn infections
		int[] infectionBlue = new int[69];
		int[] infectionRed = new int[69];
		int counter = 0;
		for (int i = 0; i < 11; i++) { //check all hexes
			if (i < 5) {
				for (int j = 0; j < i + 4; j++) { // neighbors are -1, +1, -(i+3), -(i+4), +i+4 and +i+5
					int blue = 0;
					int red = 0;
					//count this tile
					if (stacks1[counter] == maxStacks[counter]) {
						red+=stacks1[counter];
					}
					if (stacks2[counter] == maxStacks[counter]) {
						blue+=stacks2[counter];
					}
					//count neighbor tiles
					if (j != 0) { //left neighbor and upper left
						if (maxStacks[counter - 1] == stacks2[counter - 1]) {
							blue+=stacks2[counter - 1];
						}
						if(maxStacks[counter - 1] == stacks1[counter - 1]) {
							red+=stacks1[counter - 1];
						}
						if(i != 0) {
							if (maxStacks[counter - (i + 4)] == stacks2[counter - (i + 4)]) {
								blue+=stacks2[counter - (i + 4)];
							}
							if(maxStacks[counter - (i + 4)] == stacks1[counter - (i + 4)]) {
								red+=stacks1[counter - (i + 4)];
							}
						}
					}
					if (j < i + 3) { //right neighbor and upper right
						if (maxStacks[counter + 1] == stacks2[counter + 1]) {
							blue+=stacks2[counter + 1];
						}
						if(maxStacks[counter + 1] == stacks1[counter + 1]) {
							red+=stacks1[counter + 1];
						}
						if (i != 0) {
							if (maxStacks[counter - (i + 3)] == stacks2[counter - (i + 3)]) {
								blue+=stacks2[counter - (i + 3)];
							}
							if(maxStacks[counter - (i + 3)] == stacks1[counter - (i + 3)]) {
								red+=stacks1[counter - (i + 3)];
							}
						}
					}
					
					 //bottom left and right
					if (maxStacks[counter + (i + 4)] == stacks2[counter + (i + 4)]) {
						blue+=stacks2[counter + (i + 4)];
					}
					if(maxStacks[counter + (i + 4)] == stacks1[counter + (i + 4)]) {
						red+=stacks1[counter + (i + 4)];
					}
					if (maxStacks[counter + (i + 5)] == stacks2[counter + (i + 5)]) {
						blue+=stacks2[counter + (i + 5)];
					}
					if(maxStacks[counter + (i + 5)] == stacks1[counter + (i + 5)]) {
						red+=stacks1[counter + (i + 5)];
					}
					
					// check results of red and blue pressure on the tile
					if(red > blue) {
						if(stacks2[counter] > 0) {
							infectionBlue[counter]--;
							blues--;
							availableStacks1++;
						}else if (stacks1[counter] != maxStacks[counter]){
							infectionRed[counter]++;
							reds++;
						}
					}else if (blue > red){
						if(stacks1[counter] > 0) {
							infectionRed[counter]--;
							availableStacks2++;
							reds--;
						}else if (stacks2[counter] != maxStacks[counter]){
							infectionBlue[counter]++;
							blues++;
						}
					}
					counter++;
				}
			}else if(i == 5) { //neighbors are -1, +1, +(14 - i), +(13 - i), -(i+3), -(i+4)
					for (int j = 0; j<9; j++) {
						int blue = 0;
						int red = 0;
						//count this tile
						if (stacks1[counter] == maxStacks[counter]) {
							red+=stacks1[counter];
						}
						if (stacks2[counter] == maxStacks[counter]) {
							blue+=stacks2[counter];
						}
						//count neighbor tiles
						if (j != 0) { //left neighbor and upper left
							if (maxStacks[counter - 1] == stacks2[counter - 1]) {
								blue+=stacks2[counter - 1];
							}
							if(maxStacks[counter - 1] == stacks1[counter - 1]) {
								red+=stacks1[counter - 1];
							}
							
							if (maxStacks[counter - (i + 4)] == stacks2[counter - (i + 4)]) {
								blue+=stacks2[counter - (i + 4)];
							}
							if(maxStacks[counter - (i + 4)] == stacks1[counter - (i + 4)]) {
								red+=stacks1[counter - (i + 4)];
							}
							
						}
						if (j < i + 3) { //right neighbor and upper right
							if (maxStacks[counter + 1] == stacks2[counter + 1]) {
								blue+=stacks2[counter + 1];
							}
							if(maxStacks[counter + 1] == stacks1[counter + 1]) {
								red+=stacks1[counter + 1];
							}
							if (maxStacks[counter - (i + 3)] == stacks2[counter - (i + 3)]) {
								blue+=stacks2[counter - (i + 3)];
							}
							if(maxStacks[counter - (i + 3)] == stacks1[counter - (i + 3)]) {
								red+=stacks1[counter - (i + 3)];
							}
							
						}
						
						 //bottom left and right
						if (maxStacks[counter + 13 - i] == stacks2[counter + 13 - i]) {
							blue+=stacks2[counter + 13 - i];
						}
						if(maxStacks[counter + 13 - i] == stacks1[counter + 13 - i]) {
							red+=stacks1[counter + 13 - i];
						}
						if (maxStacks[counter + 14 - i] == stacks2[counter + 14 - i]) {
							blue+=stacks2[counter + 14 - i];
						}
						if(maxStacks[counter + 14 - i] == stacks1[counter + 14 - i]) {
							red+=stacks1[counter + 14 - i];
						}
						
						// check results of red and blue pressure on the tile
						if(red > blue) {
							if(stacks2[counter] > 0) {
								infectionBlue[counter]--;
								availableStacks1++;
								blues--;
							}else if (stacks1[counter] != maxStacks[counter]){
								infectionRed[counter]++;
								reds++;
							}
						}else if (blue > red){
							if(stacks1[counter] > 0) {
								infectionRed[counter]--;
								availableStacks2++;
								reds--;
							}else if (stacks2[counter] != maxStacks[counter]){
								infectionBlue[counter]++;
								blues++;
							}
						}
						counter++;
					}
				
			}else {
				
				for (int j = 0; j < 14 - i; j++) { //neighbors are -1, +1, +(14 - i), +(13 - i), -(6 - i) and -(5 - i)
					
					int blue = 0;
					int red = 0;
					//count this tile
					if (stacks1[counter] == maxStacks[counter]) {
						red+=stacks1[counter];
					}
					if (stacks2[counter] == maxStacks[counter]) {
						blue+=stacks2[counter];
					}
					//count neighbor tiles
					if (j != 0) { //left neighbor and bottom left
						if (maxStacks[counter - 1] == stacks2[counter - 1]) {
							blue+=stacks2[counter - 1];
						}
						if(maxStacks[counter - 1] == stacks1[counter - 1]) {
							red+=stacks1[counter - 1];
						}
						if(i < 10) {
							if (maxStacks[counter + 13 - i] == stacks2[counter + 13 - i]) {
								blue+=stacks2[counter + 13 - i];
							}
							if(maxStacks[counter + 13 - i] == stacks1[counter + 13 - i]) {
								red+=stacks1[counter + 13 - i];
							}
						}
						
					}
					if (j < 13 - i) { //right neighbor and bottom right
						if (maxStacks[counter + 1] == stacks2[counter + 1]) {
							blue+=stacks2[counter + 1];
						}
						if(maxStacks[counter + 1] == stacks1[counter + 1]) {
							red+=stacks1[counter + 1];
						}
						if (i < 10) {
							if (maxStacks[counter + 14 - i] == stacks2[counter + 14 - i]) {
								blue+=stacks2[counter + 14 - i];
							}
							if(maxStacks[counter + 14 - i] == stacks1[counter + 14 - i]) {
								red+=stacks1[counter + 14 - i];
							}
						}
					}
					
					 //upper left and right
						if (maxStacks[counter -(14 - i)] == stacks2[counter -(14 - i)]) {
							blue+=stacks2[counter -(14 - i)];
						}
						if(maxStacks[counter -(14 - i)] == stacks1[counter -(14 - i)]) {
							red+=stacks1[counter -(14 - i)];
						}
						if (maxStacks[counter -(15 - i)] == stacks2[counter -(15 - i)]) {
							blue+=stacks2[counter -(15 - i)];
						}
						if(maxStacks[counter -(15 - i)] == stacks1[counter -(15 - i)]) {
							red+=stacks1[counter -(15 - i)];
						}
					
					
					// check results of red and blue pressure on the tile
					if(red > blue) {
						if(stacks2[counter] > 0) {
							infectionBlue[counter]--;
							availableStacks1++;
							blues--;
						}else if (stacks1[counter] != maxStacks[counter]){
							infectionRed[counter]++;
							reds++;
						}
					}else if (blue > red){
						if(stacks1[counter] > 0) {
							infectionRed[counter]--;
							availableStacks2++;
							reds--;
						}else if (stacks2[counter] != maxStacks[counter]){
							infectionBlue[counter]++;
							blues++;
						}
					}
					
					counter++;
				}	
			}
		} //for		
		//update stacks
		oldStacks1 = stacks1;
		oldStacks2 = stacks2;
		for(int t = 0; t<69; t++) {
			stacks1[t] += infectionRed[t];
			stacks2[t] += infectionBlue[t];
		}
		 
		endGame();
		repaint();
	}
	public void endGame() { //checks if the game is over
		if(reds == 0 && availableStacks1 == 0) {
			blueWin = true;
		}
		if(blues == 0 && availableStacks2 == 0) {
			redWin = true;
		}
		
			boolean c = true;
			for(int l = 0; l < 69; l++) { //nothing is changing on the board
				if (stacks1[l] != oldStacks1[l] || stacks2[l] != oldStacks2[l]) {
					c = false;
					break;
				}
			}
			if(c) {
				boolean s = true;
				int redC = 0;
				int blueC = 0;	
				for(int i = 0; i < 69; i++) { // all tiles are full
					if (stacks1[i] != maxStacks[i] && stacks2[i] != maxStacks[i]) {
						s = false;
						break;
					}
					if (stacks1[i] > 0) {
						redC++;
					}else {
						blueC++;
					}
				}
				if(s || availableStacks1 == 0 && availableStacks2 == 0) {
					if (blueC > redC) {
						blueWin = true;
					}else if (redC > blueC) {
						redWin = true;
					}else {
						draw = true;
					}
				}
			}	
	}
	public void infect() {
		if (turnPlayer == 0) {
			if(stacks2[hoveredPoly] == 0 && stacks1[hoveredPoly] < maxStacks[hoveredPoly] && availableStacks1 > 0) {
				stacks1[hoveredPoly]++;
				availableStacks1--;
				reds++;
				repaint();
				changeTurnPlayer();
			}
		}else {
			if(stacks1[hoveredPoly] == 0 && stacks2[hoveredPoly] < maxStacks[hoveredPoly] && availableStacks2 > 0) {
				stacks2[hoveredPoly]++;
				availableStacks2--;
				blues++;
				repaint();
				changeTurnPlayer();
			}
			
		}
	}
	public static void main (String arg[]){
		Random r = new Random();
		for (int i = 0; i < 69; i++) {
			maxStacks[i] = r.nextInt(9) + 1;
		}
		//create and run the game
		new HexWar();
	}
}
	

