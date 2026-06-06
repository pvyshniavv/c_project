package GUI_Project;

import GUI_Project.integration.CExternalProcessor;
import GUI_Project.integration.GraphProcessor;
import GUI_Project.io.GraphFileReader;
import GUI_Project.presenter.GraphPresenter;
import GUI_Project.view.MainFrame;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * Application entry point. Hands control to the Event Dispatch Thread (EDT)
 * and creates the presentation-layer objects, wiring the MVP triad together.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }
    private static void startApplication() {
        // Path to the compiled C module. Adjust to your build output, e.g.
        // ../c_backend/graph_tool  (or graph_tool.exe on Windows).
        File cExecutable = new File("../c_backend/graph_tool");

        MainFrame frame = new MainFrame();
        GraphFileReader reader = new GraphFileReader();
        GraphProcessor processor = new CExternalProcessor(cExecutable);

        // The presenter binds itself to the view's events in its constructor.
        new GraphPresenter(frame, reader, processor);

        frame.setVisible(true);

    }
}
