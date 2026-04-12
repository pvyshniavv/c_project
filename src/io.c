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
    //checking if pointers are not empty 
    if (filename == NULL || elements == NULL) {
        return ERROR_INVALID_PARAMETER; //using error 1 from error_handler
    }

    //opening file in a reading mode
    FILE *f = fopen( filename, "r");
    if(f == NULL) {
    perror ("Brak pliku.");
    return ERROR_NO_INPUT_FILE_PATH_WAS_GIVEN; //using error 20 from error_handler
}

    //we assume 'elements' is a temporary head
    Edge *current = elements;

    //setting temporary methods
    char temp_name[3];
    unsigned int temp_start, temp_end;
    double temp_weight;

    //creating buffer for saving text-line from file
    char buffer[256];

    //we read the file over lines
    while(fgets(buffer, sizeof(buffer), f) != NULL) {

        //We try to extract 4 variables from the loaded line using sscanf
        if(sscanf(buffer, "%2s %u %u %lf", temp_name, &temp_start, &temp_end, &temp_weight) == 4) {

            //the allocation of memory, only when thw line was correct
            Edge *new_edge = (Edge*)malloc(sizeof(Edge));
            if(new_edge == NULL) {
                fclose(f);
                return ERROR_INVALID_PARAMETER;
            }

            //coping the data to the new structure
            strcpy(new_edge->edge_name, temp_name);
            new_edge->start_node = temp_start;
            new_edge->end_node = temp_end;
            new_edge->weight = temp_weight;
            new_edge->next = NULL;

            //attaching the node to the list
            current->next = new_edge;
            current = new_edge;

        }
    }

    fclose(f);
    return 0; //if everything ended succesfully 

}






int create_output_file(enum file_output_type format, const Node *elements, char *output_file_path)
{
    //checking if pointers are not empty 
    if(output_file_path== NULL || elements == NULL) {
        return ERROR_INVALID_PARAMETER; //using error 1 from error_handler
    }

    //we need to open the file in different modes depending on the format 
    //"w" is for writing, "wb" is for bibary writing
    FILE *f;
    if(format == TEXT) {
        f = fopen(output_file_path, "w");
        } else {
            f = fopen(output_file_path, "wb");
        }


        if(f == NULL) {
            //cannot open or create file
            return ERROR_CANNOT_OPEN_FILE;
        }
        //we assume 'elements' is a temporary head
        const Node *current = elements->next;
        while(current != NULL) {
            if(format == TEXT) {
                //simple fprintf for text format
                fprintf(f, "%u %.2f %.2f\n", current->node, current->x_axis, current->y_axis);
            }
            else if (format == BINARY) {
                //we have to save string length (1 byte), then string, then two doubles.
                //but out struct has 'unsigned int node'. then we habe to conbert int to string first
                char name_str[50];
        
                sprintf(name_str, "%u", current->node); //converting number to text

                unsigned char name_length = (unsigned char)strlen(name_str);

                //writing binary data: &variable, size_of_element, count of elements, file name)
                fwrite(&name_length, sizeof(unsigned char), 1, f);  //1 byte
                fwrite(name_str, sizeof(char), name_length, f);     //string characters ASCII 
                fwrite(&current->x_axis, sizeof(double), 1, f);     //8 bytes
                fwrite(&current->y_axis, sizeof(double), 1, f);     //8 bytes
            }

            current = current->next; //move to the next node
        }
        fclose(f); //close file

    return 0; //if everything ended succesfully 
}