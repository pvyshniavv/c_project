#include <stdio.h>
#include <stdlib.h>
#include "graph.h"
#include "triangulation.h"
#include "error_handler.h"

#define EDGE_EXISTS (1)
#define VISITED (1)
#define STARTING_NODE (1)
#define STARTING_PARENT_NODE (-1)

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

/**
 * @brief Checks if a graph passes Euler's planarity formula (E <= 3V - 6).
 * @return 0 if it passes (or has < 3 vertices), ERROR_GRAPH_NOT_PLANAR if it is mathematically non-planar.
 */
static int use_euler_planarity_test(int **adj_matrix, int matrix_size, int total_vertices)
{
    // if less then 3 graph is always planar
    if (total_vertices < 3)
    {
        return 0;
    }

    unsigned int edge_count = 0;

    for (int i = 1; i < matrix_size; i++)
    {
        // deduplicating undirected edges
        for (int j = i + 1; j < matrix_size; j++)
        {
            if (adj_matrix[i][j] == EDGE_EXISTS)
            {
                edge_count++;
            }
        }
    }
    // calcualting max edges that a planar graph could have using Euler's formula:
    // E <= 3V - 6
    unsigned int max_planar_edges = (3 * total_vertices) - 6;

    // check if there are too many edges
    if (max_planar_edges < edge_count)
    {
        return ERROR_GRAPH_NOT_PLANAR; // test is not passed
    }

    return 0; // test is successfully passed
}

/**
 * @brief Recursive DFS to find cycles and add dummy edges.
 * * @param current_node The intersection we are currently standing on.
 * @param parent_node The intersection we just came from.
 * @param adj_matrix The 2D graph grid.
 * @param visited Array tracking where we have been.
 * @param matrix_size Size of the matrix (for our loops).
 */
static void dfs_find_holes(int current_node, int parent_node, int **adj_matrix, int *visited, int matrix_size, int *pathstack, int depth)
{
    visited[current_node] = VISITED;
    pathstack[depth] = current_node;

    for (int j = 1; j < matrix_size; j++)
    {
        if (adj_matrix[current_node][j] == EDGE_EXISTS)
        {
            // restriction considering the node we're just visited
            if (j == parent_node)
            {
                continue;
            }

            // Cycle detected
            if (visited[j] == VISITED)
            {
                int cycle_length = 0;
                int cycle_start_index = 0;

                for (int current_depth = depth; current_depth >= 0; current_depth--)
                {
                    cycle_length++;

                    // cycle is ended
                    if (pathstack[current_depth] == j)
                    {
                        cycle_start_index = current_depth;
                        break;
                    }
                }

                // triangulation...

                // checking if there's a hole
                if (cycle_length > 3)
                {
                    int anchor_node = pathstack[depth];

                    // create dummy edge
                    for (int k = depth - 2; k > cycle_start_index; k--)
                    {
                        int target_node = pathstack[k];

                        adj_matrix[anchor_node][target_node] = EDGE_EXISTS;
                        adj_matrix[target_node][anchor_node] = EDGE_EXISTS;
                    }
                }
            }
            else
            {
                dfs_find_holes(j, current_node, adj_matrix, visited, matrix_size, pathstack, depth + 1); // Calling this function as a recursion
            }
        }
    }
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
    int **adj_matrix = malloc(matrix_size * sizeof(int *));
    if (adj_matrix == NULL)
    {
        return ERROR_POINTS_TO_NULL;
    }

    for (int i = 0; i < matrix_size; i++)
    {
        adj_matrix[i] = calloc(matrix_size, sizeof(int));
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

    // populating matrix
    while (current_edge != NULL)
    {
        // From Start to End
        adj_matrix[current_edge->start_node][current_edge->end_node] = EDGE_EXISTS;

        // Marking the reverse connection too, because it's an undirected graph
        adj_matrix[current_edge->end_node][current_edge->start_node] = EDGE_EXISTS;

        current_edge = current_edge->next;
    }

    // check if the graph has 3 or more vertices to then check if it's a planar graph
    // graph with less then 3 vertices if always planar
    if (use_euler_planarity_test(adj_matrix, matrix_size, total_vertices) == ERROR_GRAPH_NOT_PLANAR)
    {
        for (int i = 0; i < matrix_size; i++)
        {
            free(adj_matrix[i]);
        }
        free(adj_matrix);

        return ERROR_GRAPH_NOT_PLANAR;
    }

    int *visited = calloc(matrix_size, sizeof(int));
    if (visited == NULL)
    {
        for (int i = 0; i < matrix_size; i++)
        {
            free(adj_matrix[i]);
        }
        free(adj_matrix);

        return ERROR_POINTS_TO_NULL;
    }

    int *pathstack = calloc(matrix_size, sizeof(int));
    if (pathstack == NULL)
    {
        free(visited);
        for (int i = 0; i < matrix_size; i++)
        {
            free(adj_matrix[i]);
        }
        free(adj_matrix);

        return ERROR_POINTS_TO_NULL;
    }

    unsigned int depth = 0;

    dfs_find_holes(STARTING_NODE, STARTING_PARENT_NODE, adj_matrix, visited, matrix_size, pathstack, depth);

    free(visited);
    free(pathstack);

    // freeing memory
    for (int i = 0; i < matrix_size; i++)
    {
        free(adj_matrix[i]);
    }
    free(adj_matrix);

    return 0;
}