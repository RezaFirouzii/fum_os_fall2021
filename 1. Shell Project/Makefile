# Linux C Shell Makefile

CC = gcc
CFLAGS  = -g -Wall
OBJ = shell.c

all: packages shell

packages:
	sudo apt-get install libreadline-dev

shell: $(OBJ)
	$(CC) $(CFLAGS) $(OBJ) -o shell -lreadline