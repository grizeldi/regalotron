import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class NetworkTrafficManager implements Runnable{
	public Socket pi1, pi2, pi3;
	private Main main;
	public boolean exit = false;
	public boolean setUpFinished = false;
	
	public NetworkTrafficManager(Main main){
		this.main = main;
		new Thread(this).start();
	}

	@Override
	public void run() {
		//Set up the sockets
		try (ServerSocket server = new ServerSocket(3333);){
			while (pi1 == null || pi2 == null || pi3 == null){
				Socket tempSocket = server.accept();
				BufferedReader socketReader = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
				String string = socketReader.readLine();
				if (string.equals("pi1")){
					pi1 = tempSocket;
					pi1.setTcpNoDelay(true);
					main.writeToConsole("[INFO]: Pi1 connected.");
				}
				else if (string.equals("pi2")){
					pi2 = tempSocket;
					pi2.setTcpNoDelay(true);
					main.writeToConsole("[INFO]: Pi2 connected.");
					Thread.sleep(1000);
					main.displayPanel.setCellOcupancy(6, false);
				}
				else if (string.equals("pi3")){
					pi3 = tempSocket;
					main.writeToConsole("[INFO]: Pi3 connected.");
				}
				else {
					socketReader.close();
					tempSocket.close();
					main.writeToConsole("[Warning]: Weird meetup command recieved.");
				}
			}
			setUpFinished = true;
			main.writeToConsole("All Raspberry Pis are connected.");
				
			//Run the listener threads
			//Pi1 listener
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader socketReader = new BufferedReader(new InputStreamReader(pi1.getInputStream()));
						
						while (!exit){
							String s = socketReader.readLine();
							if (s == null){
								main.writeToConsole("[WARNING]: Pi 1 lost connection, shutting down all networking.");
								exit = true;
							}
							else if (s.contains("xPos")){
								int xPos = Integer.parseInt(s.split(" ")[1]);
								main.displayPanel.armX = xPos;
								main.displayPanel.repaint();
							}else if (s.contains("yPos")){
								int yPos = Integer.parseInt(s.split(" ")[1]);
								main.displayPanel.armY = yPos;
								main.displayPanel.repaint();
							}
							main.displayPanel.repaint();
							System.out.println("Raw input log from Pi1: " + s);
						}
						socketReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			//Pi3 listener
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader socketReader = new BufferedReader(new InputStreamReader(pi3.getInputStream()));
						
						while (!exit){
							String input = socketReader.readLine();
							if (input == null){
								main.writeToConsole("[WARNING]: Pi 3 lost connection, shutting down all networking...");
								socketReader.close();
								exit = true;
							}
							else if (input.contains("full")){
								String [] array = input.split(" ");
								main.displayPanel.setCellOcupancy(Integer.parseInt(array[1]), Boolean.parseBoolean(array[2].toLowerCase()));
								main.writeToConsole("[INFO]: State of cell number " + Integer.parseInt(array[1]) + " has changed.");
							}
							System.out.println("Raw input log from Pi3: " + input);
						}
					}catch (IOException e){
						e.printStackTrace();
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean writeToPi1(String message){
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(pi1.getOutputStream()));
			writer.print(message);
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean writeToPi2(String message){
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(pi2.getOutputStream()));
			writer.print(message);
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
