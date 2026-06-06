package GUI_Project.model;

/**
 * Edge model. Stores the identifiers of the two endpoint nodes,
 * an edge name and a weight. Pure data holder.
 */
public class Edge {

    private final int sourceId;
    private final int targetId;
    private final String name;
    private final double weight;

    public Edge(int sourceId, int targetId, String name, double weight) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.name = name;
        this.weight = weight;
    }
    public int getSourceId() {
        return sourceId;
    }
    public int getTargetId() {
        return targetId;
    }
    public String getName() {
        return name;
    }
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{" + sourceId + "->" + targetId
                + ", name='" + name + "', weight=" + weight + '}';
    }
}
