package GUI_Project.model;

public class Node {
    private int id;
    private double x;
    private double y;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setId(int id) { this.id = id; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }
}
