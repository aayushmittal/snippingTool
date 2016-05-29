import java.awt.*;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.math.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
public class SelectionFrame extends JFrame {
	private JButton capture;
	private JButton captureNew;
	private JButton cancel;
	private JButton save;
	private ImageIcon image;
	private JPanel imageArea;
	private JPanel buttonArea;
	private JComboBox dropDownMenu;
	private int optionSelected;
	private static String[] list = {"Fullscreen","Custom"}; 	
	public SelectionFrame(ImageIcon imageIcon) {
		super("Snipping Tool");
		setLayout(new FlowLayout());
		optionSelected = 0; //0 = Fullscreen; 1 = Custom
		imageArea = new JPanel(); // Where Image will be displayed it available. 
		buttonArea = new JPanel(); // For all the options that a user can select
		dropDownMenu = new JComboBox(list); //If the image is not present, give option for fullscreen or custom
		//default is fullscreen
		dropDownMenu.addItemListener(new ItemListener() {	
			// To get what the user has chosem		
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange() == ItemEvent.SELECTED){
					// User's decision
					optionSelected = dropDownMenu.getSelectedIndex();
				}
			}
		});
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		capture = new JButton("Capture"); // To start Snipping
		capture.addActionListener(new ActionListener() {	
			//Button's functionality		
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionFrame.this.setVisible(false); //Hides current window
				SelectionFrame.this.dispose(); //Removes currut window
				try{
					Thread.sleep(500); //Time given so that current window is hidden before going furthur
				}catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				try{
					screenCapture s = new screenCapture(optionSelected);
					//Constructor to screenCapture. Cropping window opens
				}catch(Exception exception){
					System.out.print(" error in screen capture");
				}							
			}
		});
		if(imageIcon == null){
			//No image was captured or program is started the first time
			imageArea.add(dropDownMenu, BorderLayout.CENTER);
			//Give fullscreen or custom options
			buttonArea.add(capture,BorderLayout.CENTER);
			//only capture option needed
			add(imageArea,BorderLayout.NORTH);
			setSize(350,100);
		}
		else if(imageIcon != null){
			//Image is captured
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			captureNew = new JButton("Capture New");
			//If the captured image is to be discarded for a new capture
			captureNew.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					SelectionFrame.this.setVisible(false);
					SelectionFrame.this.dispose();
					//same functionalit as the capture button
					SelectionFrame s = new SelectionFrame(null);
				}
			});
			cancel = new JButton("Cancel");
			// To discard current capture and close application
			save = new JButton("Save");
			//to save current capture
			cancel.addActionListener(new ActionListener() {
				//cancel button functionality
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					SelectionFrame.this.setVisible(false); //hide current frame
					SelectionFrame.this.dispose(); // close the current frame
				}
			});
			save.addActionListener(new ActionListener() {
			//save button functionality				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//save image
					ImageIcon imgIcon = imageIcon;
					JFileChooser fileChooser = new JFileChooser(); //To open location browser
					fileChooser.setDialogTitle("Select a directory to save image");
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int val = fileChooser.showSaveDialog(SelectionFrame.this);
					if(val == JFileChooser.APPROVE_OPTION){	
						//Enter file name to be saved
						String name = JOptionPane.showInputDialog("Enter File Name");
						Date date = new Date();						
						String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
						File file;
						if(name!=null)
							file = new File(selectedPath + "/" +name + ".jpg");
						else
							//if file name does not exist, create one depending on time stamp
							file = new File(selectedPath + "/" +date.getTime() + ".jpg");
						BufferedImage buffimg = new BufferedImage(imgIcon.getIconWidth(),
								imgIcon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
						Graphics g = buffimg.getGraphics();
						imgIcon.paintIcon(null, g, 0, 0);
						g.dispose();
						//image created to save
						try {
							ImageIO.write(buffimg, "jpg", file); //image saved
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						SelectionFrame.this.setVisible(false);
						SelectionFrame.this.dispose();		//end applicaiton on save				
						
					}else if(val == JFileChooser.CANCEL_OPTION){
						//Do Nothing, save canceled
					}
				}
			});			
			image = imageIcon;
			buttonArea.add(captureNew,BorderLayout.WEST);
			buttonArea.add(save,BorderLayout.CENTER);
			buttonArea.add(cancel,BorderLayout.EAST);
			//add all three buttons for user to choose functionality
			imageArea.setPreferredSize(new Dimension(image.getIconWidth(),image.getIconHeight()));
			//Set size
			JScrollPane sp = new JScrollPane(new JLabel(image));
			//make it scroll bar
			Dimension dimen = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
			dimen.setSize(dimen.getWidth(), dimen.getHeight()-100);
			sp.setPreferredSize(dimen);
			add(sp,BorderLayout.CENTER);
			//setSize(getMaximumSize());
			//setSize(imageLabel.getWidth() + 350,imageLabel.getWidth() + 100);			
		}
		add(buttonArea,BorderLayout.SOUTH);
		setVisible(true); //show window
	}
}
