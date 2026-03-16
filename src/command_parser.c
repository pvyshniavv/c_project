#include <stdio.h>
#include <string.h>
#include <getopt.h>
#include "../include/command.h"
#include "../include/command_parser.h"
#include "../include/error_handler.h"

#define MINIMUM_NUMBER_OF_ARGUMENTS (2)
#define RETURN_SHORT_VERSION_OF_A_FLAG (0)
#define MATCH (0)
#define FAIL (-1)

static void execute_help()
{
    printf("THIS IS THE FUTURE HELP MENU\n");
}

int parse_command(int argc, char *argv[], Command *cmd)
{
    cmd->chosen_algorithm = FRUCHTERMAN;
    cmd->chosen_format = TEXT;
    cmd->output_file_path = NULL;

    if (argc >= MINIMUM_NUMBER_OF_ARGUMENTS)
        cmd->input_file_path = argv[1];
    else
        return ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN;

    int catch_flag;

    struct option long_option[] = {
        {"algorithm", required_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'a'},
        {"text", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 't'},
        {"binary", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'b'},
        {"output", required_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'o'},
        {"help", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'h'},
        {0, 0, 0, 0}};

    while ((catch_flag = (getopt_long(argc, argv, "a:tbo:h", long_option, NULL))) != FAIL)
    {
        switch (catch_flag)
        {
        case 'a':
            if (strcmp(optarg, "fruchterman") == MATCH)
                cmd->chosen_algorithm = FRUCHTERMAN;
            else if (strcmp(optarg, "triangulation") == MATCH)
                cmd->chosen_algorithm = TRIANGULATION;
            else
                return INVALID_ALGORITHM_TYPE;
            break;
        case 't':
            cmd->chosen_format = TEXT;
            break;
        case 'b':
            cmd->chosen_format = BINARY;
            break;
        case 'o':
            cmd->output_file_path = optarg;
            break;
        case 'h':
            execute_help();
            return INITIALIZE_HELP;
        }
    }

    if (cmd->output_file_path == NULL)
        return ERROR_NO_OUTPUT_FILE_PATH_WAS_GIVEN;

    return 0;
}