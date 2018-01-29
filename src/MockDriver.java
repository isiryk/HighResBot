import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MockDriver {
	
	static boolean connection = false;
	static boolean initialize = false;
	static boolean abort = false;
	
	static String IPAddress = "127.0.0.1";
	
	static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	
	static Scanner operation = new Scanner(System.in);
	
	static String[] name = new String[2];
	static String[] value = new String[2];
	
	public static void main(String argv[]) throws Exception {
		Scanner optionSelector = new Scanner(System.in);
		while(!abort){
			int option = userOption(optionSelector);
			if(option == 1){
				option1();
				clientSocket = new Socket(IPAddress, 1000);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
			} else if(option == 2){
				option2();
				
			} else if(option == 3){
				option3();
				
			} else if(option == 4){
				option4();
				
			} else {
				System.out.println("Invalid option selected, please restart the program");
			}
		}
		optionSelector.close();
		clientSocket.close();
		outToServer.close();
		inFromServer.close();
		operation.close();
	}
	
	public static int userOption(Scanner optionSelector) throws InterruptedException{
		int option = -1;
		boolean pass = false;
		while(!pass){
			  System.out.println("What would you like to do?" + "\n"
					  				+ "Enter the option number" + "\n"
					  				+ "1)Open Connection" + "\n"
					  				+ "2)Initialize" + "\n"
					  				+ "3)Execute Operation" + "\n"
					  				+ "4)Abort" + "\n");
			  option = optionSelector.nextInt();
			 // Thread.sleep(1000);
			  if(option == 1){
				  pass = true;
				  connection = true;
			  } else if(option == 2 && connection){
				  pass = true;
				  initialize = true;
			  } else if(option == 3 && connection && initialize){
				  pass = true;
			  } else if(option == 4){
				  pass = true;
			  } else {
				  System.out.println("Please make sure you first open a connection"
				  		+ "\nthen you initialize"
				  		+ "\nand only then begin executing commands"
				  		+ "\nif you are stuck, please abort and start over \n");
			  }
		}
		return option;
	}
	
	public static void option1(){
		System.out.println("Defaulting to 127.0.0.1");
	}
	
	public static void option2() throws IOException{
		String response;
		outToServer.writeBytes("initialize" + "\n");
		response = inFromServer.readLine();
		System.out.println(response + "\n");
	}
	
	public static void option3() throws IOException{
		String response;
		System.out.println("Please enter what operation you want to do: \n");
		String op = operation.nextLine().toLowerCase();
		System.out.println(op);
		if(!op.equals("pick") || !op.equals("place") || 
				!op.equals("transfer")){
			System.out.println("Invalid command: Please choose either \n Place \n Transfer \n Pick");
//			return;
		}
		populate(op);
		System.out.println(name[0] + name[1]);
		System.out.println(value[0] + name[1]);
		outToServer.writeBytes(op + "\n");
		for(int j = 0; j < name.length; j++){
			outToServer.writeBytes(name[j] + "\n");
		}
		for(int j = 0; j < value.length; j++){
			outToServer.writeBytes(value[j] + "\n");
		}
		response = inFromServer.readLine();
		System.out.println(response + "\n");
	}
	
	public static void populate(String op){
		System.out.println("Please enter the source name");
		name[0] = operation.nextLine();
		System.out.println("Please enter the source value");
		value[0] = operation.nextLine();
		if(op.toLowerCase().equals("transfer")){
			System.out.println("Please enter the destination name");
			name[1] = operation.nextLine();
			System.out.println("Please enter the destination value");
		} else {
			name[1] = "";
			value[1] = "";
		}
		
	}
	
	public static void option4(){
		abort = true;
	}
}
