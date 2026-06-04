package GUI_Project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container grouping the lists of {@link Node} and {@link Edge}
 * objects into a single graph structure.
 */
public class OutputGraph {

    private final List<Node> nodes;
    private final List<Edge> edges;

    public OutputGraph(List<Node> nodes, List<Edge> edges) {
        this.nodes = (nodes != null) ? nodes : new ArrayList<>();
        this.edges = (edges != null) ? edges : new ArrayList<>();
    }

    /** Returns an unmodifiable view of the nodes. */
    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /** Returns an unmodifiable view of the edges. */
    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
