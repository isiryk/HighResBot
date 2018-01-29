import java.util.*;
import java.io.*;
import java.net.*;

public class MockAPIServer {

	//process ID of the current element
	static int processID;
	//checks if the client already initialized
	static boolean initialized = false;
	//this is the response message
	static String response;
	
	/*
	 * This is a mini database I made. I am assuming the names are various
	 * locations in the laboratory and the values would be like item #'s or barcode values.
	 * 		This posed a problem for my implementation as I was not sure how the user
	 * 		will tell the server where to place the item, for the sake of pseudo, I just
	 * 		made it a random location in the list :)
	 */
	static String[] names = {"apollo","","","cosmos"
		,"","","","kepler","orion","pulsar"};
	static String[] values = {"13","","","45","","","","7","4","1"};
	
	//These are just the variables i use to keep track of the values the user sent over
	static String source;
	static String sourceVal;
	static String destination;
	static String destinationVal;
	
	//If the user has not initialized yet, there is no process, therefore id = -1
	static String id = "-1";
	
	/*
	 * This is the main functionality of the server. My thoughts on this were simple.
	 * The user will send over 3 pieces of information, that information will all be in
	 * String form (because of how i sent over the arrays). The first 2 values will represent
	 * The first two values in the names array. The second two will represent the first 2 values 
	 * in the values array. In most cases, the second value of each array should be empty
	 * but just in case i decided that if the command is not "transfer", the second value
	 * mirrors the first.
	 */
	public static void main(String []args) throws Exception{
		//Open Socket, this allows the server to accept connections
		ServerSocket mockRobotSocket = new ServerSocket(1000);
		//Until I break out of the loop, the server will keep running
		while(true){
			Socket mockRobotConnection = mockRobotSocket.accept();
			//Information from the client
			BufferedReader inFromClient = new BufferedReader
					(new InputStreamReader(mockRobotConnection.getInputStream()));
			//Response buffer
			DataOutputStream outToClient = new DataOutputStream(
					mockRobotConnection.getOutputStream());
			//First value received, every value will always be lowercase in my code
			String operation = inFromClient.readLine().toLowerCase();
			//Trying to figure out what it is
			if(operation.equals("initialize")){
				//initialize just sets it to "home" state and gives a PID
				id = Integer.toString(home());
				response = "Initialization Successful: Your PID is: " +
						id + "\n";
				outToClient.writeBytes(response);
				initialized = true;
			} else if(operation.equals("abort")) {
				//abort shuts the connection down
				response = "Aborting process: " + id + "\n";
				outToClient.writeBytes(response);
				draw(outToClient);
				mockRobotConnection.close();
				break;
			}
			else if (operation.equals("transfer") && initialized == true){
				//if its a transfer, i need all of the values saved
				source = inFromClient.readLine().toLowerCase();
				destination = inFromClient.readLine().toLowerCase();
				sourceVal = inFromClient.readLine().toLowerCase();
				destinationVal = inFromClient.readLine().toLowerCase();
				transfer();
				outToClient.writeBytes(response);
				draw(outToClient);			
			} else if (initialized == true) {
				//if its not transfer, it must be either pick or place, either way
				//since both
				source = inFromClient.readLine().toLowerCase();
				destination = inFromClient.readLine().toLowerCase();
				sourceVal = inFromClient.readLine().toLowerCase();	
				destinationVal = inFromClient.readLine().toLowerCase();
				if(operation.equals("pick")){
					pick();
					outToClient.writeBytes(response);
					draw(outToClient);
				} else {
					place();
					outToClient.writeBytes(response);
					draw(outToClient);
				}
			} else {
				response = "Server Error: Please try again later" + "\n";
			}
		}
	}
	
	/*
	 * Random PID is generated from 0-10000000
	 */
	public static int home(){
		processID = (int)(Math.random()*10000000);
		return processID;
	}
	
	/*
	 * This is just an aide since I do not have an actual UI to work with
	 */
	public static void draw(DataOutputStream toClient) throws Exception{
		String visual = "";
		for(int j = 0; j < names.length; j++){
			visual = visual + " " + names[j];
		}
		toClient.writeBytes(visual + "/n");
	}
	
	/*
	 * Look to see if the item needed to pick up exists.
	 * If it doesnt, there is an error, we tell the user where it came from
	 * If it does, its successful, we already deleted the value (picked it up)
	 * and are waiting for further instructions so we are successful
	 */
	public static int pick(){
		boolean found = false;
		for(int j = 0; j < names.length; j++){
			if(names[j].equals(source)){
				names[j] = "";
				values[j] = "";
				found = true;
			}
		}
		if(found == false){
			response = "Error: could not find the item" + source + "\n";
		} else {
			response = "Success: " + id + " has been picked up" + "\n";
		}
		return processID;
	}
	
	/*
	 * First we figure out what slot to put it into (Since I didnt see how the user
	 * would specify it, for now I kept it at a random number in the array). We then
	 * check to see if we picked something up already, if we didnt, we return an error.
	 * If we did pick something up, we check that the slot doesnt have anything placed there
	 * already, if there is, we return an error, if not, we place it, save the values to the
	 * new slot, and return a success message.
	 */
	public static int place(){
		int slot = (int)Math.random() * 10;
		if(!source.equals("") && !sourceVal.equals("")){
			if(names[slot].equals("")){
				names[slot] = source;
				values[slot] = sourceVal;
				response = "Success: " + id + " has been placed successfully in slot " + 
				slot + "\n";
			} else {
				response = "Error: Something else is in the destination location" + "\n";
			}
		} else {
			response = "Error: Nothing has been picked up" + "\n";
		}
		return processID;
	}
	
	/*
	 * This just performs the above 2 operations in succession
	 */
	public static int transfer(){
		pick();
		place();
		return processID;
	}
	
	public static int status(){
		response = "status is currently under construction";
		return processID;
	}
	
}