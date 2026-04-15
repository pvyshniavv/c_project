#include <stdio.h>
#include "command.h"
#include "command_executor.h"
#include "graph.h"
#include "io.h"
#include "error_handler.h"
#include "triangulation.h"
#include "fruchterman.h"

/**
 * @brief helper function to generate a liked list of Nodes based on the max node id found in Edges.
 * @details iterates through the given edge list to find the highest node id.
 * Then it allocates a lomplete linked list of nodes from 0 to max_node,
 * initializing their coordinates (x_axis, y_axis) to 0.0.
 * 
 * @param edges_temp_head pointer to the temporary head of the Edges linked list.
 * @param output_graph structure, into which transformed graph will be saved.
 * @return Node* pointer to the temporary head of the newly created Nodes linked list.
 */


static Node* create_nodes_from_edges (Edge* edges_temp_head) {
    Node* nodes_head = (Node*)malloc(sizeof(Node));
    if(nodes_head == NULL ) return NULL;
    nodes_head->next = NULL;

    Node* current_node = nodes_head;
    unsigned int max_node = 0;


    Edge* curr_edge = edges_temp_head->next;
    while(curr_edge != NULL) {
        if(curr_edge->start_node > max_node) max_node = curr_edge->start_node;
        if(curr_edge->end_node > max_node) max_node = curr_edge->end_node;
        curr_edge = curr_edge->next;
    }

    for(unsigned int i = 0; i<= max_node; i++) {
        Node* new_node = (Node*)malloc(sizeof(Node));
        if(new_node != NULL) {
            new_node->node = i;
            new_node->x_axis = 0.0;
            new_node->y_axis = 0.0;
            new_node->next = NULL;

            current_node->next = new_node;
            current_node = new_node;
        }
    }
    return nodes_head;
}


int execute_command(Command cmd)
{
    return 0;
}