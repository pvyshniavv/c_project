#include <stdio.h>
#include "graph.h"
#include <math.h>
#include "fruchterman.h"
#include "error_handler.h"
#include "stdlib.h"

// additional function. evaluate the distance between two points on the ground
static double calculate_distance(double x1, double x2, double y1, double y2)
{
    return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

// the main algorythm
int use_fruchterman_for_graph(const Edge *initial_graph, Node *output_graph)
{
    printf("[FRUCHTERMAN] Starting force-directed layout algorithm...\n");

    // checking if pointers are not empty
    if (initial_graph == NULL || output_graph == NULL)
    {
        printf("[FRUCHTERMAN] ERROR: Invalid input graphs.\n");
        return ERROR_POINTS_TO_NULL;
    }

    unsigned int max_id = 0;
    int num_nodes = 0; // evaluating the number of nodes in list
    Node *curr_node = output_graph;
    while (curr_node != NULL)
    {
        if (curr_node->node > max_id)
        {
            max_id = curr_node->node;
        }
        num_nodes++;
        curr_node = curr_node->next;
    }

    if (num_nodes == 0)
        return 0;

    printf("[FRUCHTERMAN] Graph has %d nodes. Max ID is %u.\n", num_nodes, max_id);

    // created temporary arrays for faster calculations
    unsigned int array_size = max_id + 1;
    double *x = calloc(array_size, sizeof(double)); // calloc for filling a new memory by zeros
    double *y = calloc(array_size, sizeof(double));
    double *dx = calloc(array_size, sizeof(double));
    double *dy = calloc(array_size, sizeof(double));

    if (x == NULL || y == NULL || dx == NULL || dy == NULL)
    {
        printf("[FRUCHTERMAN] ERROR: Memory allocation failed!\n");
        free(x);
        free(y);
        free(dx);
        free(dy);
        return ERROR_POINTS_TO_NULL;
    }

    // settings for algorithm and screen
    double width = 800.0;
    double height = 600.0;
    int iterations = 100;

    double area = width * height;
    double k = sqrt(area / num_nodes); // disatance between two nodes
    double temperature = width / 10.0; // max possible vertex movement in one step (starting at 80 pixels)

    printf("[FRUCHTERMAN] Scattering nodes randomly...\n");

    // random inicialization starter positions
    curr_node = output_graph;
    while (curr_node != NULL)
    {
        unsigned int id = curr_node->node; // Use the actual node ID as the array index
        x[id] = (double)(rand() % (int)width);
        y[id] = (double)(rand() % (int)height);
        curr_node = curr_node->next;
    }

    printf("[FRUCHTERMAN] Running physics simulation (%d iterations)...\n", iterations);

    // main cycle
    for (int iter = 0; iter < iterations; iter++)
    {

        // the 1 step: repulsion (work with the fast arrays)
        for (unsigned int v = 1; v <= max_id; v++)
        {
            dx[v] = 0.0;
            dy[v] = 0.0;
            for (unsigned int u = 1; u <= max_id; u++)
            {
                if (v == u)
                    continue; // the node can't repulse itself

                // we calculate the difference in the X and Y axes and the total distance
                double delta_x = x[v] - x[u];
                double delta_y = y[v] - y[u];
                double dist = calculate_distance(x[v], x[u], y[v], y[u]);
                if (dist < 0.0001)
                    dist = 0.0001; // prevents division on 0

                double force = (k * k) / dist; // repulsive force by two nodes
                dx[v] += (delta_x / dist) * force;
                dy[v] += (delta_y / dist) * force;
            }
        }

        // the 2 step: gravitation (work with linked list of edges)
        const Edge *curr_edge = initial_graph;
        while (curr_edge != NULL)
        {
            unsigned int u = curr_edge->start_node;
            unsigned int v = curr_edge->end_node;

            // making sure we don't do out of array bounds
            if (u <= max_id && v <= max_id)
            {
                double delta_x = x[u] - x[v];
                double delta_y = y[u] - y[v];
                double dist = calculate_distance(x[v], x[u], y[v], y[u]);
                if (dist < 0.0001)
                    dist = 0.0001;

                double force = (dist * dist) / k; // gravitation force by two nodes
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
        for (unsigned int v = 1; v <= max_id; v++)
        {
            double disp_length = sqrt(dx[v] * dx[v] + dy[v] * dy[v]);

            if (disp_length < 0.0001)
                disp_length = 0.0001;
            double move_x = (dx[v] / disp_length) * fmin(disp_length, temperature);
            double move_y = (dy[v] / disp_length) * fmin(disp_length, temperature);

            x[v] += move_x;
            y[v] += move_y;

            if (x[v] < 0.0)
                x[v] = 0.0;
            if (x[v] > width)
                x[v] = width;
            if (y[v] < 0.0)
                y[v] = 0.0;
            if (y[v] > height)
                y[v] = height;
        }

        temperature *= 0.95;

        // Print progress every 25 loops so the user knows it hasn't frozen
        if ((iter + 1) % 25 == 0)
        {
            printf("[FRUCHTERMAN] Simulated %d/%d iterations...\n", iter + 1, iterations);
        }
    }

    printf("[FRUCHTERMAN] Physics complete. Saving coordinates...\n");

    // the 5 step:  transfer calculated data back to the linked list
    curr_node = output_graph;
    while (curr_node != NULL)
    {
        unsigned int id = curr_node->node;
        curr_node->x_axis = x[id];
        curr_node->y_axis = y[id];
        curr_node = curr_node->next;
    }

    free(x);
    free(y);
    free(dx);
    free(dy);

    printf("[FRUCHTERMAN] Algorithm finished successfully!\n\n");
    return 0;
}