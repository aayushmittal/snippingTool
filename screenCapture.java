//package sample;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.event.*;
public class screenCapture extends JFrame{	
	private JLabel mousepanel ;
	private JLabel statusbar;
	private ImageIcon imageicon;
	private ImageIcon greyimageicon;
	private BufferedImage cap;
	private BufferedImage greyCap;
	private PAIR initial, start, end;	
	private int start_x, start_y;
	GraphicsDevice device = GraphicsEnvironment
	        .getLocalGraphicsEnvironment().getScreenDevices()[0];	
	public screenCapture(int optionSelected) throws AWTException, IOException
	{
		end = new PAIR(); //to save end coordinates
		cap = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		// get full screen
		imageicon = new ImageIcon(cap);		
		if(optionSelected == 0){			
			//user selected full screen so return from here itself
			SelectionFrame selectionFrame = new SelectionFrame(imageicon); //open options window
			// return the captured image
			screenCapture.this.setVisible(false);
			screenCapture.this.dispose();			//remove current window
		}else{
			greyCap = new BufferedImage(cap.getWidth(), cap.getHeight(),  
				    BufferedImage.TYPE_BYTE_GRAY);
				    //Make the scren grey to show that the screen can now be cropped 
			statusbar = new JLabel("default");					
			Graphics gr = greyCap.getGraphics();
			gr.drawImage(cap, 0, 0, null);
			gr.dispose(); //create the grey image
			greyimageicon = new ImageIcon(greyCap);
			mousepanel = new JLabel(greyimageicon);
			mousepanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			Handlerclass handler = new Handlerclass();
			mousepanel.addMouseListener(handler);
			mousepanel.addMouseMotionListener(handler);
			// to listen mouse events
			add(statusbar, BorderLayout.SOUTH);			
			add(mousepanel);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
			//Gets screen
			device.setFullScreenWindow(null);
			device.setFullScreenWindow(this);	
			//open capture in full screen to crop		
			setVisible(true);
		}		
	}	
	static BufferedImage deepCopy(BufferedImage inp)
	{
			//copy image
			ColorModel cm = inp.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = inp.copyData(null);
			return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}	  
	public class Handlerclass implements MouseListener, MouseMotionListener
	{
		//Mouse events
		public void mouseClicked(MouseEvent event)
		{
			statusbar.setText("Clicked");
		}		
		public void mousePressed(MouseEvent event)
		{	
			//set initial and start
			initial = new PAIR(event.getX(), event.getY());
			start = new PAIR(event.getX(), event.getY());
			statusbar.setText("Pressed");
		}
		public void mouseReleased(MouseEvent event)
		{	
			try{
				//Set coordinates so that image can be cropped in any quadrant 
				setCoordinates(event.getX(), event.getY());
				BufferedImage copy = cap.getSubimage(start.x, start.y, end.x-start.x, end.y-start.y);
				//image cropped
				BufferedImage crop = new BufferedImage(copy.getWidth(), copy.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = crop.createGraphics();
				g.drawImage(copy,0,0,null); //create cropped image again to save space
				ImageIcon sendImage = new ImageIcon(crop);				
				device.setFullScreenWindow(null);
				setVisible(false);
				dispose(); //remove window Aftter crop
				SelectionFrame selectionFrame = new SelectionFrame(sendImage);	
				//show crop with options			
			}catch(Exception e){
				System.out.print("System Error");
			}
		}
		public void mouseEntered(MouseEvent event)
		{
			statusbar.setText("Entered Area");
		}
		public void mouseExited(MouseEvent event)
		{
			statusbar.setText("EXIT");
		}
		public void mouseDragged(MouseEvent event)
		{
			statusbar.setText("Dragged");
			//copy image
			BufferedImage temp = deepCopy(greyCap);
			Graphics2D g2d = temp.createGraphics();
			//create rectangle to mark the selected area
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
			g2d.setColor(Color.RED);
			setCoordinates(event.getX(), event.getY());
			g2d.drawRect(start.x, start.y, end.x-start.x, end.y-start.y);
			imageicon.setImage(temp);
			mousepanel.setIcon(null);
			mousepanel.setIcon(imageicon); //draw image with rectangle on it					
		}
		public void mouseMoved(MouseEvent event)
		{
			;
		}
	}
	public void setCoordinates(int event_x, int event_y)
	{
		//detects drag direction and sets coordinates to crop
		int diff_x = event_x - initial.x;
		int diff_y = event_y - initial.y;
		if(diff_x>=0 && diff_y>=0)
			end.set(event_x,  event_y);
		else if(diff_x<=0 && diff_y>=0)
		{
			end.y = event_y;
			end.x = initial.x;
			start.x = event_x;
		}
		else if(diff_x>=0 && diff_y<=0)
		{
			end.x = event_x;
			end.y = initial.y;
			start.y = event_y;
		}
		else
		{
			end.x = initial.x;
			end.y = initial.y;
			start.x = event_x;
			start.y = event_y;
		}
	}	
}
