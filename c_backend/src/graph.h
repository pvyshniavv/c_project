#ifndef GRAPH_H
#define GRAPH_H

typedef struct Edge {
    int to;
    double weight;
    char *name;
} Edge;

typedef struct Vector {
    Edge *data;
    int size;
    int capacity;
} Vector;

typedef struct Graph {
    int vertices_num;
    Vector *adj;
} Graph;

Graph *create_graph(int vertices);
void add_edge(Graph *graph, int from, int to, double weight, char *name);
void free_graph(Graph *graph);

#endif
