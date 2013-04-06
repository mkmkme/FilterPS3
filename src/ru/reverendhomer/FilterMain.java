package ru.reverendhomer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class FilterMain extends JFrame {
	JTextField inputFile, outputFile;
	JLabel in, out, blur, process;
	JSlider slider;
	JButton searchIn, searchOut, filter;
	JFileChooser chooserIn, chooserOut;
	JMenuBar menuBar;
	
	public FilterMain() {
		super("Create a PS3 game cover!");
		add(addWidgets(), BorderLayout.CENTER);
		add(process, BorderLayout.PAGE_END);
	}
	
	private JComponent addWidgets() {
		JPanel panel = new JPanel();
		LayoutManager lm = new GridLayout(3, 3);
		panel.setLayout(lm);
		Border border = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		panel.setBorder(border);

		chooserIn = new JFileChooser();
		chooserOut = new JFileChooser();
		slider = new JSlider(1, 10);
		slider.setMajorTickSpacing(9);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		process = new JLabel();
		blur = new JLabel("Choose the blur");
		in = new JLabel("Choose image for cover");
		inputFile = new JTextField(5);
		searchIn = new JButton("Search");
		searchIn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = chooserIn.showDialog(null, "Open image");
				if (ret == JFileChooser.APPROVE_OPTION)
					inputFile.setText(chooserIn.getSelectedFile().getAbsolutePath());
			}
		});
		out = new JLabel("Path to save a cover:");
		outputFile = new JTextField(5);
		searchOut = new JButton("Search");
		searchOut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = chooserOut.showDialog(null, "Save");
				if (ret == JFileChooser.APPROVE_OPTION)
					outputFile.setText(chooserOut.getSelectedFile().getAbsolutePath() + ".jpg");
				
			}
		});
		filter = new JButton("Create a cover!");
		filter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						process.setText("Processing...");
						process.setVisible(true);
						filter.setEnabled(false);
						int blur = slider.getValue();
						File file = new File(inputFile.getText());
						try {
							BufferedImage image = ImageIO.read(file);
							FilterPS3 filter = new FilterPS3(image, blur);
							BufferedImage result = new BufferedImage(filter.width, filter.height, BufferedImage.TYPE_INT_RGB);
							filter.makeCover();
							for (int i = 0; i < filter.width; i++) {
								for (int j = 0; j < filter.height; j++) {
									result.setRGB(i, j, filter.pixels[i][j].getRGB());
								}
							}
							File file2 = new File(outputFile.getText());
							ImageIO.write(result, "jpg", file2);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						process.setText("Done!");
						filter.setEnabled(true);
					}
					
				});
				thread.start();
				
					
//				});
			}
			
		});
		panel.add(in);
		panel.add(inputFile);
		panel.add(searchIn);
		panel.add(out);
		panel.add(outputFile);
		panel.add(searchOut);
		panel.add(blur);
		panel.add(slider);
		panel.add(filter);
		return panel;
	}

	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JMenuBar menuBar = new JMenuBar();
				JMenu menu = new JMenu("Help");
				JMenuItem item = new JMenuItem("About");
				menu.add(item);
				item.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								JFrame aboutFrame = new JFrame("About");
								JPanel about = new JPanel();
								LayoutManager manager = new BorderLayout();
								about.setLayout(manager);
								JLabel copyright, image;
								JTextArea program;
								ImageIcon icon = new ImageIcon("icon.png");
								image = new JLabel(icon);
								copyright = new JLabel("© Преподобный Гомер", SwingConstants.RIGHT);
								program = new JTextArea("Create your PS3 game cover! v 0.1\n" +
										"Программа получает на вход изображение,\n" +
										" затем применяет к нему размытие и вертикальную/горизонтальную\n" +
										"наклейку (в зависимости от соотношения сторон изображения.)");
								program.setEditable(false);
								about.add(image, BorderLayout.PAGE_START);
								about.add(program, BorderLayout.CENTER);
								about.add(copyright, BorderLayout.PAGE_END);
								aboutFrame.setContentPane(about);
								aboutFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
								aboutFrame.pack();
								aboutFrame.setLocationRelativeTo(null);
								aboutFrame.setVisible(true);
							}
						});
					}
				});
				menuBar.add(menu);
				
				FilterMain f = new FilterMain();
				f.setJMenuBar(menuBar);
				f.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
				f.setResizable(false);
				f.setLocationRelativeTo(null);
				f.setVisible(true);
				
			}
		});
	}
}
