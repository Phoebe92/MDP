
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>
#include <fcntl.h>
#include <termios.h>
#include <errno.h>
#include <sys/ioctl.h>

#define DEBUG 1
  
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
	//if(n==1)
	//{}
	//else
   // {
	printf("ARDUINO received :%s\n", buf);
   // }
    return buf;
} 

int main(int argc, char *argv[])
{
  int fd, n, i;
  char buf[64] = "temp text";
  struct termios toptions;

  /* open serial port */
  fd = open("/dev/ttyACM0", O_RDWR | O_NOCTTY);
  printf("fd opened as %i\n", fd);
  
  /* wait for the Arduino to reboot */
  usleep(3500000);
  
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

//write(fd, "SLEEP\r", 6);
//usleep(2000*100);
writeARData(fd,"SLEEP");

while(1){
  

	readARData(fd,buf);
	writeARData(fd,"BMW");

    /* Send byte to trigger Arduino to send string back */
   
      /* Receive string from Arduino */
 // n = read(fd, buf, 64);
      /* insert terminating zero in the string */
  //buf[n] = 0;
  //if(n==1)
  //{}
 // else
  //{
 // 	printf("%i bytes read, buffer contains: %s\n", n, buf);
 // }
//writeARData(fd, "BMW");
//usleep(2000*100); // must set time for sleep, otherwise the data sent could be not stable.

}

  if(DEBUG)
    {
      printf("Printing individual characters in buf as integers...\n\n");
      for(i=0; i<n; i++)
	{
	  printf("Byte %i:%i, ",i+1, (int)buf[i]);
	}
      printf("\n");
    }

  return 0;
}