#
#  This program explains POSIX Message Queue APIs
#
#  http://www.linuxpedia.org/index.php?title=Linux_System_Programming_:_POSIX_Message_Queue
#
#  Copyright (C) 2012  LinuxPedia.org (anoojgopi@linuxpedia.org)
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Lesser General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

all:
	gcc -Wall mq_send.c -o send.out -lrt
	gcc -Wall mq_receive_1.c -o receive_1.out -lrt
	gcc -Wall mq_receive_2.c -o receive_2.out -lrt
clean:
	rm *.out
