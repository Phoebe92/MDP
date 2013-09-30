// http://stackoverflow.com/questions/4727102/how-to-run-server-client-code-using-socket-programming-in-java-eclipse
package simulation;

import java.io.*;
import java.net.*;

public class SocketConnection {

    public static void main(String[] args) throws IOException {

 boolean listening = true;
 ServerSocket serverSocket = null;
 try {
     serverSocket = new ServerSocket(4445);
 } catch (IOException e) {
     System.err.println("Could not listen on port: 4447.");
  System.exit(1);
 }

 while(listening) {
     Socket clientSocket = serverSocket.accept();
     (new SimpleConHandler(clientSocket)).start();
 }

 serverSocket.close();
    }
}