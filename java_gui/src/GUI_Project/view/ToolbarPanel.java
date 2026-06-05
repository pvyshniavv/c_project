package GUI_Project.view;

import GUI_Project.model.AlgorithmType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class ToolbarPanel extends JPanel {

    private final JComboBox<AlgorithmType> algorithmBox =
            new JComboBox<>(AlgorithmType.values());
    private final JTextField clusterField = new JTextField("2", 8);
    private final JTextField marginField = new JTextField("0.0", 8);
    private final JButton partitionButton = new JButton("Podziel graf");
    private final JButton saveButton = new JButton("Zapisz rozwiązanie");

    public ToolbarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(220, 0));

        add(labeled("Algorytm:", algorithmBox));
        add(Box.createVerticalStrut(8));
        add(labeled("Liczba klastrów:", clusterField));
        add(Box.createVerticalStrut(8));
        add(labeled("Margines:", marginField));
        add(Box.createVerticalStrut(16));
        add(partitionButton);
        add(Box.createVerticalStrut(8));
        add(saveButton);

        partitionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        return row;
    }


    public AlgorithmType getSelectedAlgorithm() {
        return (AlgorithmType) algorithmBox.getSelectedItem();
    }

    /** Raw text — parsing (and NumberFormatException handling) is the presenter's job. */
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

    public void setSaveEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }
}
