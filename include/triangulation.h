/**
 * @file triangulation.h
 * @brief Mathematical transformation of a graph with triangulation algorithm.
 *
 */

#ifndef TRIANGULATION_H
#define TRIANGULATION_H
#include "graph.h"

/**
 * @brief Function transforms graph using "Planar Straight-Line Drawing algorithm".
 *
 * @param initial_graph strucute, which contains original graph.
 * @param output_graph structure, into which transformed graph will be saved.
 * @return 0 or error's id.
 */
int triangulate_graph(const Edge *initial_graph, Node *output_graph);

#endif // TRIANGULATION_H