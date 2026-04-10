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

    if (filename == NULL || elements == NULL) {
        return ERROR_INVALID_PARAMETER; //using error 1 from error_handler
    }

    //openin file in reading mode
    FILE *f = fopen( filename, "r");
    if(f == NULL) {
    perror ("Brak pliku.");
    return 0; 
}


    Edge *current = elements;

    char temp_name[3];
    unsigned int temp_start, temp_end;
    double temp_wieght;

    //creating buffer for saving text-line form file
    char buffer[256];

    while(fgets(buffer, sizeof(buffer), f) != NULL) {

        if(sscanf(buffer, "%2s %u %u %lf", temp_name, &temp_start, &temp_end, &temp_weight) == 4) {

            Edge *new_edge = (Edge*)malloc(sizeof(Edge));
            if(new_edge == NULL) {
                fclose(f);
                return ERROR_INVALID_PARAMETER;
            }

            
        }
    }


}






int create_output_file(enum file_output_type format, const Node *elements, char *output_file_path)
{
    return 0;
}