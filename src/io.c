#include <stdio.h>
#include "command.h"
#include "graph.h"
#include "io.h"
#include "error_handler.h"

/**
 * @brief function checks if the file is valid
 *
 * @param filename name of the user's file
 * @return 0 if the file is valid or error's id
 */

int deserialize_file(char *filename, Edge *elements)
{
    FILE *file_containing_graph = fopen("filename", "r");
    if (file_containing_graph == NULL)
        return FILE_DOES_NOT_EXIST;
    fclose(file_containing_graph);
    return 0;
}

int create_output_file(enum file_output_type format, const Node *elements, char *output_file_path)
{
    return 0;
}