package GUI_Project.model;

/**
 * Vertex model. Stores an identifier and Cartesian coordinates (X, Y).
 * Pure data holder — contains no business logic.
 */
public class Node {

    private final int id;
    private double x;
    private double y;
    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public int getId() {
        return id;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{id=" + id + ", x=" + x + ", y=" + y + '}';
    }
}
