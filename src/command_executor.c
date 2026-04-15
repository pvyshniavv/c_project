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
        Edge* edges_list = (Edge*)malloc(sizeof(Edge));
    if(edges_list == NULL) {
        return ERROR_OUT_OF_MEMORY;
        edges_list->next = NULL;


        int read_status = deserialize_file(cmd.input_file_path, edges_list);
        if(read_status != 0) {
            free(edges_list);
            return read_status;
        }

        Node *nodes_list = create_nodes_from_edges(edges_list);
        if(nodes_list == NULL) {

            Edge *curr_e = edges_list;
            while(curr_e != NULL) {
                Edge *temp = curr_e;
                curr_e = curr_e->next;
                free(temp);
            }
            return ERROR_OUT_OF_MEMORY;
        }

        switch (cmd.chosen_algorithm) {
            case FRUCHTERMAN:
            use_fruchterman_for_graph(edges_list->next, nodes_list->next);
            break;

            case TRIANGULATION:
            use_euler_planarity_test(edges_list->next, nodes_list->next);
            break;



            int write_status = create_output_file(cmd.input_file_path, nodes_list, cmd.output_file_path);



            

            
        }
        
    }
    return 0;
}