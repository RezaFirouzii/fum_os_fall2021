
# Linux Shell with C
Linux Shell created with C using system calls (fork, exec, dup, pipe) and
OS signal handling.
## Installation

1. clone the repository

2. Change history_file var in shell.c to your fav path (history file path)
3. 
```bash
  make
```
4. Enter your password in order to install dependencies
5. 
```bash
  ./shell
```

## Documentation
In this project we use readline-dev unix library specifically designed for handling shell commands.

First we create a history file using history_file variable declaring file path, if
there is not one already to read previous history from.

Using an infinite loop our shell each time runs a function called shell_prompt in order to show the current working directory and expect the input_buffer.

Each time by reading the input_buffer, first check if there exists a command and ENTER is not pressed lonely, then add the command to history.

Then:
    
    1. Check for pipe tokenization (spliting command by "|")
    2. Save each command after split in the args array (char *args[])
    3. For each command, pass it to execute_special_command()
    4. If command is special (cd, echo, exit, quit, history):
            call the proper handling function (e.g change_directory() for cd) and return.
    
    5. Otherwise pass the command to execute_command()
    6. For execute_command function first we create a pipe file descriptor.
    7. Then we duplicate the process from now on (fork) to handle child and parent operations.
    8. For child we copy std_input to std_output and std_ouput to pipe file descriptor input.
    9. For parent we copy std_ouput to pipe file descriptor output.
    10. Finally we pass the command to execvp in order to replace child.
    11. Now child will execute the command using system calls in unistd.h header.
    12. For each command from now on the same process is reapted:
            * Create child
            * Assign pipe I/O to std IO
            * Replace the command with child (execvp(args[0], args))

    13. Also the chck_bckgrnd() checks if the process must be run in foreground or background.
    14. All exceptions considered:
            * interrupt signal handling
            * wrong commands or wrong types


## Features

- TAB Auto Completion
- Interrupt Signal Handling (Ctrl+C)
- Multi-Pipelines support up to infinite instructions
- History support (write/read) + U/D arrow keys for history exploration
- Shell could run any file such as itself (./shell), handled with recursive interrupt handling.
- Any standard unix based command is supported with its flags such as:
    
    * Process: top, ps, nice, renice, kill, rm, make ...
    * Local: cd, grep, echo, whoami, pwd, sort, tail, ...
    * Execution: apt install, any_path/any_script.sh, ...
    
## NOT supported
- alias
- variable assignment ($(value))


  
## Author

- [Reza Firouzi](https://www.linkedin.com/in/rezafirouzi)

  