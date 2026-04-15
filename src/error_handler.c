#include <stdio.h>
#include "error_handler.h"

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
    if(error_id == ERROR_CANNOT_OPEN_FILE) 
    {
        printf("ERROR!\n");
        printf("Cannot open or create file.\n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
    if(error_id == ERROR_EMPTY_FILE) 
    {
        printf("ERROR!\n");
        printf("File was opened succesfully, but is empty. \n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
    if(error_id == ERROR_OUT_OF_MEMORY) 
    {
        printf("ERROR!\n");
        printf("Memory allocation failed (malloc returned NULL). \n");
        printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions");
    }
}