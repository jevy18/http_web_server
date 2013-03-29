package project;

/* @author Jevoun
 * @version 1.1
 * @Date: March 28, 2013
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.StringTokenizer;

public final class HttpRequest implements Runnable{

	//Declare Instance variables
	final static String CRLF = "\r\n";
	
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception{
		//Initialize variable
		this.socket = socket;

	}//end of constructor


	//Implement the run() method of the Runnable interface.
	public void run(){
		try{
			processRequest(); //process the request sent
		}catch (Exception e){
			System.out.println("This error that you have been receiving is here: " + e);
		}
	}//end of the run method

	public void processRequest() throws Exception{
		//Get a reference to the socket's input and output streams.
		InputStreamReader is = new InputStreamReader(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		//Set up input stream filters
		BufferedReader br = new BufferedReader(is);

		//Gets the request line of the HTTP request
		String requestLine = br.readLine();

		//Display the request
		System.out.println();
		System.out.println(requestLine);
		
		//Get and display the header lines.
		String headerLine = null;
		while((headerLine= br.readLine()).length() != 0){
			System.out.println(headerLine);
		}
		
		//Reads the entire length of a text
		(headerLine = br.readLine()).length();

		//Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be get
		String fileName = tokens.nextToken();

		//Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;
		
		//Declare and Initialize local variables
		FileInputStream fis = null;
		boolean fileExists = true;

		try{
			//open the request file
			fis  = new FileInputStream(fileName);
			fileExists = true;
		}
		catch(FileNotFoundException fnfe){
			fileExists = false;
			fnfe.printStackTrace(); //print the stack where all error messages can be traced
		}

		//Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists){
			statusLine = "HTTP/1.0 200 " + "OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		}else{
			statusLine = "HTTP/1.0 404 " + "Not Found" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
		}

		//Send the status line.
		os.writeBytes(statusLine);
		//Send the content type line.
		os.writeBytes(contentTypeLine);
		//Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		//Send the entity body.
		if(fileExists){
			try{
				sendBytes(fis,os); //passes the references onto the sendBytes method define below
				fis.close(); //close the fileInputStream reference
			}catch(Exception e){
				//Print the error message onto terminal/console window
				System.err.println("An Error occured while trying to send the bytes: " + e);
			}
		}else{
			os.writeBytes(entityBody);
		}

		//Close streams and socket
		os.close();
		br.close();
		socket.close();

	}//end of the processRequest method.

	
	//The method contentType takes one string as parameter and returns a string
	private static String contentType(String fileName){
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")){
			return "text/html";
		}
		if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")){
			//get the jpeg file-type
			return "image/jpeg";
		}
		if(fileName.endsWith(".gif")){
			//get the gif file-type
			return "image/gif";
		}
		return "application/octet-stream";
	}// end of contentType method

	//The method sendBytes takes two objects namely: FileInputStream and OutputStream as parameters
	private static void sendBytes(FileInputStream fis,OutputStream os)throws Exception{
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		// Copy requested file into the socket's output stream.
		while((bytes = fis.read(buffer)) != -1 ){
			os.write(buffer, 0, bytes);
		}
	}//end of sendBytes method.
}// End of HttpRequest class