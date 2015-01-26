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

#define MQ_NAME "/mqconsumer2"
#define MQ_MESSAGE_MAX_LENGTH 200
#define MQ_MAX_NUM_OF_MESSAGES 10
#define MQ_MODE (S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH)

int main()
{
	mqd_t mqd;
	struct mq_attr attr;
	int ret;
	char msg_buff[MQ_MESSAGE_MAX_LENGTH];
	ssize_t msg_len;
	int messages_received=0;

	/* Form the queue attributes */
	attr.mq_flags = 0; /* i.e mq_send will be block if message queue is full */
	attr.mq_maxmsg = MQ_MAX_NUM_OF_MESSAGES;
	attr.mq_msgsize = MQ_MESSAGE_MAX_LENGTH;
	attr.mq_curmsgs = 0; /* mq_curmsgs is dont care */

	/* Create message queue */
	mqd = mq_open(MQ_NAME, O_RDONLY | O_CREAT, MQ_MODE, &attr);
	if( mqd != (mqd_t)-1 )
	{
		printf(" Message Queue 2 Opened\n");

		while(1) 
		{
			printf(" Receiving message .... \n");
			msg_len = mq_receive(mqd, msg_buff, MQ_MESSAGE_MAX_LENGTH, NULL);
			if(msg_len < 0)
			{
				perror("   Failed");
			}
			msg_len = mq_receive(mqd, msg_buff+100, MQ_MESSAGE_MAX_LENGTH, NULL);
			if(msg_len < 0)
			{
				perror("   Failed");
			}
			else
			{
				messages_received++;
				msg_buff[MQ_MESSAGE_MAX_LENGTH-1] = '\0';
				printf("Message number : %i\n", messages_received);
				//printf("%s \n", msg_buff);
				//printf(" Successfully received %d bytes\n", (int)msg_len + 100);
			}
		}
		
		/* Close the message queue */
		ret = mq_close(mqd);
		if(ret)
			perror(" Message queue 2 close failed");
		else
			printf(" Message Queue 2 Closed\n");
	
		ret = mq_unlink(MQ_NAME);
		if(ret)
			perror(" Message queue 2 unlink failed");
		else
			printf(" Message Queue 2 unlinked\n");
	}
	else
	{
		perror(" Message queue 2 open failed ");
	}

	return 0;
}