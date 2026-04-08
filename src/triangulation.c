#include <stdio.h>
#include <stdlib.h>
#include "graph.h"
#include "triangulation.h"
#include "error_handler.h"

static unsigned int get_max_node_id(const Edge *graph)
{
    unsigned int max_id = 0;
    const Edge *current_edge = graph;

    while (current_edge != NULL)
    {
        if (current_edge->start_node > max_id)
            max_id = current_edge->start_node;

        if (current_edge->end_node > max_id)
            max_id = current_edge->end_node;

        current_edge = current_edge->next;
    }

    return max_id;
}

int triangulate_graph(const Edge *initial_graph, Node *output_graph)
{
    // checking for the graphs to be allocated
    if (initial_graph == NULL || output_graph == NULL)
    {
        return ERROR_POINTS_TO_NULL;
    }

    // defininig parameters
    unsigned int max_node_id = get_max_node_id(initial_graph);
    unsigned int total_vertices = max_node_id;

    unsigned int matrix_size = total_vertices + 1; // increasing size for indexing to start from 1

    // allocating memory for the adjacency matrix
    int **adj_matrix = malloc(matrix_size * sizeof(*int));
    if (adj_matrix == NULL)
    {
        return ERROR_POINTS_TO_NULL;
    }

    for (int i = 0; i < matrix_size; i++)
    {
        adj_matrix[i] = calloc(matrix_size * sizeof(int));
        if (adj_matrix[i] == NULL)
        {
            for (int j = 0; j < i; j++)
            {
                free(adj_matrix[j]);
            }
            free(adj_matrix);
            return ERROR_POINTS_TO_NULL;
        }
    }

    const Edge *current_edge = initial_graph;

    // marking connections
    while (current_edge != NULL)
    {
        // From Start to End
        adj_matrix[current_edge->start_node][current_edge->end_node] = 1;

        // Marking the reverse connection too, because it's an undirected graph
        adj_matrix[current_edge->end_node][current_edge->start_node] = 1;

        current_edge = current_edge->next;
    }

    // freeing memory
    for (int i = 0; i < matrix_size; i++)
    {
        free(adj_matrx[i]);
    }
    free(adj_matrix);

    return 0;
}