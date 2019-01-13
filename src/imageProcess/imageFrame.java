package imageProcess;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class imageFrame extends JFrame implements ActionListener {
	private JLabel leftView = null;
	private JLabel rightView = null;
	private ImageIcon leftImage = null;
	private ImageIcon rightImage = null;
	private JButton leftFileBtn = null;
	private JButton rightFileBtn = null;
	
	private JButton compareBtn = null;
	private JLabel resultLabel = null;
	
	private BufferedImage buffLeft = null;
	private BufferedImage buffRight = null;
	
	private int avgLeft = 0;
	private int avgRight = 0;
	private ArrayList leftArr = new ArrayList();
	private ArrayList rightArr = new ArrayList();
	
	// constructor
	public imageFrame() {
		super.setTitle("An Image Similarity Comparing program");
		super.setBounds(20, 80, 900, 600);
		super.setLayout(null);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setVisible(true);
		
		leftImage = new ImageIcon("");
		
		leftView = new JLabel("Left view",leftImage,SwingConstants.CENTER);
		leftView.setBounds(50, 50, 300, 300);
		super.add(leftView);
		
		leftFileBtn = new JButton();
		leftFileBtn.setText("File choice");
		leftFileBtn.setBounds(150, 380, 100, 20);
		super.add(leftFileBtn);
		leftFileBtn.addActionListener(this);
		
		rightImage = new ImageIcon("");
		
		rightView = new JLabel("Right View",rightImage,SwingConstants.CENTER);
		rightView.setBounds(500, 50, 300, 300);
		super.add(rightView);
		
		rightFileBtn = new JButton();
		rightFileBtn.setText("File choice");
		rightFileBtn.setBounds(600, 380, 100, 20);
		super.add(rightFileBtn);
		rightFileBtn.addActionListener(this);
		
		compareBtn = new JButton();
		compareBtn.setText("Compare now!");
		compareBtn.setBounds(300, 450, 100, 20);
		super.add(compareBtn);
		compareBtn.addActionListener(this);
		
		resultLabel = new JLabel();
		resultLabel.setText("Similarity is XX %");
		resultLabel.setBounds(450, 450, 200, 20);
		super.add(resultLabel);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if ( arg0.getSource() == leftFileBtn ) {
			JFileChooser jfc=new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
			jfc.showDialog(new JLabel(), "Pick a picture");
			File file=jfc.getSelectedFile();
			String filePath = jfc.getSelectedFile().getAbsolutePath();
			System.out.println(filePath);
			resizeImageAndDisplay(filePath,true,300);
		} else if (arg0.getSource() == rightFileBtn) {
			JFileChooser jfc=new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
			jfc.showDialog(new JLabel(), "Pick a picture");
			File file=jfc.getSelectedFile();
			String filePath = jfc.getSelectedFile().getAbsolutePath();
			System.out.println(filePath);
			resizeImageAndDisplay(filePath,false,300);
		} else if ( arg0.getSource() == compareBtn ) {
			compareSimilarity();
		}
 	}
	
	private void compareSimilarity() {
		if ( buffLeft == null || buffRight == null ) {
			resultLabel.setText("Error in pics");
			return;
		} else {
			resultLabel.setText("Calculating");
			
			// Calculating Average value
			avgLeft = getAverage(buffLeft);
			avgRight = getAverage(buffRight);
			System.out.println("Left Average :" + avgLeft);
			System.out.println("Right Average :" + avgRight);
			
			// Building an array
			calcDist(buffLeft,avgLeft,leftArr);
			calcDist(buffRight,avgRight,rightArr);
			System.out.println("Left arr :" + leftArr.size());
			System.out.println("Right arr :" + rightArr.size());
			
			// Calculate counter and percentage 
			int len = 0;
			if ( leftArr.size() > rightArr.size() ) {
				len = rightArr.size();
			} else {
				len = leftArr.size();
			}
			
			int counter = 0;
			for ( int i = 0; i < len; i++) {
				if ( leftArr.get(i) == rightArr.get(i) ) {
					counter += 1;
				}
			}
			double percent = counter * 100.0/len;
			
			// Display
			String str = String.format("Similarity = %.2f percent", percent);
			resultLabel.setText(str);
		}
	}
	
	private void calcDist(BufferedImage image,int avg,ArrayList list) {
		list.clear();
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		int r,g,b = 0;
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = image.getRGB(i, j);
				r = (pixel & 0xff0000) >> 16;
				g = (pixel & 0xff00) >> 8;
				b = (pixel & 0xff);
				int val = r+g+b;
				if (val >= avg) {
					list.add(1);
				} else {
					list.add(0);
				}
			}
		}
	}
	
	private int getAverage( BufferedImage image ) {
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		System.out.println("width=" + width + ",height=" + height + ".");
		System.out.println("minx=" + minx + ",miniy=" + miny + ".");
		int sum = 0;
		int r,g,b = 0;
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = image.getRGB(i, j);
				r = (pixel & 0xff0000) >> 16;
				g = (pixel & 0xff00) >> 8;
				b = (pixel & 0xff);
				sum += r + g +b;
			}
		}
		int avg = sum / ((width - minx)*(height - miny)); 
		return avg;
	}
	
	private void resizeImageAndDisplay(String picPath,boolean isLeft,int size) {
		try {
			InputStream is = new FileInputStream(new File(picPath));
			OutputStream os = null;
			if (isLeft == true) {
				os = new FileOutputStream(new File("left.jpg"));
			} else {
				os = new FileOutputStream(new File("right.jpg"));
			}
			
			try {
				BufferedImage prevImage = ImageIO.read(is);
		        double width = prevImage.getWidth();
		        double height = prevImage.getHeight();
		        double percent = size/width;
		        int newWidth = (int)(width * percent);
		        int newHeight = (int)(height * percent);
		        BufferedImage image = new BufferedImage(newWidth, newHeight,
		        		BufferedImage.TYPE_INT_BGR);
		        Graphics graphics = image.createGraphics();
		        graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
		        String format = "jpg";
		        ImageIO.write(image, format, os);
		        
		        os.flush();
		        is.close();
		        os.close();
		        
		        if ( isLeft == true ) {
		        	leftImage = new ImageIcon("left.jpg");
		        	leftView.setIcon(leftImage);
		        	buffLeft = image;
		        } else {
		        	rightImage = new ImageIcon("right.jpg");
		        	rightView.setIcon(rightImage);
		        	buffRight = image;
		        }
		        
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
