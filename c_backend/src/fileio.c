#define _POSIX_C_SOURCE 200809L // Wymagane dla %ms na Linuxie (Mateusz oceń)
#include "node.h"
#include "fileio.h"
#include "graph.h"
#include <stdio.h>
#include <stdlib.h>

Graph* load_graph_from_file(const char *filepath, int vertices_amount) {
    FILE *file = fopen(filepath, "r");
    if (!file) {perror("Bląd: Nie udalo się otworzyć pliku wejściowego do odczytu danych");
        return NULL;
    }
    Graph *graph = create_graph(vertices_amount);

    char *name = NULL;
    int from, to;
    double weight;

    while (fscanf(file, "%ms %d %d %lf", &name, &from, &to, &weight) == 4) {
        if (from <= 0 || to <= 0) {
            fprintf(stderr, "Ostrzeżenie: Ignorowanie krawędzi z niedodatnim ID wierzcholka.\n");
            free(name);
            continue;
        }
        
        add_edge(graph, from, to, weight, name);
        free(name); 
        name = NULL; 
    }

    fclose(file);
    return graph;
}
int find_max_vertex_id(const char *filepath) {
    FILE *file = fopen(filepath, "r");
    if (file == NULL) return -1;

    int from, to;
    double weight;
    int max_vertex = 0;

    while (fscanf(file, "%*s %d %d %lf", &from, &to, &weight) == 3) {
        if (from > max_vertex) max_vertex = from;
        if (to > max_vertex) max_vertex = to;
    }
    fclose(file);
    return max_vertex;
}

void save_layout_human(const char *filepath, GraphLayout *layout) {
    FILE *f = fopen(filepath, "w");
    if (!f) {perror("Nie udalo się otworzyć pliku do zapisu");return;}

    fprintf(f, "%d\n", layout->count);
    for (int i = 0; i < layout->count; i++) {
        fprintf(f, "%d %f %f\n", layout->nodes[i].id, layout->nodes[i].x, layout->nodes[i].y);
    }

    fclose(f);
    printf("Wyniki zapisano pomyślnie do: %s\n", filepath);
}

void save_layout_binary(const char *filepath, GraphLayout *layout) {
    FILE *f = fopen(filepath, "wb");
    if (!f) {perror("Nie udalo się otworzyć pliku do zapisu");return;}
    fwrite(&layout->count, sizeof(int), 1, f);
    for (int i = 0; i < layout->count; i++) {
        Node n = layout->nodes[i];
        fwrite(&n.id, sizeof(int), 1, f);      // 4 bytes
        fwrite(&n.x,  sizeof(double), 1, f);   // 8 bytes
        fwrite(&n.y,  sizeof(double), 1, f);   // 8 bytes
    }

    fclose(f);
    printf("Wyniki zapisano pomyślnie do: %s\n", filepath);
}
