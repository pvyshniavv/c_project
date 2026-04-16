#include <stdio.h>
#include <stdlib.h>
#include "command.h"
#include "command_executor.h"
#include "graph.h"
#include "io.h"
#include "error_handler.h"
#include "triangulation.h"
#include "fruchterman.h"

/**
 * @brief Allocates a linked list of Nodes from 0 to max_node.
 */
static Node *allocate_full_node_list(Edge *first_actual_edge)
{
    unsigned int max_node = 0;
    Edge *curr_edge = first_actual_edge;

    while (curr_edge != NULL)
    {
        if (curr_edge->start_node > max_node)
            max_node = curr_edge->start_node;

        if (curr_edge->end_node > max_node)
            max_node = curr_edge->end_node;

        curr_edge = curr_edge->next;
    }

    Node *head = NULL;
    Node *current = NULL;

    for (unsigned int i = 1; i <= max_node; i++)
    {
        Node *new_node = malloc(sizeof(Node));
        // free memory in case of partial allocation failure
        if (new_node == NULL)
        {
            Node *curr = head;
            while (curr != NULL)
            {
                Node *temp = curr;
                curr = curr->next;
                free(temp);
            }
            return NULL;
        }

        new_node->node = i;
        new_node->x_axis = 0.0;
        new_node->y_axis = 0.0;
        new_node->next = NULL;

        if (head == NULL)
        {
            head = new_node;
            current = new_node;
        }
        else
        {
            current->next = new_node;
            current = new_node;
        }
    }
    return head;
}

int execute_command(Command cmd, Edge *edges_dummy_head, Node *nodes_dummy_head)
{
    // 1. Read graph data into provided memory
    int read_status = deserialize_file(cmd.input_file_path, edges_dummy_head);
    if (read_status != 0)
        return read_status;

    Edge *actual_edges = edges_dummy_head->next;

    nodes_dummy_head->next = allocate_full_node_list(actual_edges);
    if (nodes_dummy_head->next == NULL)
        return ERROR_OUT_OF_MEMORY;

    int algo_status = 0;

    // 2. Route data to specific algorithm
    switch (cmd.chosen_algorithm)
    {
    case FRUCHTERMAN:
        algo_status = use_fruchterman_for_graph(actual_edges, nodes_dummy_head->next);
        break;

    case TRIANGULATION:
        algo_status = triangulate_graph(actual_edges, nodes_dummy_head->next);
        break;

    default:
        return INVALID_ALGORITHM_TYPE;
    }

    // 3. Write output or handle algorithmic error
    if (algo_status != 0)
        return algo_status;

    return create_output_file(cmd.chosen_format, nodes_dummy_head, cmd.output_file_path);
}