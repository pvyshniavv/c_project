package GUI_Project.presenter;

import GUI_Project.model.Node;

import java.util.List;

/**
 * K-means clustering in 2D over node positions. Each node receives a cluster
 * index in {@code [0, k)}. Used by the presenter to partition the graph
 * without calling the external C module.
 * <p>
 * Initialization picks {@code k} nodes evenly spaced by index (deterministic).
 * The iteration stops when no centroid moves by more than {@code tolerance}
 * or after a fixed safety cap.
 */
final class KMeansPartitioner {

    private static final int MAX_ITERATIONS = 200;

    /**
     * @param nodes      points to cluster (uses {@link Node#getX()} and
     *                   {@link Node#getY()})
     * @param k          number of clusters; if {@code k > nodes.size()} it is
     *                   silently capped
     * @param tolerance  stopping tolerance — iteration ends when the largest
     *                   centroid displacement between steps is &le; this value
     * @return assignment array where {@code assignment[i]} is the cluster index
     *         of {@code nodes.get(i)}
     */
    int[] partition(List<Node> nodes, int k, double tolerance) {
        final int n = nodes.size();
        if (n == 0 || k < 1) {
            return new int[0];
        }
        if (k > n) {
            k = n;
        }

        // --- Initial centroids: nodes at evenly spaced indices. ---
        double[][] centroids = new double[k][2];
        for (int i = 0; i < k; i++) {
            Node seed = nodes.get(i * n / k);
            centroids[i][0] = seed.getX();
            centroids[i][1] = seed.getY();
        }

        int[] assignment = new int[n];

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            // --- Step 1: assign each node to the nearest centroid. ---
            for (int i = 0; i < n; i++) {
                Node node = nodes.get(i);
                int best = 0;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < k; c++) {
                    double dx = node.getX() - centroids[c][0];
                    double dy = node.getY() - centroids[c][1];
                    double d2 = dx * dx + dy * dy;
                    if (d2 < bestDist) {
                        bestDist = d2;
                        best = c;
                    }
                }
                assignment[i] = best;
            }

            // --- Step 2: recompute centroids; track max displacement. ---
            double[][] next = new double[k][2];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                int c = assignment[i];
                next[c][0] += nodes.get(i).getX();
                next[c][1] += nodes.get(i).getY();
                counts[c]++;
            }
            double maxMove = 0;
            for (int c = 0; c < k; c++) {
                if (counts[c] > 0) {
                    next[c][0] /= counts[c];
                    next[c][1] /= counts[c];
                } else {
                    // Empty cluster — keep its previous position so the
                    // algorithm does not blow up.
                    next[c][0] = centroids[c][0];
                    next[c][1] = centroids[c][1];
                }
                double dx = next[c][0] - centroids[c][0];
                double dy = next[c][1] - centroids[c][1];
                double move = Math.sqrt(dx * dx + dy * dy);
                if (move > maxMove) {
                    maxMove = move;
                }
            }
            centroids = next;

            if (maxMove <= tolerance) {
                break;
            }
        }
        return assignment;
    }
}
