package GUI_Project.view;

import GUI_Project.model.AlgorithmType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

/**
 * Side panel containing the interactive controls. The "Liczba klastrów" and
 * "Margines" entries are TEXT FIELDS — values typed here are read by the
 * "Podziel graf" button. The reset-view button refits the graph to the panel
 * (same as double-clicking on it).
 */
public class ToolbarPanel extends JPanel {

    private final JComboBox<AlgorithmType> algorithmBox =
            new JComboBox<>(AlgorithmType.values());
    private final JTextField clusterField = new JTextField("2", 8);
    private final JTextField marginField = new JTextField("0.01", 8);
    private final JButton partitionButton = new JButton("Podziel graf");
    private final JButton saveButton = new JButton("Zapisz rozwiązanie");
    private final JButton resetViewButton = new JButton("Resetuj widok");

    public ToolbarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(240, 0));

        clusterField.setToolTipText("Liczba klastrów K-means (liczba całkowita ≥ 1)");
        marginField.setToolTipText("Tolerancja zatrzymania K-means (≥ 0); 0 = pełna zbieżność");
        partitionButton.setToolTipText("Pogrupuj węzły metodą K-means po pozycjach");
        saveButton.setToolTipText("Zapisz węzły, krawędzie i przypisania klastrów do pliku");
        resetViewButton.setToolTipText("Dopasuj graf do okna (lub kliknij dwukrotnie na grafie)");

        add(labeled("Algorytm:", algorithmBox));
        add(Box.createVerticalStrut(8));
        add(labeled("Liczba klastrów:", clusterField));
        add(Box.createVerticalStrut(8));
        add(labeled("Margines:", marginField));
        add(Box.createVerticalStrut(16));
        add(partitionButton);
        add(Box.createVerticalStrut(8));
        add(saveButton);
        add(Box.createVerticalStrut(16));
        add(resetViewButton);
        add(Box.createVerticalGlue());

        for (JComponent c : new JComponent[]{partitionButton, saveButton, resetViewButton}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }

        // NullPointerException protection: save stays disabled until something
        // is loaded into memory.
        saveButton.setEnabled(false);
    }

    private JPanel labeled(String text, Component field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(field);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    // ---- accessors used by the presenter --------------------------------

    public AlgorithmType getSelectedAlgorithm() {
        return (AlgorithmType) algorithmBox.getSelectedItem();
    }

    public String getClusterText() {
        return clusterField.getText().trim();
    }

    public String getMarginText() {
        return marginField.getText().trim();
    }

    public void addPartitionListener(ActionListener listener) {
        partitionButton.addActionListener(listener);
    }

    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void addResetViewListener(ActionListener listener) {
        resetViewButton.addActionListener(listener);
    }

    public void setSaveEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }
}
