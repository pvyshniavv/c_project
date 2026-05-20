package GUI_Project.model;

public class Edge {
    private int sourceId;
    private int targetId;
    private double weight;
    private String edgeName;

    public Edge(String edgeName, int sourceId, int targetId, double weight) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
        this.edgeName = edgeName;
    }

    public int getSourceId() { return sourceId; }
    public int getTargetId() { return targetId;}
    public double getWeight() { return weight; }
    public String getEdgeName() { return edgeName; }

    public void setSourceId(int sourceId) { this.sourceId = sourceId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setEdgeName(String edgeName) { this.edgeName = edgeName; }
}
