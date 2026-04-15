#include <stdio.h>
#include "graph.h"
#include <math.h>
#include "fruchterman.h"

//additional function. evaluate the distance between two points on the ground
static double calculate_distance(double x1, double x2, double y1, double y2) {
    return sqrt ((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
}

//the main algorythm
int use_fruchterman_for_graph(const Edge *initial_graph, Node *output_graph) {
    //checking if pointers are not empty 
    if(initial_graph == NULL || output_graph == NULL)
    return -1;

    //evaluating the number of nodes in list
    int num_nodes = 0;
    Node *curr_node = output_graph;
    while( curr_node != NULL) {
        num_nodes++;
        curr_node = curr_node->next;
    }

    if (num_nodes == 0) return 0;

    //created temporary errays for faster calculations
    double *x = (double *)calloc(num_nodes, sizeof(double)); //calloc for filling a new memory by zeros
    double *y = (double *)calloc(num_nodes, sizeof(double));
    double *dx = (double *)calloc(num_nodes, sizeof(double));
    double *dy = (double *)calloc(num_nodes, sizeof(double));


    //settings for algorithm and screen
    double width = 800.0;
    double height = 600.0;
    int iterations = 100;

    double area = width * height;
    double k = sqrt(area / num_nodes); //disatance between two nodes
    double temperature = width / 10.0; //max possible vertex movement in one step (starting at 80 pixels)


    //random inicialization starter positions 
    curr_node = output_graph;
    int i = 0;
    x[i] = (double)(rand() % (int)width);
    y[i] = (double)(rand() % (int)height);
    curr_node = curr_node->next;
    i++;

    //main cycle
    for(int iter = 0; iter < iterations; iter++) {


        // the 1 step: repulsion (work with the fast arrays)
        for(int v = 0; v < num_nodes; v++) {
            dx[v] = 0.0;
            dy[v] = 0.0;
            for(int u = 0; u < num_nodes; u++) {
            if(v == u) continue; //the node can't repulse itself

            //we calculate the difference in the X and Y axes and the total distance
            double delta_x = x[v] - x[u];
            double delta_y = y[v] - y[u];
            double dist = calculate_distance(x[v], x[u], y[v], y[u]);
            if(dist < 0.0001) dist = 0.0001; //prevents division on 0

            double force = (k * k) / dist; //repulsive force by two nodes
            dx[v] += (delta_x / dist) * force; 
            dy[v] += (delta_y / dist) * force;
            }
        }

        //the 2 step: gravitation (work with linked list of edges)
        const Edge *curr_edge = initial_graph;
        while(curr_edge != NULL) {
            unsigned int u = curr_edge->start_node;
            unsigned int v = curr_edge->end_node;


            //making sure we don't do out of array bounds
            if(u < (unsigned int)num_nodes && v < (unsigned int)num_nodes) {
            double delta_x = x[u] - x[v];
            double delta_y = y[u] - y[v];
            double dist = calculate_distance(x[v], x[u], y[v], y[u]);
            if(dist < 0.0001) dist = 0.0001;

            double force = (dist * dist) / k; //gravitation force by two nodes
            double disp_x = (delta_x / dist) * force; 
            double disp_y = (delta_y / dist) * force;

            dx[u] -= disp_x;
            dy[u] -= disp_y;
            dx[v] += disp_x;
            dy[v] += disp_y;
            }
            curr_edge = curr_edge->next;
        }

        // the 3 step: Using force (updating positions)
        for(int v = 0; v< num_nodes; v++) {
            double disp_length = sqrt(dx[v] * dx[v] + dy[v] * dy[v]);

            if(disp_length < 0.0001) disp_length = 0.0001;
            double move_x = (dx[v] / disp_length) * fmin(disp_length, temperature);
            double move_y = (dy[v] / disp_length) * fmin(disp_length, temperature);

            x[v] += move_x;
            y[v] += move_y;

            if (x[v] < 0.0) x[v] = 0.0;
            if (x[v] > width) x[v] = width;
            if (y[v] > height) y[v] = height;
            if (y[v] < 0.0) y[v] = 0.0;
        }
        
        temperature *= 0.95;
        
        //the 5 step:  transfer calculated data back to the linked list
        curr_node = output_graph;
        int i = 0;
        while(curr_node != NULL) {
            curr_node->x_axis = x[i];
            curr_node->y_axis = y[i];
            curr_node = curr_node->next;
            i++;
        }
        free(x);
        free(y);
        free(dx);
        free(dy);
    }
    return 0;
}