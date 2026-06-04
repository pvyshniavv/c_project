package GUI_Project.view;

import GUI_Project.model.OutputGraph;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;


import static javax.swing.text.StyleConstants.setBackground;

public class GraphPanel extends JPanel {
    private static final int NODE_RADIUS = 6;
    private static final int PADDING = 40;

    private OutputGraph graph;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(640, 480));
    }
    /** Sets the graph to display and triggers a repaint. */
    public void setGraph(OutputGraph graph) {
        this.graph = graph;
        repaint();
    }

    public void clear() {
        this.graph = null;
        repaint();
    }

}
