#include "graph.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//---------------VECTORS----------------
/**
 * Initializes a vector with an initial capacity of 4.
 * @param vector The vector to initialize.
 */
void vector_init(Vector *vector) {
    vector->size = 0;
    vector->capacity = 4; // Initial capacity
    vector->data = malloc(vector->capacity * sizeof(Edge));
    if (!vector->data) {
        perror("Nie udało się zaalokować pamięci dla danych wektora");
        exit(EXIT_FAILURE);
    }
}

/**
 * Adds an edge to the vector, resizing by factor of 2 if necessary.
 * @param vector The vector to add the edge to.
 * @param e The edge to add.
 */
void vector_push_back(Vector *vector, Edge e) {
    if (vector->size >= vector->capacity) {
        vector->capacity *= 2;
        vector->data = realloc(vector->data, vector->capacity * sizeof(Edge));
        if (!vector->data) {
            perror("Nie udało się realokować pamięci dla danych wektora");
            exit(EXIT_FAILURE);
        }
    }
    vector->data[vector->size++] = e;
}

/**
 * Frees the memory allocated for the vector, including the edge names.
 * @param vector The vector to free.
 */
void vector_free(Vector *vector) {
    for (int i = 0; i < vector->size; i++) {
        free(vector->data[i].name);
    }
    free(vector->data);
}

//---------------GRAPH----------------
/**
 * Creates a graph with the specified number of vertices.
 * @param vertices The number of vertices in the graph.
 * @return A pointer to the created graph.
 */
Graph *create_graph(int vertices) {
    if (vertices <= 0) {
        fprintf(stderr, "Nieprawidłowa liczba wierzchołków: %d\n", vertices);
        exit(EXIT_FAILURE);
    }
    Graph *graph = malloc(sizeof(Graph));
    if (!graph) {
        perror("Nie udało się zaalokować pamięci dla grafu");
        exit(EXIT_FAILURE);
    }
    graph->vertices_num = vertices;
    graph->adj = calloc((size_t)(vertices + 1), sizeof(Vector));
    if (!graph->adj) {
        perror("Nie udało się zaalokować pamięci dla list sąsiedztwa grafu");
        free(graph);
        exit(EXIT_FAILURE);
    }
    for (int i = 1; i <= vertices; i++) {
        vector_init(&graph->adj[i]);
    }
    return graph;
}

/**
 * Adds an edge to the graph, creating a reverse edge for undirected graphs.
 * @param graph The graph to add the edge to.
 * @param from The starting vertex of the edge.
 * @param to The ending vertex of the edge.
 * @param weight The weight of the edge.
 * @param name The name of the edge.
 */
void add_edge(Graph *graph, int from, int to, double weight, char *name) {
    if (from <= 0 || to <= 0 || from > graph->vertices_num ||
        to > graph->vertices_num) {
        fprintf(stderr,
                "Ignorowanie krawędzi z nieprawidłowym ID wierzchołka: %d -> "
                "%d (dozwolony zakres: 1..%d)\n",
                from, to, graph->vertices_num);
        return;
    }
    Edge e;
    e.to = to;
    e.weight = weight;
    e.name = strdup(name);
    if (!e.name) {
        perror("Nie udało się zaalokować pamięci dla nazwy krawędzi");
        exit(EXIT_FAILURE);
    }
    Edge reverse_e;
    reverse_e.to = from;
    reverse_e.weight = weight;
    reverse_e.name = strdup(name);
    if (!reverse_e.name) {
        perror("Nie udało się zaalokować pamięci dla nazwy krawędzi odwrotnej");
        exit(EXIT_FAILURE);
    }
    vector_push_back(&graph->adj[from], e);
    vector_push_back(&graph->adj[to], reverse_e);
}

/**
 * Frees the memory allocated for the graph, including all edges and their
 * names.
 * @param graph The graph to free.
 */
void free_graph(Graph *graph) {
    for (int i = 1; i <= graph->vertices_num; i++) {
        vector_free(&graph->adj[i]);
    }
    free(graph->adj);
    free(graph);
}
