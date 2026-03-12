#include <stdio.h>
#include <unistd.h>
#include "../include/command.h"
#include "../include/command_parser.h"
#include "../include/error_handler.h"

static void execute_help()
{
}

int parse_command(int argc, char *argv[], Command *cmd)
{

    if (argv[1] == '--help')
    {
        execute_help();
        return 0;
    }

    if (argv > 1)
        cmd->input_file_name_or_path = argv[1];
    else
        return ERROR_NO_FILE_GIVEN;

    getopt(argc, argv, "a:tbh")

        return 0;
}