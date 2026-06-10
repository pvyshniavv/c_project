package GUI_Project;

import GUI_Project.io.GraphFileReader;
import GUI_Project.presenter.GraphPresenter;
import GUI_Project.view.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Hands control to the Event Dispatch Thread (EDT)
 * and wires the MVP triad together.
 * <p>
 * Note: the {@code integration} package (containing {@code GraphProcessor} and
 * {@code CExternalProcessor}) is part of the documented architecture and is
 * kept in the codebase for future direct integration with the compiled C
 * module. The current presenter performs partitioning in pure Java
 * (K-means by node position), so no external executable is required to run
 * the application.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        MainFrame frame = new MainFrame();
        GraphFileReader reader = new GraphFileReader();
        new GraphPresenter(frame, reader); // binds itself to view events
        frame.setVisible(true);
    }
}
