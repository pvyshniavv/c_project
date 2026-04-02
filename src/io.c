#include <stdio.h>
#include "command.h"
#include "graph.h"
#include "io.h"

/**
 * @brief function checks if the file is valid
 *
 * @param filename name of the user's file
 * @return 0 if the file is valid or error's id
 */

int deserialize_file(char *filename, Edge *elements)
{
    FILE *f = fopen( filename, "r");
    if(f == NULL) 
    perror ("Brak pliku.");
    return 0;

    if(scanf(filename, "%lf", &elements) != 1) {
        fclose(filename);
        return 0;
    }

}

int create_output_file(enum file_output_type format, const Node *elements, char *output_file_path)
{
    return 0;
}