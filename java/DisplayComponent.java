import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JPanel;


public class DisplayComponent extends JPanel{
	private static final long serialVersionUID = 1L;
	private boolean [][] full = new boolean [3] [2];
	public int armX = 0, armY = 1;
	private Main main;
	private Color cell1Color, cell2Color, cell3Color, cell4Color, cell5Color, cell6Color;
	private boolean buttonsVisible = false;
	private int highlightedButton = 0;
	Object sync = new Object();

	public DisplayComponent(Main main) {
		for (int i = 0; i < 3; i++){
			full [i][0] = false;
			full [i][1] = false;
		}
		setLayout(new FlowLayout());
		this.main = main;
		addMouseListener(new MouseAdapter() {
			@Override
			public synchronized void mouseClicked(MouseEvent e) {
				buttonsVisible = false;
				synchronized (sync) {
					sync.notifyAll();
				}		
				System.out.println("click");
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				int width = getWidth();
				if (x < (width / 2) - 75 && x > (width / 2) - 225 && y < 130 && y > 30){
					highlightedButton = 1;
				}else if (x < (width / 2) + 75 && x > (width / 2) - 75 && y < 130 && y > 30){
					highlightedButton = 2;
				}else if (x < (width / 2) + 225 && x > (width / 2) + 75 && y < 130 && y > 30){
					highlightedButton = 3;
				}else if (x < (width / 2) - 75 && x > (width / 2) - 225 && y < 230 && y > 130){
					highlightedButton = 4;
				}else if (x < (width / 2) + 75 && x > (width / 2) - 75 && y < 230 && y > 130){
					highlightedButton = 5;
				}else if (x < (width / 2) + 225 && x > (width / 2) + 75 && y < 230 && y > 130){
					highlightedButton = 6;
				}else 
					highlightedButton = 0;
				repaint();
			}
		});
		cell1Color = Color.GREEN;
		cell2Color = Color.GREEN;
		cell3Color = Color.GREEN;
		cell4Color = Color.GREEN;
		cell5Color = Color.GREEN;
		cell6Color = Color.GREEN;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth();
		//g.clearRect(0, 0, getWidth(), getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(cell1Color);
		if (buttonsVisible && highlightedButton == 1 && full[0][0])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) - 225, 30, 150, 100, true);
		g2d.setColor(cell2Color);
		if (buttonsVisible && highlightedButton == 2 && full[1][0])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) - 75, 30, 150, 100, true);
		g2d.setColor(cell3Color);
		if (buttonsVisible && highlightedButton == 3 && full[2][0])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) + 75, 30, 150, 100, true);
		g2d.setColor(cell4Color);
		if (buttonsVisible && highlightedButton == 4 && full[0][1])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) - 225, 130, 150, 100, true);
		g2d.setColor(cell5Color);
		if (buttonsVisible && highlightedButton == 5 && full[1][1])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) - 75, 130, 150, 100, true);
		g2d.setColor(cell6Color);
		if (buttonsVisible && highlightedButton == 6 && full[2][1])
			g2d.setColor(Color.CYAN);
		g2d.fill3DRect((width / 2) + 75, 130, 150, 100, true);
		g2d.setColor(Color.BLACK);
		g2d.draw3DRect((width / 2) - 225, 30, 450, 200, true);
		
		//Draw black lines
		g.fillRect((width / 2) - 280, 250, 505, 4);
		g.fillRect((width / 2) + 245, 30, 4, 200);
		
		//Calculate and print stats
		int slotsFull = 0;
		for (int i = 0; i < 3; i++){
			if (full [i][0])
				slotsFull ++;
			if (full [i][1])
				slotsFull ++;
		}
		
		g2d.setFont(new Font("Arial", Font.BOLD, 25));
		g2d.drawString("Slots full: " + slotsFull, (width / 2) - 290, 340);
		g2d.drawString("Slots free: " + (6 - slotsFull), (width / 2) + 115, 340);
		
		//Draw position indicators
		if (armX == 0)
				g2d.fill3DRect((width / 2) - 255, 255, 4, 10, true);
		else if (armX == 1)
				g2d.fill3DRect((width / 2) - 150 - 2, 255, 4, 10, true);
		else if (armX == 2)
				g2d.fill3DRect((width / 2) - 2, 255, 4, 10, true);
		else if (armX == 3)
				g2d.fill3DRect((width / 2) + 150 - 2, 255, 4, 10, true);
		
		if (armY == 1)
			g2d.fill3DRect((width / 2) + 250, 180 - 2, 10, 4, true);
		else if (armY == 2)
			g2d.fill3DRect((width / 2) + 250, 80 - 2, 10, 4, true);
		
		//Paint the slot numbers
		g2d.drawString("Slot 1", (width / 2) - 190, 90);
		g2d.drawString("Slot 2", (width / 2) - 40, 90);
		g2d.drawString("Slot 3", (width / 2) + 110, 90);
		g2d.drawString("Slot 4", (width / 2) - 190, 190);
		g2d.drawString("Slot 5", (width / 2) - 40, 190);
		g2d.drawString("Slot 6", (width / 2) + 110, 190);
	}
	
	public void setCellOcupancy(int cellId, boolean full){
		switch (cellId){
			case 1:
				this.full [0][0] = full;
				break;
			case 2:
				this.full [1][0] = full;
				break;
			case 3:
				this.full [2][0] = full;
				break;
			case 4:
				this.full [0][1] = full;
				break;
			case 5:
				this.full [1][1] = full;
				break;
			case 6:
				this.full [2][1] = full;
				break;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				NetworkTrafficManager mng = main.trafficManager;
				int slotsFull = 0;
				for (int i = 0; i < 3; i++){
					if (DisplayComponent.this.full [i][0])
						slotsFull ++;
					if (DisplayComponent.this.full [i][1])
						slotsFull ++;
				}
				while (mng.pi2 == null){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mng.writeToPi2("free " + (6 - slotsFull));
			}
		}).start();
		
		//Update colors
		if (this.full [0][0])
			cell1Color = Color.RED;
		else
			cell1Color = Color.GREEN;
		if (this.full [1][0])
			cell2Color = Color.RED;
		else
			cell2Color = Color.GREEN;
		if (this.full [2][0])
			cell3Color = Color.RED;
		else
			cell3Color = Color.GREEN;
		if (this.full [0][1])
			cell4Color = Color.RED;
		else
			cell4Color = Color.GREEN;
		if (this.full [1][1])
			cell5Color = Color.RED;
		else
			cell5Color = Color.GREEN;
		if (this.full [2][1])
			cell6Color = Color.RED;
		else
			cell6Color = Color.GREEN;
		repaint();
	}
	
	public int defineFreeSlot(){
		if (!full[0][0])
			return 1;
		else if (!full[0][1])
			return 4;
		else if (!full[1][0])
			return 2;
		else if (!full[1][1])
			return 5;
		else if (!full[2][0])
			return 3;
		else if (!full[2][1])
			return 6;
		else
			return 0;
	}
	
	public int choosePutIn(){
		buttonsVisible = true;
		synchronized(sync){
			try {
				sync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (main.cancel){
			buttonsVisible = false;
			return 0;
		}
		System.out.println("exiting loop");
		repaint();
		if (highlightedButton == 1 && full[0][0])
			return 1;
		else if (highlightedButton == 2 && full[1][0])
			return 2;
		else if (highlightedButton == 3 && full[2][0])
			return 3;
		else if (highlightedButton == 4 && full[0][1])
			return 4;
		else if (highlightedButton == 5 && full[1][1])
			return 5;
		else if (highlightedButton == 6 && full[2][1])
			return 6;
		else
			return 0;
	}
}
