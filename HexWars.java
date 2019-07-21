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
static int availableStacks1 = 10; //Player 1 stacks available
static int availableStacks2 = 10; //Player 2 stacks available
static int[] maxStacks = new int[69]; //max stacks per tile
static Polygon[] polygons = new Polygon[70];
static int hoveredPoly = -1;
static int turnPlayer = 0;

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
								for (int k = 0; k < maxStacks[counter]; k++) {
									int pad = diagonal/8;
									int the_rest = diagonal - 2*pad;
									int p = the_rest/maxStacks[counter];
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
								for (int k = 0; k < maxStacks[counter]; k++) {
									int pad = diagonal/8;
									int the_rest = diagonal - 2*pad;
									int p = the_rest/maxStacks[counter];
									g2.drawRect(xStart + j * diagonal + pad + k*p, yStart + bit, p, 2*bit);
								}
								counter++;
								g2.drawPolygon(xPoints, yPoints, 6);
							}	
						}
				} //for			
				
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
	public void changeTurnPlayer() {
		if(turnPlayer == 0) {
			turnPlayer = 1;
		}else {
			turnPlayer = 0;
		}
	}
	public void infect() {
		if (turnPlayer == 0) {
			if(stacks2[hoveredPoly] == 0 && stacks1[hoveredPoly] < maxStacks[hoveredPoly]) {
				stacks1[hoveredPoly]++;
				changeTurnPlayer();
			}
		}else {
			if(stacks1[hoveredPoly] == 0 && stacks2[hoveredPoly] < maxStacks[hoveredPoly]) {
				stacks2[hoveredPoly]++;
				changeTurnPlayer();
			}
			
		}
	}

	
	public static void main (String arg[]) throws InterruptedException {
		Random r = new Random();
		for (int i = 0; i < 69; i++) {
			maxStacks[i] = r.nextInt(9) + 1;
		}
		//create and run the game
		new HexWar();
	}
}
	

