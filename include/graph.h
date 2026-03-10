/**
 * @file graph.h
 * @brief Structures, which contain graph broken down into parts.
 *
 * @details Structure Edge:
 * Initial graph is broken down into edges, that's why this structure is a singly linked list, where every single structure contains data about one edge.
 * Structure Node:
 * Generated graph is broken down into nodes, that's why this structure is a singly linked list, where every single structure contains data about one node.
 */

#ifndef GRAPH_H
#define GRAPH_H

/**
 * @brief Structure, which contains graph partitioned into edges.
 *
 */
typedef struct
{
    char edge_name[2];       ///< Name of the edge (e.g. AB).
    unsigned int start_node; ///< Ordinal number of the edge's starting node.
    unsigned int end_node;   ///< Ordinal number of the edge's ending node.
    double weight;           ///< Edge's weight.
    struct Edge *next;       ///< Pointer to the next edge.
} Edge;

/**
 * @brief Structure, which contains graph partitioned into vertices.
 *
 */
typedef struct
{
    unsigned int node; ///< Ordinal number of the node.
    double x_axis;     ///< Node's position in the x axis.
    double y_axis;     ///< Node's position in the y axis.
    struct Node *next;
} Node;

#endif // GRAPH_H