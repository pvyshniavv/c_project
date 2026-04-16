#include <stdio.h>
#include <string.h>
#include <getopt.h>
#include "command.h"
#include "command_parser.h"
#include "error_handler.h"

/* Constant values assigned to numbers*/
#define MINIMUM_NUMBER_OF_ARGUMENTS (1) // 1 - program_name, 2 - input_file_path/help
#define RETURN_SHORT_VERSION_OF_A_FLAG (0)
#define MATCH (0)
#define FAIL (-1)

/* Function which prints out the help message */
static void execute_help()
{
    printf("\n--- GRAPH VISUALIZER HELP MENU ---\n");
    printf("Usage: ./program [OPTIONS] <input_file>\n\n");
    printf("Options:\n");
    printf("  -a, --algorithm <name>   Choose algorithm: 'fruchterman' (default) or 'triangulation'\n");
    printf("  -t, --text               Save output as plain text (default)\n");
    printf("  -b, --binary             Save output as binary\n");
    printf("  -o, --output <path>      [REQUIRED] Path to save the generated graph\n");
    printf("  -h, --help               Print this help menu\n\n");
}

int parse_command(int argc, char *argv[], Command *cmd)
{
    printf("[PARSER] Initializing default parameters...\n");

    // Assigning default values
    cmd->chosen_algorithm = FRUCHTERMAN;
    cmd->chosen_format = TEXT;
    cmd->input_file_path = NULL;
    cmd->output_file_path = NULL;

    // Check if the user passed any argument
    if (argc < MINIMUM_NUMBER_OF_ARGUMENTS)
    {
        printf("[PARSER] ERROR: No input file path provided!\n");
        printf("[PARSER] Usage: %s <input_file> [options]\n", argv[0]);
        return ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN;
    }

    // Catch the --help flag if it's the very first argument
    if (strcmp(argv[1], "-h") == MATCH || strcmp(argv[1], "--help") == MATCH)
    {
        execute_help();
        return INITIALIZE_HELP;
    }

    // Enforce strict positioning: argv[1] MUST be the input file.
    // We check if they accidentally put a flag here (starts with '-')
    if (argv[1][0] == '-')
    {
        printf("[PARSER] ERROR: The first argument must be your input file, not a flag ('%s').\n", argv[1]);
        return ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN;
    }

    cmd->input_file_path = argv[1];
    printf("[PARSER] Input path successfully locked to: '%s'\n", cmd->input_file_path);

    int catch_flag;
    // Structure of the long flags
    struct option long_option[] = {
        {"algorithm", required_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'a'},
        {"text", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 't'},
        {"binary", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'b'},
        {"output", required_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'o'},
        {"help", no_argument, RETURN_SHORT_VERSION_OF_A_FLAG, 'h'},
        {0, 0, 0, 0}};

    printf("[PARSER] Parsing remaining flags...\n");

    // Forcing getopt to skip argv[1] and start from argv[2]
    optind = 2;

    // Scanning for flags
    while ((catch_flag = (getopt_long(argc, argv, "a:tbo:h", long_option, NULL))) != FAIL)
    {
        switch (catch_flag)
        {
        case 'a':
            if (strcmp(optarg, "fruchterman") == MATCH)
            {
                cmd->chosen_algorithm = FRUCHTERMAN;
                printf("[PARSER] Algorithm set to: Fruchterman\n");
            }
            else if (strcmp(optarg, "triangulation") == MATCH)
            {
                cmd->chosen_algorithm = TRIANGULATION;
                printf("[PARSER] Algorithm set to: Triangulation\n");
            }
            else
            {
                printf("[PARSER] ERROR: Invalid algorithm '%s'!\n", optarg);
                return INVALID_ALGORITHM_TYPE;
            }
            break;
        case 't':
            cmd->chosen_format = TEXT;
            printf("[PARSER] Format set to: TEXT\n");
            break;
        case 'b':
            cmd->chosen_format = BINARY;
            printf("[PARSER] Format set to: BINARY\n");
            break;
        case 'o':
            cmd->output_file_path = optarg;
            printf("[PARSER] Output path set to: '%s'\n", optarg);
            break;
        case 'h':
            execute_help();
            return INITIALIZE_HELP;
        case '?':
            return ERROR_INVALID_PARAMETER;
        }
    }

    // Checking if the user passed in the output file path
    printf("[PARSER] Validating extracted arguments...\n");
    if (cmd->output_file_path == NULL)
    {
        printf("[PARSER] ERROR: No output file path provided! You must use -o or --output.\n");
        return ERROR_NO_OUTPUT_FILE_PATH_WAS_GIVEN;
    }

    printf("[PARSER] Command parsed successfully!\n\n");
    return 0;
}