/*
 *  This program explains POSIX Message Queue APIs
 *
 *  http://www.linuxpedia.org/index.php?title=Linux_System_Programming_:_POSIX_Message_Queue
 *
 *  Copyright (C) 2012  LinuxPedia.org (anoojgopi@linuxpedia.org)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

#include <stdio.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <mqueue.h>
 #include <time.h>

#define MQ_NAME1 "/mqconsumer1"
 #define MQ_NAME2 "/mqconsumer2"
#define MQ_MESSAGE_DATA "hello world"
#define MQ_MESSAGE_LENGTH 100
#define MQ_MESSAGE_PRIORITY 0

int main()
{

	FILE *ptr_file;
	char buf[1000];
	ptr_file =fopen("sorted_data.csv","r");
	if (!ptr_file)
	{
		perror(" Error is opening file ");
	}

	mqd_t mqd1;
	mqd_t mqd2;
	int ret;
	int messages_sent=0;

	/* Open the message queue. Message queue is already created from a different process */
	mqd1 = mq_open(MQ_NAME1, O_WRONLY);
	mqd2 = mq_open(MQ_NAME2, O_WRONLY);
	if( mqd1 != (mqd_t)-1 && mqd2 != (mqd_t)-1 )
	{
		printf(" Message Queue Opened\n");
		clock_t start_t, end_t;
		start_t = clock();	

		while (fgets(buf,1000, ptr_file)!=NULL && messages_sent<100000)
		{
			/* Sending messages to queue 1 */
			ret = mq_send(mqd1, buf, MQ_MESSAGE_LENGTH, MQ_MESSAGE_PRIORITY);
			if(ret)
				perror("Failed");

			ret = mq_send(mqd1, buf+100, MQ_MESSAGE_LENGTH, MQ_MESSAGE_PRIORITY);
			if(ret)
				perror("Failed");

			/* Sending messages to queue 2 */
			ret = mq_send(mqd2, buf, MQ_MESSAGE_LENGTH, MQ_MESSAGE_PRIORITY);
			if(ret)
				perror("Failed");

			ret = mq_send(mqd2, buf+100, MQ_MESSAGE_LENGTH, MQ_MESSAGE_PRIORITY);
			if(ret)
				perror("Failed");

			messages_sent++;
			printf(" Messages sent : %i\n", messages_sent);
		}

		end_t = clock();
    	double time_taken = (double)(end_t-start_t) / (double)CLOCKS_PER_SEC;
    	printf("Messages sent %i :: Time taken %f :: Throughput %f\n", messages_sent, time_taken, (double)messages_sent/time_taken);
		

		/* Close the message queue 1 */
		ret = mq_close(mqd1);
		if(ret)
			perror(" Message queue 1 close failed");
		else
			printf(" Message Queue 1 Closed\n");

		/* Close the message queue 2 */
		ret = mq_close(mqd2);
		if(ret)
			perror(" Message queue 2 close failed");
		else
			printf(" Message Queue 2 Closed\n");
	
	}
	else if ( mqd1 == (mqd_t)-1 )
	{
		perror(" Message queue 1 open failed ");
	}
	else
	{
		perror(" Message queue 2 open failed ");
	}

	fclose(ptr_file);

	return 0;
}
