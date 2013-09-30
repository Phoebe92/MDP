package simulation;

import maze.*;
import java.io.*;
import java.net.*;


public class SimpleConHandler extends Thread
{

    private Socket clientSocket;
    private Client client;

    public SimpleConHandler(Socket clientSocket) {

    	this.clientSocket = clientSocket;
    	this.client = new Client();
    }

    @Override
	public void run() {

 BufferedInputStream in;
 BufferedOutputStream out;
 client.initialize();
 
 try {

     in = new BufferedInputStream(clientSocket.getInputStream());
     out = new BufferedOutputStream(clientSocket.getOutputStream());

 } catch (IOException e) {
     System.out.println(e.toString());
     return;
 }

 try {
	
	 byte[] msg = new byte[100];
	 byte[] omsg = new byte[10];
     int bytesRead = 0;
     int n;
     
     // valid code: reading data from client
     while(true)
    {	
    	
	     n = in.read(msg);
	     String s = new String(msg);
	     //s=s.substring(0,s.lastIndexOf("|"));
	     System.out.println("Received from arduino:"+s);
	    
	     //int i=0; 
	     //while(i<1000){i++;}
	     
	     
	     // input algorithm here....
	     //dowhat = instructions(obstacle);
	     String instr = client.sendInstruction(s);
	     instr += "\r\n";
	     byte[] bytes = instr.getBytes();
	     
	     if(instr.equals("disconnect\r\n")){
	    	 System.out.println("disconnected");
	    	 break;
	     }
	     
	     msg[0]='u';
	     msg[1]='\r';   //for simulator use 
	     msg[2]='\n';
	     //msg[1]='\0';
	     for(int x=3;x<msg.length;x++)
	     {
	    	 msg[x]=' ';
	     }
	     out.write(bytes);
	     out.flush();
	     String d = new String(msg);
	     System.out.println("Sent:"+d);
	     
	     Thread.sleep(500);
     }
     /* while((n = in.read(msg, bytesRead, 256)) != -1) {
  bytesRead += n;
  if (bytesRead == 4096) {
      break;
  }
  if (in.available() == 0) {
      break;
  }
     }*/


 } catch(IOException | InterruptedException e1) {
     System.out.println(e1.toString());
     try {
         out.close();
         in.close();
         clientSocket.close();
     } catch ( IOException e2 ) {;}
 }

 try {
     out.close();
     in.close();
     clientSocket.close();
 } catch ( IOException e2 ) {;}
    }
}