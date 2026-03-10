/**
 * @file spectral.h
 * @brief Mathematical transformation of a graph with spectral algorithm.
 *
 */

#ifndef SPECTRAL_H
#define SPECTRAL_H
#include "graph.h"

/**
 * @brief Function transforms graph using "Spectral Graph Theory".
 *
 * @param initial_graph strucute, which contains original graph.
 * @param output_graph structure, into which transformed graph will be saved.
 * @return 0 or error's id.
 */
int use_spectral_for_graph(const Edge *initial_graph, Node *output_graph);

#endif // SPECTRAL_H