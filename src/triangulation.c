#include <stdio.h>
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
    if (initial_graph == NULL || output_graph == NULL)
        return ERROR_INVALID_PARAMETER;

    unsigned int max_node_id = get_max_node_id(initial_graph);

    unsigned int total_vertices = max_node_id;

    return 0;
}