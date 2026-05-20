#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "graph.h"
#include "node.h"

double** create_laplacian_matrix(Graph *graph) {
    int n = graph->vertices_num;
    double **laplacian_matrix = malloc(n * sizeof(double *));
    if (laplacian_matrix == NULL) {
        perror("Blad alokacji pamieci macierzy Laplace'a - wiersze");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < n; i++) {
        laplacian_matrix[i] = calloc(n, sizeof(double));
        if (laplacian_matrix[i] == NULL) {
            perror("Blad alokacji pamieci macierzy Laplace'a - kolumny");
            exit(EXIT_FAILURE);
        }
    }

    for (int i = 1; i <= n; i++) {           
        double degree = 0.0;
        for (int k = 0; k < graph->adj[i].size; k++) {
            int j = graph->adj[i].data[k].to;       
            laplacian_matrix[i-1][j-1] -= 1.0;                          
            degree += 1.0;
        }
        laplacian_matrix[i-1][i-1] += degree;                         
    }
    return laplacian_matrix;
}


static void identity_matrix(double **matrix, int n) {
    for (int i = 0; i < n; i++) {
        memset(matrix[i], 0, n * sizeof(double));
        matrix[i][i] = 1.0;
    }
}

static void copy_matrix(double **source, double **destination, int n) {
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            destination[i][j] = source[i][j];
}

static void jacobi_eigen(double **symmetric_matrix_A, double **eigenvector_matrix_V, int n) {
    identity_matrix(eigenvector_matrix_V, n);
    int max_sweeps = 300;  
    int sweep = 0;

    while (sweep < max_sweeps) {
        double max_off_diagonal = 0.0;
        int pivot_row = 0;
        int pivot_col = 1;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (fabs(symmetric_matrix_A[i][j]) > max_off_diagonal) {
                    max_off_diagonal = fabs(symmetric_matrix_A[i][j]);
                    pivot_row = i;
                    pivot_col = j;
                }
            }
        }

        if (max_off_diagonal < 1e-10) break;  

        double a_pp = symmetric_matrix_A[pivot_row][pivot_row];
        double a_qq = symmetric_matrix_A[pivot_col][pivot_col];
        double a_pq = symmetric_matrix_A[pivot_row][pivot_col];

        double theta = 0.5 * atan2(2.0 * a_pq, a_qq - a_pp);
        double cos_theta = cos(theta);
        double sin_theta = sin(theta);

        symmetric_matrix_A[pivot_row][pivot_row] = cos_theta*cos_theta * a_pp - 2*cos_theta*sin_theta * a_pq + sin_theta*sin_theta * a_qq;
        symmetric_matrix_A[pivot_col][pivot_col] = sin_theta*sin_theta * a_pp + 2*cos_theta*sin_theta * a_pq + cos_theta*cos_theta * a_qq;

        symmetric_matrix_A[pivot_row][pivot_col] = 0.0;
        symmetric_matrix_A[pivot_col][pivot_row] = 0.0;

        for (int i = 0; i < n; i++) {
            if (i != pivot_row && i != pivot_col) {
                double a_ip = symmetric_matrix_A[i][pivot_row];
                double a_iq = symmetric_matrix_A[i][pivot_col];

                symmetric_matrix_A[i][pivot_row] = cos_theta * a_ip - sin_theta * a_iq;
                symmetric_matrix_A[i][pivot_col] = sin_theta * a_ip + cos_theta * a_iq;

                symmetric_matrix_A[pivot_row][i] = symmetric_matrix_A[i][pivot_row];
                symmetric_matrix_A[pivot_col][i] = symmetric_matrix_A[i][pivot_col];
            }
        }

        for (int i = 0; i < n; i++) {
            double v_ip = eigenvector_matrix_V[i][pivot_row];
            double v_iq = eigenvector_matrix_V[i][pivot_col];
            eigenvector_matrix_V[i][pivot_row] = cos_theta * v_ip - sin_theta * v_iq;
            eigenvector_matrix_V[i][pivot_col] = sin_theta * v_ip + cos_theta * v_iq;
        }

        sweep++;
    }
}

// debug
// void print_matrix(double **matrix, int n) {
//     printf("\n--- Macierz Laplace'a (%d x %d) ---\n", n, n);
    
//     printf("     ");
//     for (int j = 1; j <= n; j++) {
//         printf("[%d]      ", j);
//     }
//     printf("\n");

//     for (int i = 1; i <= n; i++) {
//         printf("[%d] ", i);
//         for (int j = 1; j <= n; j++) {
//             printf("%8.3f ", matrix[i-1][j-1]); 
//         }
//         printf("\n");
//     }
//     printf("------------------------------------\n\n");
// }


void free_matrix(double **matrix, int n) {
    if (matrix == NULL) return;
    for (int i = 0; i < n; i++) {
        free(matrix[i]);
    }
    free(matrix);
}

GraphLayout* solve_using_spectral_layout(Graph *graph)
{
    if (graph == NULL || graph->vertices_num < 2) {
        fprintf(stderr, "Błąd: Graf jest pusty lub ma za mało wierzchołków do spectral layout.\n");
        return NULL;
    }

    int n = graph->vertices_num;

    double **laplacian = create_laplacian_matrix(graph);
    // print_matrix(laplacian, n);                    // debug

    double **A = malloc(n * sizeof(double*));     // będzie miała wartości własne na diagonalu
    double **V = malloc(n * sizeof(double*));     // kolumny = wektory własne
    for (int i = 0; i < n; i++) {
        A[i] = malloc(n * sizeof(double));
        V[i] = malloc(n * sizeof(double));
    }

    copy_matrix(laplacian, A, n);
    identity_matrix(V, n);

    jacobi_eigen(A, V, n);

    // printf("\n--- Macierz A (wartości własne na diagonalu) ---\n");
    print_matrix(A, n);

    // printf("\n--- Macierz V (wektory własne w kolumnach) ---\n");
    print_matrix(V, n);


    int idx_lambda2 = -1, idx_lambda3 = -1;
    double smallest = 1e9, second = 1e9;

    for (int i = 0; i < n; i++) {
        double val = fabs(A[i][i]);               
        if (val < 1e-8) continue;                 

        if (val < smallest) {
            second = smallest;
            idx_lambda3 = idx_lambda2;
            smallest = val;
            idx_lambda2 = i;
        } else if (val < second) {
            second = val;
            idx_lambda3 = i;
        }
    }

    if (idx_lambda2 == -1 || idx_lambda3 == -1) {
        fprintf(stderr, "Blad: Nie znaleziono dwoch niezerowych wartosci wlasnych.\n");
        goto cleanup;
    }

    GraphLayout *layout = malloc(sizeof(GraphLayout));
    if (!layout) goto cleanup;

    layout->count = n;
    layout->nodes = malloc(n * sizeof(Node));
    if (!layout->nodes) {
        free(layout);
        goto cleanup;
    }

    const double SCALE = 150.0;

    for (int i = 0; i < n; i++) {
        layout->nodes[i].id = i + 1;
        layout->nodes[i].x = V[i][idx_lambda2] * SCALE;
        layout->nodes[i].y = V[i][idx_lambda3] * SCALE;
    }


cleanup:
    free_matrix(laplacian, n);
    for (int i = 0; i < n; i++) {
        free(A[i]);
        free(V[i]);
    }
    free(A);
    free(V);

    if (layout){return layout;}
    else{return NULL;}
}