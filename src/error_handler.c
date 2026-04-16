#include <stdio.h>
#include "error_handler.h"

void handle_error(int error_id)
{
    if (error_id == 0 || error_id == INITIALIZE_HELP)
    {
        if (error_id == INITIALIZE_HELP)
        {
            ;
        }
        return;
    }

    printf("ERROR!\n");

    switch (error_id)
    {
    case ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN:
        printf("No input file path was given.\n");
        break;

    case ERROR_NO_OUTPUT_FILE_PATH_WAS_GIVEN:
        printf("No output file path was given.\n");
        break;

    case INVALID_ALGORITHM_TYPE:
        printf("Invalid algorithm type was passed.\n");
        break;

    case ERROR_CANNOT_OPEN_FILE:
        printf("Cannot open or create file.\n");
        break;

    case ERROR_EMPTY_FILE:
        printf("File was opened succesfully, but is empty. \n");
        break;

    case ERROR_OUT_OF_MEMORY:
    case ERROR_POINTS_TO_NULL:
        printf("Memory allocation failed (malloc returned NULL). \n");
        break;

    case ERROR_GRAPH_NOT_PLANAR:
        printf("The provided graph is mathematically not planar and cannot be triangulated.\n");
        break;

    default:
        printf("An unknown error occurred.\n");
        break;
    }

    printf("If you dont know, how to use the program properly, please, type\n [-h\\--help] - to open short instructions\n");
}