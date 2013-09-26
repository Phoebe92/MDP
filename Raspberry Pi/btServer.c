#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
const int BUF_SIZE = 1024;
void writeData(int client, char *data)
{
	write(client, data, strlen(data));
	//printf("Message Sent!");
}

char* readData(int client, char* buf, int SIZE)
{
    int bytes_read;
	//read bytes_readdata from the client
	
    bytes_read = read(client, buf, SIZE);

    //if( bytes_read > 0 ) {
        printf("received [%s]\n", buf);
    //}
	return buf;
} 


int main(void)
{
    struct sockaddr_rc loc_addr = { 0 }, rem_addr = { 0 };
    char buf[BUF_SIZE];
    int s, client, bytes_read;
    socklen_t opt = sizeof(rem_addr);
    char* rd="data";
    // allocate socket
    s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    // bind socket to port 1 of the first available 
    // local bluetooth adapter
    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = (uint8_t) 15;
    bind(s, (struct sockaddr *)&loc_addr, sizeof(loc_addr));
    
	// put socket into listening mode
	listen(s, 1);
	// accept one connection
	client = accept(s, (struct sockaddr *)&rem_addr, &opt);
	ba2str( &rem_addr.rc_bdaddr, buf );
    	fprintf(stderr, "accepted connection from %s\n", buf);

	while(1){
 
		
		memset(buf, 0, sizeof(buf));
    		rd = readData(client,buf,BUF_SIZE);
		writeData(client, "abcdefg");
		
		// using BT term tool, disconnect sends "", change accordingly during integration
		if (strcasecmp(rd, "") == 0)
		{break;}	
	}
	
   // close connection
    	close(client);
	close(s);
		
    return 0;
}
