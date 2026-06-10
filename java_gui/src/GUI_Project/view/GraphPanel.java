package GUI_Project.view;

import GUI_Project.model.Edge;
import GUI_Project.model.Node;
import GUI_Project.model.OutputGraph;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom component that renders the graph. Supports:
 * <ul>
 *   <li>auto-fit of the graph to the panel on the first draw,</li>
 *   <li>zoom-around-cursor with the mouse wheel,</li>
 *   <li>pan with left-button drag,</li>
 *   <li>double-click (or external "reset") to refit,</li>
 *   <li>coloring of nodes and intra-cluster edges by cluster index.</li>
 * </ul>
 * The panel is passive — it only renders the graph it is given.
 */
public class GraphPanel extends JPanel {

    private static final int NODE_RADIUS = 6;
    private static final int PADDING = 40;
    private static final double MIN_SCALE = 0.05;
    private static final double MAX_SCALE = 50.0;
    private static final double ZOOM_STEP = 1.15;

    /** Color palette for cluster indices (wraps around with modulo). */
    private static final Color[] CLUSTER_COLORS = {
            new Color( 52, 120, 246), // blue
            new Color(232,  80,  80), // red
            new Color( 76, 175,  80), // green
            new Color(255, 152,   0), // orange
            new Color(156,  39, 176), // purple
            new Color(  0, 188, 212), // cyan
            new Color(255, 193,   7), // amber
            new Color(121,  85,  72)  // brown
    };
    private static final Color DEFAULT_NODE_COLOR = CLUSTER_COLORS[0];
    private static final Color DEFAULT_EDGE_COLOR = new Color(150, 150, 150);

    private OutputGraph graph;

    /** User zoom factor on top of the auto-fit. */
    private double scale = 1.0;
    /** User pan in screen pixels. */
    private double offsetX = 0.0;
    private double offsetY = 0.0;

    private Point lastDragPoint;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(640, 480));
        installInteractionListeners();
    }

    public void setGraph(OutputGraph graph) {
        this.graph = graph;
        repaint();
    }

    public void clear() {
        this.graph = null;
        repaint();
    }

    /** Reset zoom and pan to the auto-fit view. */
    public void resetView() {
        scale = 1.0;
        offsetX = 0.0;
        offsetY = 0.0;
        repaint();
    }

    //interaction

    private void installInteractionListeners() {
        addMouseWheelListener(new MouseWheelListener() {
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getPreciseWheelRotation() < 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
                double oldScale = scale;
                double newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale * factor));
                if (newScale == oldScale) {
                    return;
                }
                // Zoom around the cursor: the point under the mouse stays put.
                double mx = e.getX();
                double my = e.getY();
                offsetX = mx - (mx - offsetX) * (newScale / oldScale);
                offsetY = my - (my - offsetY) * (newScale / oldScale);
                scale = newScale;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lastDragPoint = e.getPoint();
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    resetView();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (lastDragPoint == null) {
                    return;
                }
                offsetX += e.getX() - lastDragPoint.x;
                offsetY += e.getY() - lastDragPoint.y;
                lastDragPoint = e.getPoint();
                repaint();
            }
        });
    }

    // ---- rendering ------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.isEmpty()) {
            drawHint(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Bounds bounds = computeBounds(graph);
            Map<Integer, double[]> screenPos = computeScreenPositions(graph, bounds);
            Map<Integer, Integer> clusterById = new HashMap<>();
            for (Node n : graph.getNodes()) {
                clusterById.put(n.getId(), n.getCluster());
            }

            drawEdges(g2, screenPos, clusterById);
            drawNodes(g2, screenPos);
        } finally {
            g2.dispose();
        }
    }

    private void drawHint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(120, 120, 120));
        g2.setFont(getFont().deriveFont(Font.ITALIC, 12f));
        String text = "Wczytaj plik krawędzi i/lub plik współrzędnych z menu Plik.";
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text);
        g2.drawString(text, (getWidth() - w) / 2, getHeight() / 2);
    }

    private void drawEdges(Graphics2D g2, Map<Integer, double[]> pos,
                           Map<Integer, Integer> clusterById) {
        g2.setStroke(new BasicStroke(1.3f));
        for (Edge edge : graph.getEdges()) {
            double[] from = pos.get(edge.getSourceId());
            double[] to = pos.get(edge.getTargetId());
            if (from == null || to == null) {
                continue;
            }
            Integer srcC = clusterById.get(edge.getSourceId());
            Integer dstC = clusterById.get(edge.getTargetId());
            if (srcC != null && srcC >= 0 && srcC.equals(dstC)) {
                g2.setColor(clusterColor(srcC));
            } else {
                g2.setColor(DEFAULT_EDGE_COLOR);
            }
            g2.draw(new Line2D.Double(from[0], from[1], to[0], to[1]));
        }
    }

    private void drawNodes(Graphics2D g2, Map<Integer, double[]> pos) {
        g2.setStroke(new BasicStroke(1f));
        for (Node node : graph.getNodes()) {
            double[] p = pos.get(node.getId());
            if (p == null) {
                continue;
            }
            Color fill = (node.getCluster() >= 0)
                    ? clusterColor(node.getCluster()) : DEFAULT_NODE_COLOR;
            Ellipse2D circle = new Ellipse2D.Double(
                    p[0] - NODE_RADIUS, p[1] - NODE_RADIUS,
                    NODE_RADIUS * 2.0, NODE_RADIUS * 2.0);
            g2.setColor(fill);
            g2.fill(circle);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(circle);
        }
    }

    private Color clusterColor(int cluster) {
        return CLUSTER_COLORS[Math.floorMod(cluster, CLUSTER_COLORS.length)];
    }

    /**
     * Maps graph coordinates to screen pixels. First applies an auto-fit
     * (center + scale-to-fit), then the user's zoom and pan. Node radii and
     * edge stroke widths are NOT affected by the user zoom — only positions —
     * so dots and lines stay readable at any zoom level.
     */
    private Map<Integer, double[]> computeScreenPositions(OutputGraph graph, Bounds b) {
        Map<Integer, double[]> result = new HashMap<>();
        double width = Math.max(1, getWidth() - 2 * PADDING);
        double height = Math.max(1, getHeight() - 2 * PADDING);
        double spanX = (b.maxX - b.minX) <= 0 ? 1 : (b.maxX - b.minX);
        double spanY = (b.maxY - b.minY) <= 0 ? 1 : (b.maxY - b.minY);
        double fit = Math.min(width / spanX, height / spanY);

        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        double midX = (b.minX + b.maxX) / 2.0;
        double midY = (b.minY + b.maxY) / 2.0;

        for (Node node : graph.getNodes()) {
            double baseX = centerX + (node.getX() - midX) * fit;
            double baseY = centerY + (node.getY() - midY) * fit;
            double screenX = baseX * scale + offsetX;
            double screenY = baseY * scale + offsetY;
            result.put(node.getId(), new double[]{screenX, screenY});
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
