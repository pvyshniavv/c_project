package GUI_Project.model;

import java.util.ArrayList;
import java.util.List;

public class OutputGraph {
    private List<Node> nodes;
    private List<Edge> edges;

    public OutputGraph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public OutputGraph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }

    public void setNodes(List<Node> nodes) { this.nodes = nodes; }
    public void setEdges(List<Edge> edges) { this.edges = edges;}
}
