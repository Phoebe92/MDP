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

char* AR_IP_DATA ="30|30|30|20";
char IP_AR_DATA[100]="01w";
 


char* formatAndroidData()
{
   char tmp[100];
   char buf[100];
   char tmp2[100];	
   char *token;
   char *tmptoken;
   char *retptr;
   int sensor,i; 
      
   strcpy(tmp,AR_IP_DATA);
   token = strtok(tmp, "|");
   sensor=atoi(token);
   sensor+=10;
   sprintf(buf, "%d",sensor);
   tmptoken = buf;
   strcat(tmp2,tmptoken);
   strcat(tmp2,"|");		 

   /* walk through other tokens */
   while( token != NULL ) 
   {
      token = strtok(NULL, "|");
	if(token!=NULL)
	{
        sensor=atoi(token);
        sensor+=10;
	 sprintf(buf, "%d",sensor);
	 tmptoken = buf;
	 strcat(tmp2,tmptoken);
        strcat(tmp2,"|");		 
	}
   }
	// cat both str tgt.
	
	strcat(tmp2,IP_AR_DATA);
	for(i=0;i<strlen(tmp2);i++)
	{
		tmp2[i] = tmp2[i+1];
	}
	retptr = tmp2;
	return retptr ;
}

int main()
{
   char *cat;
   char rslt[100];
   cat = formatAndroidData();
   strcpy(rslt,cat);
   printf("%s",rslt);
}