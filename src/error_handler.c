#include <stdio.h>
#include "../include/error_handler.h"

void handle_error(int error_id)
{
    if (error_id == ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN)
    {
        printf("ERROR!\n");
        printf("No input file path was given.\n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
    else if (error_id == ERROR_NO_OUTPUT_FILE_PATH_WAS_GIVEN)
    {
        printf("ERROR!\n");
        printf("No output file path was given.\n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
    if (error_id == INVALID_ALGORITHM_TYPE)
    {
        printf("ERROR!\n");
        printf("Invalid algorithm type was passed.\n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
    if (error_id == INITIALIZE_HELP)
    {
        printf("Hope it helped\n");
    }
}