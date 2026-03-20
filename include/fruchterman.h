/**
 * @file fruchterman.h
 * @brief Mathematical transformation of a graph with fruchterman algorithm.
 *
 */

#ifndef FRUCHTERMAN_H
#define FRUCHTERMAN_H
#include "graph.h"

/**
 * @brief Function transforms graph using "Fruchterman Graph Theory".
 *
 * @param initial_graph strucute, which contains original graph.
 * @param output_graph structure, into which transformed graph will be saved.
 * @return 0 or error's id.
 */
int use_fruchterman_for_graph(const Edge *initial_graph, Node *output_graph);

#endif // FRUCHTERMAN_H