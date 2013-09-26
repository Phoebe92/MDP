#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <arpa/inet.h> 

#include <stdint.h>
#include <fcntl.h>
#include <termios.h>
#include <sys/ioctl.h>

void writeData(int client, char *data);
char* readData(int client, char* buf, int SIZE);
int BTServer(void);
int IPClient();
void writeIPData(int sockfd, char* data);
char* readIPData(int sockfd, char* buf);
void writeARData(int client, char *data);
char* readARData(int fd, char* buf);

// Parameters and protocol used for BT 
char* DISCONNECT = "";
char* ALGORITHM_MODE = "algorithm";
char* ANDROID_MODE = "android";
char AN_AR_DATA[100];

// Parameters and protocol used for IP
char* IPADDRESS = "192.168.20.21";
int PORTNO = 4447;
char* TRIGGER = "START";
char* END = "END";
char* AR_IP_DATA ="";
char IP_AR_DATA[100];

// 0 - android mode
// 1 - algorithm mode
int mode = 0;

// 0 - not opened.
// 1 - android sending
// 2 - algo sending
int ARmode = 0;



// thread for WIFI communication, working as a IP Client...
void *threadIP(void *arg)
{
	while(1)
	{
		if(mode==1)
		{
			IPClient();	
		}
	}	
/*
	char *str;
	int i = 0;

	str=(char*)arg;

	while(1)
	{
		usleep(1);
		printf("threadFunc says: %d\n",i);
		++i;
	}

	return NULL;
*/

}

void *threadAR(void *arg)
{
	while(1)
	{
		if(ARmode==1 || ARmode==2)
		{
			ARClient();	
		}
	}	
}

int main(void)
{
	pthread_t IPth, ARth;	// this is our thread identifier
	int i = 0;
	pthread_create(&IPth,NULL,threadIP,"foo");
	pthread_create(&ARth,NULL,threadAR,"foo");
	
	while(1)
	{
		BTServer();
		printf("main is running...\n");
	}
	
	/*
	while(i < 100)
	{
		usleep(1);
		printf("main is running...\n");
		++i;
	}*/

	printf("main waiting for thread to terminate...\n");
	pthread_join(IPth,NULL);

	return 0;
}



void writeIPData(int sockfd, char* data)
{
	 write(sockfd, data, strlen(data)); 
}
	
char* readIPData(int sockfd, char* buf)
{
    int n=read(sockfd, buf, strlen(buf));
    printf("WIFI received [%s]\n", buf);
    return buf;
} 

// WIFI Communication Segment
int IPClient()
{
    int sockfd = 0, n = 0;
    char recvBuff[100]= "temp text";
    char sendBuff[1024]="";
    char tmpChk;
    time_t ticks; 
    struct sockaddr_in serv_addr; 
    char* args[1];
    char* IPrd="data";
    int port = PORTNO ;
    args[0] = IPADDRESS ;

	// address and port of server. change accordingly
    memset(recvBuff, '0',sizeof(recvBuff));
    if((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        printf("\n Error : Could not create socket \n");
        return 1;
    } 

    memset(&serv_addr, '0', sizeof(serv_addr)); 

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port); 
 
    if(inet_pton(AF_INET, args[0], &serv_addr.sin_addr)<=0)
    {
        printf("\n inet_pton error occured\n");
        return 1;
    } 

    if( connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
       printf("\n Error : Connect Failed \n");
       return 1;
    } 

    writeIPData(sockfd,TRIGGER);

    while(1)
    {
  		// chk for mode, if mode = 1 then proceed
		if(mode==1)
		{
			// reading instruction from algorithm..
			readIPData(sockfd, recvBuff);
			IPrd = recvBuff;
			
			// forward instruction to arduino..
			//strcpy(, rd);		// copy data to char array
			strcpy(IP_AR_DATA,IPrd );
			ARmode = 2;
			
			while(ARmode!=0); 			// waiting for data to be written by arduino..
			
			// take instruction from arduino and write to algorithm..
		       writeIPData(sockfd,AR_IP_DATA);
			memset(recvBuff, '0',sizeof(recvBuff));

			// to deal with STOP
			// PC sends STOP(knowing from its own map)
			// FWD to ARDUINO, ARDUINO adjusts position, reply to PC
			// PC receive and loop, waiting for the "STOP" command from android
			// "STOP" command from android is suppose to trigger shortest path.
			// at this segment, change a global variable once stop from PC is received
			// waits for android to receive command and modify that variable
			// once modified, loop gets break out of, write to pc
			// pc read and gives instruction for shortest path
			// continue reading instruction from algo
		}
		else
		{
			usleep(2000*100);
			//close connection
			writeIPData(sockfd,END);
			close(sockfd);
			break;
		}			      	
    }
    return 0;
}



// Bluetooth Communication Segment
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
        printf("BT received [%s]\n", buf);
    //}
	return buf;
} 



int BTServer(void)
{
    struct sockaddr_rc loc_addr = { 0 }, rem_addr = { 0 };
    char buf[BUF_SIZE];
    int s, client, bytes_read;
    socklen_t opt = sizeof(rem_addr);
    char* BTrd="data";

    // allocate socket
    s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    // bind socket to port 1 of the first available 
    // local bluetooth adapter
    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = (uint8_t) 15;
    bind(s, (struct sockaddr *)&loc_addr, sizeof(loc_addr));

    // put socket into listening mode
    listen(s, 10);

    // accept one connection
    client = accept(s, (struct sockaddr *)&rem_addr, &opt);
	
    ba2str( &rem_addr.rc_bdaddr, buf );
    fprintf(stderr, "accepted connection from %s\n", buf);
	
    while(1){
	memset(buf, 0, sizeof(buf));

    	BTrd= readData(client,buf,BUF_SIZE);
	
	// using BT term tool, disconnect sends "", change accordingly during integration
	if (strcasecmp(BTrd, DISCONNECT ) == 0)
	{
		mode =0;
		break;
	}
	else
	{
		if(strcasecmp(BTrd, "ARDUINO") == 0) 	// temperory, just for demo, remove after demo.
		{
			writeData(client,AR_IP_DATA);
		}
		else if(strcasecmp(BTrd, ALGORITHM_MODE) == 0)
		{
			mode = 1;
		}
		else if(strcasecmp(BTrd, ANDROID_MODE) == 0) 	
		{
			mode = 0;
		}
		else if(mode==0 && ARmode==0)	// if under android mode, receive instructions
		{								// and fwd to arduino
			//set protocol for movement. example U for forward, D for backward..
			strcpy(AN_AR_DATA,BTrd);		// copy data to char array
			ARmode =1;
		}
		 			
	}
	
	}
	
    // close connection
    close(client);
    close(s);
    return 0;
}

int ARClient()
{
	int fd, n, i;
  char buf[1024] = "temp text";
  struct termios toptions;

  /* open serial port */
  fd = open("/dev/ttyACM0", O_RDWR | O_NOCTTY);
  printf("fd opened as %i\n", fd);
  
  /* wait for the Arduino to reboot */
  usleep(3500000);
 // usleep(350000);
  
  /* get current serial port settings */
  tcgetattr(fd, &toptions);
  /* set 9600 baud both ways */
  cfsetispeed(&toptions, B9600);
  cfsetospeed(&toptions, B9600);
  /* 8 bits, no parity, no stop bits */
  toptions.c_cflag &= ~PARENB;
  toptions.c_cflag &= ~CSTOPB;
  toptions.c_cflag &= ~CSIZE;
  toptions.c_cflag |= CS8;
  /* Canonical mode */
toptions.c_cflag     &=  ~CRTSCTS;       // no flow control
toptions.c_cc[VMIN]      =   1;                  // read doesn't block
toptions.c_cc[VTIME]     =   5;                  // 0.5 seconds read timeout
toptions.c_cflag     |=  CREAD | CLOCAL;     // turn on READ & ignore ctrl lines

/* Make raw */
cfmakeraw(&toptions);

/* Flush Port, then applies attributes */

toptions.c_oflag &= ~OPOST;
tcflush( fd, TCIFLUSH );
tcsetattr(fd, TCSANOW, &toptions);
  
  while(1)
  {
	if(ARmode==1)
	{
		writeARData(fd,AN_AR_DATA);
		AR_IP_DATA = readARData(fd,buf);  // remove after demo(dun need to initialise arp_ip_data
		ARmode = 0;
	}
	else if(ARmode==2)
	{
		writeARData(fd,IP_AR_DATA);
		AR_IP_DATA = readARData(fd,buf);
		ARmode = 0;
	}
  }
  
return fd;
}

void writeARData(int client, char *data)
{
	char tmp[100];		
	strcpy(tmp,data);		// copy data to char array
	strcat(tmp,"\r");		// concatenate "\r" to end of char array
	write(client, tmp, strlen(tmp));
	usleep(2000*100); // must set time for sleep, otherwise the data sent could be not stable.
}
char* readARData(int fd, char* buf)
{
      int n =  read(fd, buf, strlen(buf));
	buf[n] = 0;
	if(n==1)
	{}
	else
    	{
		printf("ARDUINO received :%s\n", buf);
    	}
    return buf;
} 