package GUI_Project.view;

import GUI_Project.model.Edge;
import GUI_Project.model.Node;
import GUI_Project.model.OutputGraph;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class GraphPanel extends JPanel {

    private static final int NODE_RADIUS = 6;
    private static final int PADDING = 40; // Empty space around the graph

    private OutputGraph graph;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(640, 480));
    }
    public void setGraph(OutputGraph graph) {
        this.graph = graph;
        repaint();
    }

    public void clear() {
        this.graph = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Bounds bounds = computeBounds(graph);
            Map<Integer, double[]> screenPos = mapToScreen(graph, bounds);
            drawEdges(g2, screenPos);
            drawNodes(g2, screenPos);
        } finally {
            g2.dispose();
        }
    }

    private void drawEdges(Graphics2D g2, Map<Integer, double[]> pos) {
        g2.setColor(new Color(120, 120, 120));
        g2.setStroke(new BasicStroke(1.2f));
        for (Edge edge : graph.getEdges()) {
            double[] from = pos.get(edge.getSourceId());
            double[] to = pos.get(edge.getTargetId());
            if (from != null && to != null) {
                g2.draw(new Line2D.Double(from[0], from[1], to[0], to[1]));
            }
        }
    }

    private void drawNodes(Graphics2D g2, Map<Integer, double[]> pos) {
        for (Node node : graph.getNodes()) {
            double[] p = pos.get(node.getId());
            if (p == null) {
                continue;

            }
            Ellipse2D circle = new Ellipse2D.Double(
                    p[0] - NODE_RADIUS, p[1] - NODE_RADIUS,
                    NODE_RADIUS * 2.0, NODE_RADIUS * 2.0);
            g2.setColor(new Color(52, 120, 246));
            g2.fill(circle);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(circle);
        }
    }

    private Map<Integer, double[]> mapToScreen(OutputGraph graph, Bounds b) {
        Map<Integer, double[]> result = new HashMap<>();

        double width = Math.max(1, getWidth() - 2 * PADDING);
        double height = Math.max(1, getHeight() - 2 * PADDING);

        double spanX = (b.maxX - b.minX) == 0 ? 1 : (b.maxX - b.minX);
        double spanY = (b.maxY - b.minY) == 0 ? 1 : (b.maxY - b.minY);
        double scale = Math.min(width / spanX, height / spanY);

        for (Node node : graph.getNodes()) {
            double sx = PADDING + (node.getX() - b.minX) * scale;
            double sy = PADDING + (node.getY() - b.minY) * scale;
            result.put(node.getId(), new double[]{sx, sy});
        }
        return result;
    }
    private Bounds computeBounds(OutputGraph graph) {
        Bounds b = new Bounds();
        for (Node node : graph.getNodes()) {
            b.minX = Math.min(b.minX, node.getX());
            b.maxX = Math.max(b.maxX, node.getX());
            b.minY = Math.min(b.minY, node.getY());
            b.maxY = Math.max(b.maxY, node.getY());
        }
        return b;
    }
    private static final class Bounds {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
    }
}
