#include <stdio.h>
#include "command.h"
#include "graph.h"
#include "io.h"
#include "error_handler.h"
#include "string.h"
#include "stdlib.h"

/**
 * @brief function checks if the file is valid
 *
 * @param filename name of the user's file
 * @return 0 if the file is valid or error's id
 */

int deserialize_file(char *filename, Edge *elements)
{
    printf("[IO] Opening input file: '%s'...\n", filename);

    // Checking if pointers are not empty
    if (filename == NULL || elements == NULL)
    {
        printf("[IO] ERROR: Invalid parameters passed to deserializer.\n");
        return ERROR_INVALID_PARAMETER;
    }

    // Opening file in a reading mode
    FILE *f = fopen(filename, "r");
    if (f == NULL)
    {
        printf("[IO] ERROR: Could not open file '%s'.\n", filename);
        return ERROR_CANNOT_OPEN_FILE;
    }

    // We assume 'elements' is a temporary head
    Edge *current = elements;

    // Setting temporary methods
    char temp_name[3];
    unsigned int temp_start, temp_end;
    double temp_weight;

    // Creating buffer for saving text-line from file
    char buffer[256];

    // Track how many valid lines we find
    int edges_read = 0;

    // We read the file over lines
    while (fgets(buffer, sizeof(buffer), f) != NULL)
    {

        // We try to extract 4 variables from the loaded line using sscanf
        if (sscanf(buffer, "%2s %u %u %lf", temp_name, &temp_start, &temp_end, &temp_weight) == 4)
        {

            // The allocation of memory, only when thw line was correct
            Edge *new_edge = malloc(sizeof(Edge));
            if (new_edge == NULL)
            {
                fclose(f);
                return ERROR_OUT_OF_MEMORY;
            }

            // Coping the data to the new structure
            strcpy(new_edge->edge_name, temp_name);
            new_edge->start_node = temp_start;
            new_edge->end_node = temp_end;
            new_edge->weight = temp_weight;
            new_edge->next = NULL;

            // Attaching the node to the list
            current->next = new_edge;
            current = new_edge;

            edges_read++;
            printf("[IO] Loaded edge: %s (%u -> %u) Weight: %.2f\n", temp_name, temp_start, temp_end, temp_weight);
        }
        else
        {
            printf("[IO] Warning: Skipped invalid line: %s", buffer);
        }
    }

    fclose(f);

    // Check if file was empty
    if (edges_read == 0)
    {
        printf("[IO] ERROR: File was opened successfully, but contained no valid edges!\n");
        return ERROR_EMPTY_FILE;
    }

    printf("[IO] Successfully loaded %d edges.\n\n", edges_read);
    return 0;
}

int create_output_file(enum file_output_type format, const Node *elements, char *output_file_path)
{
    printf("[IO] Creating output file: '%s' (Format: %s)...\n",
           output_file_path, format == TEXT ? "TEXT" : "BINARY");

    // Checking if pointers are not empty
    if (output_file_path == NULL || elements == NULL)
    {
        return ERROR_INVALID_PARAMETER;
    }

    // We need to open the file in different modes depending on the format
    // "w" is for writing, "wb" is for binary writing
    FILE *f;
    if (format == TEXT)
    {
        f = fopen(output_file_path, "w");
    }
    else
    {
        f = fopen(output_file_path, "wb");
    }

    if (f == NULL)
    {
        // Cannot open or create file
        printf("[IO] ERROR: Could not create output file '%s'.\n", output_file_path);
        return ERROR_CANNOT_OPEN_FILE;
    }
    // Skip the dummy head
    const Node *current = elements->next;
    int nodes_written = 0;

    while (current != NULL)
    {
        if (format == TEXT)
        {
            // Simple fprintf for text format
            fprintf(f, "%u %.2f %.2f\n", current->node, current->x_axis, current->y_axis);
        }
        else if (format == BINARY)
        {
            // Write raw numbers into the file
            fwrite(&current->node, sizeof(unsigned int), 1, f);
            fwrite(&current->x_axis, sizeof(double), 1, f);
            fwrite(&current->y_axis, sizeof(double), 1, f);
        }

        current = current->next; // Move to the next node
        nodes_written++;
    }
    fclose(f); // Close file

    printf("[IO] Success! Wrote %d nodes to '%s'.\n", nodes_written, output_file_path);
    return 0;
}