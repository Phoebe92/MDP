// http://stackoverflow.com/questions/4727102/how-to-run-server-client-code-using-socket-programming-in-java-eclipse
package simulation;

import java.io.*;
import java.net.*;

import maze.Client;

public class SocketConnection {

    public static void main(String[] args) throws IOException {

 boolean listening = true;
 ServerSocket serverSocket = null;
 try {
     serverSocket = new ServerSocket(4449);
 } catch (IOException e) {
     System.err.println("Could not listen on port: 4446.");
  System.exit(1);
 }

 int counter = 0;
 while(listening) {
	 
     Socket clientSocket = serverSocket.accept();
     (new SimpleConHandler(clientSocket, counter)).start();
     
     if(counter == 0){    	 
    	 counter++;
     }else{
    	 //System.out.println("from socket connection: " + Client.getShortestPath());    	 
    	 counter = 0;
     }
     
 }

 serverSocket.close();
    }
}