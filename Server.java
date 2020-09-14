/**
 * Concurrent Socket Server
 * @version 1.3
 * @since   2019-30-11
 */
import java.io.*;
import java.net.*;
/**
 * A concurrent (multi-threaded) server that accepts requests from clients.
 * The server and client programs connect to the same network address and port
 * The (multi-threaded) server handles client requests concurrently. 
 * More than one client can be serviced at any one time.
 * When running program on specified Server, use the following IP, and port:
 * Server Host address	 
 * IP Address	
 * Port # (RDP)	      
 * Port # (comms)	 
 * @author Jose Daniel Oropeza 
 */
public class Server {
	private ServerSocket serverSocket; 
	static private int initialPort = 3500;
	/**
	 * Main method
	 * @param args
	 * @throws UnknownHostException 
	 * */
	public static void main(String[] args) throws UnknownHostException {	
        Server server=new Server(); // Instantiate the server
        server.start(initialPort); // Run the server, clean up activities performed inside the threads
    }
	
	/*
	 * Starts the Server at the specified port. To service requests concurrently this method listens for connections and services them using the clientHandler.
	 * @param int port The port number the server services requests at.
	 */
    public void start(int port) {   
		try { 
			serverSocket = new ServerSocket(port); // Create the Server socket
			System.out.println("Server is listening for connection requests from clients at port 3500");
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
        while (true) {   	       
        	try {
				new ClientHandler(serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			} 	     	  
		}
    }

	/**
	 * Blueprint for the work each server client handler thread does. The client handler keeps listening for requests inside a while loop. A command is read from the client and 
	 * the command is ran of the Server side. An output must be written to the client, as we are interested in clocking the Client-Server turn around times.
	 */
	public class ClientHandler extends Thread {
	        private Socket clientSocket;
	        private String cmd; // command to be ran by the server thread
	        public String output = null;  // printing this is in development to assures that commands are being ran correctly on the server.  
	        private BufferedReader in; // for reading data from the client  
	        private PrintWriter out; // for sending data to the client
	        
	        public ClientHandler(Socket clientSocket) {  	
		        this.clientSocket = clientSocket;
		               
	        }
	        
	        /*
	         * Runs ClientHadler Threads
	         */
	        public void run() {
	        	try {  
	    			out = new PrintWriter(clientSocket.getOutputStream(), true);
	    	        // out.println("Server Says: New client Thread Connected! Requests for this thread serviced through clientSocket address: " + clientSocket.getRemoteSocketAddress());
	    			// out.flush();
	    	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  		
	    	        cmd = in.readLine(); 		    	        
		    	    this.output = this.RunCommand(cmd); 	
		    	    out.println();
		    	    // System.out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");    	        
		    	    in.close();
	    	        out.close(); 
	    	        clientSocket.close(); // terminate connection 
	    	        
				} catch (IOException e) {
					e.printStackTrace();
				}  	
	       }
	        
	        /*
	         * Runs the specified Linux command on and returns the output
	         * @param String cmd The command requested by the client
	         * @return String output The output to be sent to the client
	         */
	        public String RunCommand(String cmd) {
	    		   String s = null;		
	    		   StringBuilder sb = new StringBuilder();
	    		   String outputA = null;
	    		   BufferedReader stdInput = null;
	    	      // BufferedReader stdError;  
	    	    try {   
	    	        Process p = Runtime.getRuntime().exec(cmd);       
	    	        stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));     
	    	        
	    	        
	    	        while ((s = stdInput.readLine()) != null) {
	    	            sb.append(s);	            
	    	            System.out.println(sb.toString());            
	    	        }
	    	        outputA = sb.toString();        
	    	    }
	    	    catch (IOException e) {
	    	        System.out.println("Command Error!");           
	    	    }	    
	    	    catch (IllegalArgumentException e)
	    	    {
	    	    	System.out.println("Exception! Empty Character Detected!");	    	
	    	    }  
	        	return outputA;
	    	}
      
    }
}
