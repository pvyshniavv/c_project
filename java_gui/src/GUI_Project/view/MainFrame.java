package GUI_Project.view;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFrame extends JFrame {

    private final GraphPanel graphPanel = new GraphPanel();
    private final ToolbarPanel toolbarPanel = new ToolbarPanel();

    private final JMenuItem openEdgesItem = new JMenuItem("Wczytaj plik wejściowy (krawędzie)...");
    private final JMenuItem openCoordsTextItem = new JMenuItem("Wczytaj współrzędne (tekst)...");
    private final JMenuItem openCoordsBinaryItem = new JMenuItem("Wczytaj współrzędne (binarny)...");

    public MainFrame() {
        super("Narzędzie do pozycjonowania grafów");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());
        add(graphPanel, BorderLayout.CENTER);
        add(toolbarPanel, BorderLayout.EAST);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Plik");
        fileMenu.add(openEdgesItem);
        fileMenu.addSeparator();
        fileMenu.add(openCoordsTextItem);
        fileMenu.add(openCoordsBinaryItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public ToolbarPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public void addOpenEdgesListener(ActionListener listener) {
        openEdgesItem.addActionListener(listener);
    }

    public void addOpenCoordsTextListener(ActionListener listener) {
        openCoordsTextItem.addActionListener(listener);
    }

    public void addOpenCoordsBinaryListener(ActionListener listener) {
        openCoordsBinaryItem.addActionListener(listener);
    }

    public File chooseOpenFile(String description, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        if (extensions.length > 0) {
            chooser.setFileFilter(new FileNameExtensionFilter(description, extensions));
        }
        int result = chooser.showOpenDialog(this);
        return (result == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
    }

    public File chooseSaveFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        return (result == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informacja",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
