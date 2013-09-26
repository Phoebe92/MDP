#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <arpa/inet.h> 
#include <time.h> 


void writeIPData(int sockfd, char* data)
{
	 write(sockfd, data, strlen(data)); 
}
	

char* readIPData(int sockfd, char* buf)
{
    read(sockfd, buf, strlen(buf));
    printf("received [%s]\n", buf);
    return buf;
} 

int main(void)
{
    int sockfd = 0, n = 0;
    char recvBuff[100];
    char sendBuff[1024];
    char tmpChk;
    time_t ticks; 
    struct sockaddr_in serv_addr; 
    char* args[1];
    int port = 4446;
    args[0] ="192.168.1.21";
    char *rd;
/*
    if(argc != 2)
    {
        printf("\n Usage: %s <ip of server> \n",argv[0]);
        return 1;
    } 
*/
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

	
	while(1)
    {
	writeIPData(sockfd,"START");

        /*	sendBuff[0]='1';
		sendBuff[1]='7';
		sendBuff[2]='\r';
		sendBuff[3]='\n';
		sendBuff[4]='\0';
		ticks = time(NULL);
        // snprintf(sendBuff, sizeof(sendBuff), "%.24s\r\n", ctime(&ticks));
        write(sockfd, sendBuff, strlen(sendBuff)); 
	*/


	rd = readIPData(sockfd, recvBuff);
   	    //read(sockfd, recvBuff, strlen(recvBuff));
	    //snprintf(recvBuff, sizeof(recvBuff), "%.24s\r\n", ctime(&ticks));
	/*	int length = sizeof(recvBuff)/sizeof(int);
		int i=0; 
		for(i=0;i<length;i++)
		{	tmpChk = recvBuff[i];
			if(tmpChk=='\0')
			{ 
				break;
			}
			printf("%c",recvBuff[i]);
		}
	*/	  
       //printf("%s",rd);
	memset(recvBuff, '0',sizeof(recvBuff));
       // close(connfd);
       sleep(1);
	
    }
	

	/*
    while ( (n = read(sockfd, recvBuff, sizeof(recvBuff)-1)) > 0)
    {
        recvBuff[n] = 0;
        if(fputs(recvBuff, stdout) == EOF)
        {
            printf("\n Error : Fputs error\n");
        }
    } */

    if(n < 0)
    {
        printf("\n Read error \n");
    } 

    return 0;
}