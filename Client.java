/**
 * Concurrent Socket Server
 * @version 1.2
 * @since   2019-12-2
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList; 
import java.util.Iterator;
import java.util.Scanner;

/**
 * A multi-threaded client capable of spawning numerous client sessions that connect to the server.
 * The server and client programs connect to the same network address and port
 * The client tansmits requests to the server on a specified network port
 * When running program on specified Client server, use the following IP, and port:
 * Host address	 
 * IP Address	          
 * Port # (RDP)	 111          
 * Port # (comms)	 1025-4998	
 * @author Jose Daniel Oropeza 
 */
public class Client { 
	static Socket[] socketArray = new Socket[1000]; // Support opening up to 50 connections
	static String ip, cmd; // value set by DisplayMenu() ; IP/hostname should be "localhost" when on local machine
	static int port, numClients; // value set by DisplayMenu()
	static long totSum = 0; // Sum of all turn around times for all threads
	static double avg = 0.0; // Average ToT for all connections
    static CopyOnWriteArrayList<String> l = new CopyOnWriteArrayList<String>(); 
    static Iterator<String> itr;
    static long sleepTime = 9001;
    

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {	
		Client client =new Client(); // Instantiate the a Client class object
		
        client.runInitialMenu(); // Collects the desired IP Address, port, and a command.
		client.startConnections(ip, port); // Starts a number of client connections  
       
		// Wait a specified amount of time so that all the threads can finish, then proceed w/ calculations
		try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		client.calculateResults();
		client.printResults();		
	}
	
	/**
	 * Creates a new socket for each of x amount of clients and services that client's requests on a different thread
	 * by calling start() on the ClientGenerator class
	 * @param ip  IPAddress
	 * @param port  The port number           
	 */
	public void startConnections(String ip, int port) {
		System.out.println("Server is listening for connection requests from clients at port 3500");
        for(int i=0;i<numClients;i++) {  
            try {      
     			socketArray[i]  = new Socket(ip, port);
     			int session = i + 1;  			
				new ClientGenerator(socketArray[i], session, cmd).start();
			} 
            catch (UnknownHostException e) {
				e.printStackTrace();
			} 
            catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
        
    /**
    * Calculates the total turn-around time and average turn-around time.        
    */
  	public void calculateResults() {
  		itr = l.iterator(); 
        while (itr.hasNext()) { 
            String s = (String)itr.next(); 
            try {
                long tot = Long.parseLong(s);
                totSum += tot;
             } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe.getMessage());
             }           
        }   
        double totalSum = ((Number)totSum).doubleValue();
        double numberClients = ((Number)numClients).doubleValue();
		avg = totalSum / numberClients;
    }

	/**
	* Blueprint for the work each connection thread does. Connection threads work to collect the time required for the client request to travel to the server, 
	* be processed by the server, and return to the client (Turn-around-time).
	*/
	public static class ClientGenerator extends Thread {
        private Socket clientSocket; 
	    private int session;
        private String cmd;
        private PrintWriter out;
        private BufferedReader in;
        private long start;  // The time when a client request is sent
        private long end; // The time after a client request travels to the server, the command is ran by the server, and the client receives a response. 
        private long total; 
        	        
        public ClientGenerator(Socket clientSocket, int session, String cmd) {  	
	        this.clientSocket = clientSocket;
	        this.session = session;
	        this.cmd = cmd;	        
        }
        
        /*
         * Runs ClientGenerator Threads
         */
        public void run() {
        	// Time sensitive stuff inside this try-catch
	        try {	
	            out = new PrintWriter(clientSocket.getOutputStream(), true); // The output stream of the client is connected to the input stream of the server 
	            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // The input stream of the client is connected to the output stream of the server 
 	
				out.println(cmd); // Write the desired command string to the server socket
				start = System.currentTimeMillis();
				in.readLine(); // Wait for a response
				end = System.currentTimeMillis();
				  total = (end - start); // Turn-around Time (elapsed time) for the client request
		            l.add(""+total+""); // add to Array list, we sum these values later on.
				// Conserve resources
	            in.close();
	            out.close();
	            clientSocket.close();           
	         
	        }         
	        catch (IOException ex) {
	            System.out.println("Server exception: " + ex.getMessage());
	            ex.printStackTrace();
	        } 
	        return; 
        }           
	}
	
    /**
	 * Requests the network address and port to which to connect, the operation to request, 
	 * and how many client requests to generate (1, 5, 10, 15, 20  and 100).        
	 */
    public void runInitialMenu() {
    	Scanner userInput = new Scanner(System.in); 		
        System.out.println("Enter a network address/hostname:");
		ip = userInput.next(); 				
		System.out.println("Enter a port number:");
		port = Integer.parseInt(userInput.next());
        
		// Select Request type - Menu
		System.out.println("Select type of client request by entering a number between 1 and 6:");
		System.out.println("Options:");
		System.out.println("1. Date and Time");
		System.out.println("2. Uptime");
		System.out.println("3. Memory Use");
		System.out.println("4. Netstat");
		System.out.println("5. Current Users");
		System.out.println("6. Running Processes");
		int requestType = Integer.parseInt(userInput.next());
		switch (requestType) {
	    case 1:
	      System.out.println("Option 1 (Date and Time) was selected");
	      cmd = "date";
	      break;
	    case 2:
	    	System.out.println("Option 2 (Uptime) was selected");
	    	cmd = "uptime";
	      break;
	    case 3:
	    	System.out.println("Option 3 (Memory Use) was selected");
	    	cmd = "free";
	      break;
	    case 4:
	    	System.out.println("Option 4 (Netstat) was selected");  
		      cmd = "netstat";
		      sleepTime = 30000;
		      break;
	    case 5:
	    	System.out.println("Option 5 (Current Users) was selected");
	          cmd ="who";
		      break;
	    case 6:
	    	System.out.println("Option 6 (Running Processes) was selected");
	    	cmd = "ps";
		      break;
	    default:
	      System.out.println("Invalid selection");
	      break;
	    }
			
		System.out.println("Enter desired number of client requests to generate (1, 5, 10, 15, 20, 25, or 100)"); // Select Number of Requests - Menu
		numClients = Integer.parseInt(userInput.next());
	    switch (numClients) {
	    case 1:
	      System.out.println("Spawning 1 client request");
	      break;
	    case 5:
	    	System.out.println("Spawning 5 client requests");
	      break;
	    case 10:
	    	System.out.println("Spawning 10 client requests");
	      break;
	    case 15:
	    	System.out.println("Spawning 15 client requests");
		      break;
	    case 20:
	    	System.out.println("Spawning 20 client requests");
		      break;
	    case 25:
	    	System.out.println("Spawning 25 client requests");
		      break;
	    case 100:
	    	System.out.println("Spawning 100 client requests");
		      break;
	    default:
	      System.out.println("Invalid selection");
	      break; 
	    }
	    
	    if ((numClients == 100) && (cmd=="netstat")) {
	    	 sleepTime = 200000;
	    }
	    userInput.close(); // conserve resources
	    return; //done
    } 
    
	/**
	 * Prints the summation of all turn-around times, and more importantly, the average turn-around time.
	 */
	public void printResults() {
        System.out.println("Array storing the turn-around time (elapsed time) for each client request:");
		System.out.println(l);
		System.out.print("\nTotal turn-around time for " + numClients + " connections: " + totSum + " m/s \n");
		System.out.println("Average turn-around time for " + numClients + " connections: " + avg + " m/s \n");		
		return;
	}	  
} 
