#ifndef NODE_H
#define NODE_H

typedef struct Node {
    int id;
    double x;
    double y;
} Node;

// Struktura trzymająca końcowe wyniki położeń wierzchołków
typedef struct {
    int count;   // Liczba wierzchołków
    Node *nodes; // Tablica wierzchołków
} GraphLayout;

#endif
