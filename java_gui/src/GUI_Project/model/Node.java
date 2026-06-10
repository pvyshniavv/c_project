package GUI_Project.model;

/**
 * Vertex model. Stores an identifier, Cartesian coordinates (X, Y), and a
 * cluster index assigned by the partitioning step ({@link #UNASSIGNED} until
 * partitioning runs).
 */
public class Node {

    /** Sentinel value meaning the node has no cluster yet. */
    public static final int UNASSIGNED = -1;

    private final int id;
    private double x;
    private double y;
    private int cluster = UNASSIGNED;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getCluster() { return cluster; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setCluster(int cluster) { this.cluster = cluster; }

    @Override
    public String toString() {
        return "Node{id=" + id + ", x=" + x + ", y=" + y + ", cluster=" + cluster + '}';
    }
}
