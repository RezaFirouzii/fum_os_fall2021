/*
* @author: Reza Firouzi
* Stdn ID: 9812762463
* Project: Linux Shell with C
*/

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <readline/readline.h>
#include <readline/history.h>


int fd;
int flag;
int status;
int bckgrnd_flag;
pid_t pid;
char *history_file;
char *input_buffer;
char cwd[1024];
char *cmd_exec[512];
static char *args[512];
static char prompt[512];
int flag_without_pipe;
int output_redirection;
int input_redirection;
char *input_redirection_file;
char *output_redirection_file;


void reset_variables(); 
void print_history ();
void greeting();
void shell_prompt(); 
void sigintHandler(int);
void change_directory();
char *skip_whitespace(char* );
char *skip_double_quote(char* );
void tokenize_by_pipe();
void tokenize_by_space (char *);
void tokenize_redirect_input(char *);
void tokenize_redirect_output(char *);
static int execute_special_commands(char *, int, int, int);
static int execute_command(int, int, int, char *);
void check_for_bckgrnd();



/* initializes the global variables */
void reset_variables() {

	fd = 0;
	flag = 0;
	flag_without_pipe = 0;
	output_redirection = 0;
	input_redirection = 0;
	cwd[0] = '\0';
	prompt[0] = '\0';
 	pid = 0;

	// change this based on your own user system path
	history_file = "/home/rezafirouzi/Dev/os-shell-project/history.txt";
}


/* prints the commands history when "history" command is entered */
void print_history() {
  	
  	register HIST_ENTRY **list;
    register int i;

    list = history_list();
    if (list)
    	for (i = 0; list[i]; i++)
            printf ("%d: %s\n", i + history_base, list[i]->line);
    return;
}

  
/* create the shell prompt */
void shell_prompt() {

	if (getcwd(cwd, sizeof(cwd)) != NULL) {

		strcat(prompt, cwd);
		strcat(prompt, "$ ");
	} else 
		perror("Error in getting curent working directory: ");
	
	return;
}


/* handles the interrupt signals (Ctrl + C) */
void sigintHandler(int sig_num) {
	
    signal(SIGINT, sigintHandler);
    fflush(stdin);
    return;
}


/* This function is used to skip the white spaces in the input string */
char *skip_whitespace(char* str) {

	int i = 0, j = 0;
	char *temp;
	if (NULL == (temp = (char *) malloc(sizeof(str)*sizeof(char)))) {
		perror("Memory Error: ");
		return NULL;
	}

	while(str[i++]) {

		if (str[i-1] != ' ')
			temp[j++] = str[i-1];
	}
	temp[j] = '\0';
	return temp;
}


/* This function is used to skip the double quote characters (") in the input string */
char *skip_double_quote (char *str) {

	int i = 0, j = 0;
	char *temp;
	if (NULL == (temp = (char *) malloc(sizeof(str)*sizeof(char)))) {
		perror("Memory Error: ");
		return NULL;
	}

	while(str[i++]) {

		if (str[i-1] != '"')
			temp[j++] = str[i-1];
	}
	temp[j] = '\0';
	return temp;
}


/* tokenizing the input string based on white-space " " */
void tokenize_by_space(char *str) {

	int m = 1;

	args[0] = strtok(str, " ");
	while ((args[m] = strtok(NULL," ")) != NULL) m++;
	args[m] = NULL;
}


/* tokenizing the input string based on pipe |  */
void tokenize_by_pipe() {

	int i, n = 1, input = 0, first = 1;

	cmd_exec[0] = strtok(input_buffer, "|");
	while ((cmd_exec[n] = strtok(NULL, "|")) != NULL) n++;

	cmd_exec[n] = NULL;
	
	for (i = 0; i < n-1; i++) {

		input = execute_special_commands(cmd_exec[i], input, first, 0);	
		first = 0;
	} 

	input = execute_special_commands(cmd_exec[i], input, first, 1);
	return;
}


/* parse the input when only input redirection "<" is present */
void tokenize_redirect_input (char *cmd_exec) {

	char *val[128];
	char *new_cmd_exec, *s1;
	new_cmd_exec = strdup(cmd_exec);

	int m = 1;
	val[0] = strtok(new_cmd_exec, "<");
	while ((val[m] = strtok(NULL,"<")) != NULL) m++;

	s1 = strdup(val[1]);
	input_redirection_file = skip_whitespace(s1);

	tokenize_by_space (val[0]);
	return;
}


/* parse the input when only output redirection ">" is present */
void tokenize_redirect_output (char *cmd_exec) {

	char *val[128];
	char *new_cmd_exec, *s1;
	new_cmd_exec = strdup(cmd_exec);

	int m = 1;
	val[0] = strtok(new_cmd_exec, ">");
	while ((val[m] = strtok(NULL,">")) != NULL) m++;

	s1 = strdup(val[1]);
	output_redirection_file = skip_whitespace(s1);

	tokenize_by_space (val[0]);
	return;
}


/* change directory when "cd" command is executed */
void change_directory() {

	char *home_dir = "/home";

	if ((args[1]==NULL) || (!(strcmp(args[1], "~") && strcmp(args[1], "~/"))))
		chdir(home_dir);
	else if (chdir(args[1]) < 0)
		perror("No such file or directory: ");

}


/* execute the special commands */
static int execute_special_commands(char *cmd_exec, int input, int isfirst, int islast) {

	char *new_cmd_exec;

	new_cmd_exec = strdup(cmd_exec);

	tokenize_by_space(cmd_exec);
	check_for_bckgrnd();

	if (args[0] != NULL) {
		if (!strcmp(args[0], "exit") || !strcmp(args[0], "quit"))
			exit(0);

		if (!strcmp(args[0], "echo")) {

			cmd_exec = skip_double_quote(new_cmd_exec);
			tokenize_by_space(cmd_exec);
		}

		if (!strcmp("cd", args[0])) {

			change_directory();
			return 1;
		}

		if (!strcmp(args[0], "history")) {
			
			print_history();
			return 1;
		}
	}
	return (execute_command(input, isfirst, islast, new_cmd_exec));
}


/* create pipe and execute the commands using execvp */
static int execute_command(int input, int first, int last, char *cmd_exec) {

	int pipefd[2], ret, input_fd, output_fd;

	if ((ret = pipe(pipefd)) == -1) {
		perror("pipe error: ");
		return 1;
	}

	pid = fork();

	// child
	if (pid == 0) {

		if (first == 1 && last == 0 && input == 0) {
			
			dup2(pipefd[1], 1);
		}
		else if (first == 0 && last == 0 && input != 0) {

			dup2(input, 0);
			dup2(pipefd[1], 1);
		}
		else dup2(input, 0);
		

		if (strchr(cmd_exec, '<')) {

			input_redirection = 1;
			tokenize_redirect_input(cmd_exec);
		}
		else if (strchr(cmd_exec, '>')) {

			output_redirection = 1;
			tokenize_redirect_output(cmd_exec);
		}

		if (output_redirection) {

			if ((output_fd = creat(output_redirection_file, 0644)) < 0) {

				fprintf(stderr, "Failed to open %s for writing\n", output_redirection_file);
				return (EXIT_FAILURE);
			}
			dup2(output_fd, 1);
			close (output_fd);
			output_redirection = 0;
		}

		if (input_redirection) {

			if ((input_fd = open(input_redirection_file, O_RDONLY, 0)) < 0) {

				fprintf(stderr, "Failed to open %s for reading\n", input_redirection_file);
				return (EXIT_FAILURE);
			}
			dup2(input_fd, 0);
			close (input_fd);
			input_redirection = 0;
		}
		
		check_for_bckgrnd ();

		if (!strcmp(args[0], "echo")) {
			int i = 1;
			while (args[i] != NULL) 
				printf("%s ", args[i++]);
			printf("\n");
		}
		// executing any command using execvp and replacing args[0] with child
		else if (execvp(args[0], args) < 0) {
			fprintf(stderr, "%s: Command not found\n",args[0]);
		}
		exit(0);
	}

	else {

		if (bckgrnd_flag == 0)
			waitpid(pid,0,0);
	}

	if (last == 1)
		close(pipefd[0]);

	if (input != 0)
		close(input);

	close(pipefd[1]);
	return (pipefd[0]);
}


/* check if the process should run in foreground or background */
void check_for_bckgrnd () {

	int i = 0;
	bckgrnd_flag = 0;
	
	while (args[i] != NULL) {
		if (!strcmp(args[i], "&")) {
			bckgrnd_flag = 1;
			args[i] = NULL;
			break;
		}
		i++;
	}

}


void greeting() {
	printf("*******************************\n");
	printf("*                             *\n");
	printf("*       Reza Firouzi          *\n");
	printf("*                             *\n");
	printf("*       ------------          *\n");
	printf("*                             *\n");
	printf("*        Linux Shell          *\n");
	printf("*                             *\n");
	printf("*******************************\n\n");
}


/* main function */
int main() {

	int status;
	system("clear");
	greeting();
	reset_variables();
	signal(SIGINT, sigintHandler);
	
	// read history (if there is none, make one)
	if (read_history(history_file))
		write_history(history_file);


	do {

		reset_variables();
		shell_prompt();
		input_buffer = readline(prompt);


		if(strcmp(input_buffer,"\n")) {
			add_history(input_buffer);
			append_history(1, history_file);
		}

		if (!strcmp(input_buffer, "\n") || !strcmp(input_buffer,""))
			continue;

		if (!strncmp(input_buffer, "exit", 4) || !strncmp(input_buffer, "quit", 4)) {

			flag = 1;
			break;
		}

		tokenize_by_pipe();

		if (bckgrnd_flag == 0)
			waitpid(pid,&status,0);
		else
			status = 0;

	} while(!WIFEXITED(status) || !WIFSIGNALED(status));

	if (flag == 1) {

		printf("\nExiting Shell...\n");
		exit(0);
	}

	return 0;
}
