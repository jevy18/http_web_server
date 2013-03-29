package project;

/* @author Jevoun
 * @version 1.1
 * @Date: March 28, 2013
 */

import java.net.Socket;
import java.net.ServerSocket;

public class WebServer{
	public static void main(String []args) throws Exception{
		
		//set the port number
		int portNumber = 6789;
		
		//Declare an instance of the Socket class
		Socket socket;
		
		//Creates a server socket, bound the specific port
		ServerSocket listenSocket;
		
		try{
			
			//Initialize the reference of ServerSocket reference
			listenSocket = new ServerSocket(portNumber);
			
			while (true){
				
				//listen for a connection to be made to this socket and accepts it
				socket = listenSocket.accept();
				//Construct an object to process the HTTP request message.
				HttpRequest request = new HttpRequest(socket);
				//Create a new thread to process the request.
				Thread thread = new Thread(request);
				//Start the thread.
				thread.start();			
			}//end of while loop
		}catch(Exception e){
			e.printStackTrace();//print the stack where all error messages can be traced
		}		
	}//end of main method.
}//end of WebServer class.