// http://stackoverflow.com/questions/4727102/how-to-run-server-client-code-using-socket-programming-in-java-eclipse
package simulation;

import java.io.*;
import java.net.*;

public class SocketConnection {

    public static void main(String[] args) throws IOException {

 boolean listening = true;
 ServerSocket serverSocket = null;
 try {
     serverSocket = new ServerSocket(4441);
 } catch (IOException e) {
     System.err.println("Could not listen on port: 4446.");
  System.exit(1);
 }

// int counter = 0;
 while(listening) {
	 
     Socket clientSocket = serverSocket.accept();
     
     //if(counter == 0){
    	 (new SimpleConHandler(clientSocket)).start();
    	 //counter++;
    // }else{
    	// counter = 0;
    // }
     
 }

 serverSocket.close();
    }
}