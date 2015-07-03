import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class Main implements Runnable {
	private JFrame mainFrame;
	private JPanel buttonsPanel, consolePanel;
	public DisplayComponent displayPanel;
	private JTextArea console;
	public NetworkTrafficManager trafficManager;
	public JButton cancelButton;
	boolean cancel = false;
	private JScrollPane pane;

	@Override
	public void run() {
		System.out.println("Starting!");
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		trafficManager = new NetworkTrafficManager(this);
		setUpGui();
	}

	private void setUpGui(){
		mainFrame = new JFrame ("Regalotron 2000 Control Panel");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new FlowLayout());
		mainFrame.setResizable(false);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Actions"));
		buttonsPanel.setPreferredSize(new Dimension(125, 500));
		
		displayPanel = new DisplayComponent(this);
		displayPanel.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
		displayPanel.setPreferredSize(new Dimension(600, 350));
		displayPanel.setOpaque(true);
		
		consolePanel = new JPanel();
		consolePanel.setBorder(new TitledBorder(new EtchedBorder(), "Console"));
		consolePanel.setPreferredSize(new Dimension(600, 138));
		
		JPanel groupPanel = new JPanel();
		groupPanel.setPreferredSize(new Dimension(600, 500));
		groupPanel.add(displayPanel);
		groupPanel.add(consolePanel);
		
		//Set up buttons panel
		JButton requestButton = new JButton("Request");
		requestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (trafficManager.setUpFinished){
					new Thread(new Runnable() {	
						@Override
						public void run() {
							((JComponent) e.getSource()).setEnabled(false);
							cancelButton.setEnabled(true);
							int id = displayPanel.choosePutIn();
							cancelButton.setEnabled(false);
							cancel = false;
							((JComponent) e.getSource()).setEnabled(true);
							if (id > 0){
								trafficManager.writeToPi1("get " + id);
								writeToConsole("[INFO]: Taking pallet in slot " + id + " out of storage.");
							}
						}
					}).start();
				}else {
					writeToConsole("[INFO]: Request impossible, because not all pis are connected.");
				}
			}
		});
		buttonsPanel.add(requestButton);

		JButton putInButton = new JButton("Store item");
		putInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (trafficManager.setUpFinished){
					int cellId = displayPanel.defineFreeSlot();
					if (cellId > 0){
						trafficManager.writeToPi1("put " + cellId);
						writeToConsole("[INFO]: Storing pallet into slot " + cellId);
					}
					else
						writeToConsole("[ERROR]: No free slots!");
				}else {
					writeToConsole("[INFO]: Store item inpossible, because not all pis are connected.");
				}
			}
		});
		buttonsPanel.add(putInButton);
		
		/*JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (trafficManager.pi1 != null)
					trafficManager.writeToPi1("reset");
			}
		});
		buttonsPanel.add(resetButton);*/
		
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancel = true;
				synchronized (displayPanel.sync) {
					displayPanel.sync.notifyAll();
				}
			}
		});
		buttonsPanel.add(cancelButton);
		
		buttonsPanel.add(Box.createRigidArea(new Dimension(100, 290)));
		
		JButton emergencyStop = new JButton("STOP");
		emergencyStop.setFont(new Font("Arial", Font.BOLD, 25));
		emergencyStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (trafficManager.pi1 != null)
					trafficManager.writeToPi1("stop");
			}
		});
		buttonsPanel.add(emergencyStop);
		
		//Set up the console
		console = new JTextArea();
		console.setEditable(false);
		
		pane = new JScrollPane(console);
		pane.setPreferredSize(new Dimension(590, 110));
		consolePanel.add(pane);
		
		mainFrame.add(buttonsPanel);
		mainFrame.add(groupPanel);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	public void writeToConsole(String text){
		console.append(text + "\n");
		JScrollBar vertical = pane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum() );
	}
}
