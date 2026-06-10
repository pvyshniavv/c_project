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

/**
 * Main application window. Hosts the menu bar (file selection), the central
 * {@link GraphPanel} and the side {@link ToolbarPanel}. Passive view: it
 * exposes hooks and dialogs but contains no application logic.
 * <p>
 * The "Plik" menu offers three loading paths:
 * <ul>
 *   <li>A text edges file (typical C input) — graph topology.</li>
 *   <li>A text coordinates file (typical C output) — node positions.</li>
 *   <li>A binary graph file from the C module — both topology and positions
 *       in a single file.</li>
 * </ul>
 */
public class MainFrame extends JFrame {

    private final GraphPanel graphPanel = new GraphPanel();
    private final ToolbarPanel toolbarPanel = new ToolbarPanel();

    private final JMenuItem openEdgesItem =
            new JMenuItem("Wczytaj plik wejściowy (krawędzie)...");
    private final JMenuItem openCoordsTextItem =
            new JMenuItem("Wczytaj współrzędne (tekst)...");
    private final JMenuItem openBinaryGraphItem =
            new JMenuItem("Wczytaj graf binarny (z modułu C)...");

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
        fileMenu.add(openCoordsTextItem);
        fileMenu.addSeparator();
        fileMenu.add(openBinaryGraphItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    // ---- accessors for the presenter ------------------------------------

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

    public void addOpenBinaryGraphListener(ActionListener listener) {
        openBinaryGraphItem.addActionListener(listener);
    }

    // ---- dialogs --------------------------------------------------------

    /** Opens a file chooser and returns the selected file, or {@code null}. */
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
