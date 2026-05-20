#ifndef FILEIO_H
#define FILEIO_H

#include <stdio.h>
#include "graph.h"
#include "node.h"

Graph* load_graph_from_file(const char *filepath, int vertices_amount);

int find_max_vertex_id(const char *filepath);

void save_layout_human(const char *filepath, GraphLayout *layout);
void save_layout_binary(const char *filepath, GraphLayout *layout);

#endif