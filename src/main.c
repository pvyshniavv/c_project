#include <stdio.h>
#include <stdlib.h>
#include "command.h"
#include "command_parser.h"
#include "error_handler.h"
#include "command_executor.h"
#include "graph.h"

/* --- Memory Cleanup Helpers --- */
static void free_edges_list(Edge *head)
{
    Edge *curr = head;
    while (curr != NULL)
    {
        Edge *temp = curr;
        curr = curr->next;
        free(temp);
    }
}

static void free_nodes_list(Node *head)
{
    Node *curr = head;
    while (curr != NULL)
    {
        Node *temp = curr;
        curr = curr->next;
        free(temp);
    }
}

int main(int argc, char *argv[])
{
    Command cmd;
    int result;

    result = parse_command(argc, argv, &cmd);
    if (result != 0)
    {
        handle_error(result);
        return (result == INITIALIZE_HELP) ? 0 : 1; // Exit early if parsing fails or help is shown
    }

    printf("[MAIN] Initializing root memory structures...\n");

    Edge *edges_dummy_head = malloc(sizeof(Edge));
    Node *nodes_dummy_head = malloc(sizeof(Node));

    if (edges_dummy_head == NULL || nodes_dummy_head == NULL)
    {
        free(edges_dummy_head);
        free(nodes_dummy_head);
        handle_error(ERROR_OUT_OF_MEMORY);
        return ERROR_OUT_OF_MEMORY;
    }
    edges_dummy_head->next = NULL;
    nodes_dummy_head->next = NULL;

    printf("[MAIN] Handing control to Executor...\n");
    result = execute_command(cmd, edges_dummy_head, nodes_dummy_head);
    if (result != 0)
        handle_error(result);

    printf("[MAIN] Cleaning up memory and exiting...\n");
    free_edges_list(edges_dummy_head);
    free_nodes_list(nodes_dummy_head);

    return result;
}