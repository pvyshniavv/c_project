package GUI_Project.presenter;

import GUI_Project.integration.GraphProcessor;
import GUI_Project.integration.ProcessingException;
import GUI_Project.io.GraphFileReader;
import GUI_Project.model.AlgorithmType;
import GUI_Project.model.Edge;
import GUI_Project.model.FileFormat;
import GUI_Project.model.Node;
import GUI_Project.model.OutputGraph;
import GUI_Project.model.ProcessingConfig;
import GUI_Project.view.MainFrame;
import GUI_Project.view.ToolbarPanel;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class GraphPresenter {

    private static final double DEFAULT_LAYOUT_RADIUS = 100.0;

    private final MainFrame view;
    private final GraphFileReader reader;
    private final GraphProcessor processor;

    /** State kept in the presenter. */
    private final List<Edge> currentEdges = new ArrayList<>();
    private final List<Node> currentNodes = new ArrayList<>();
    private Path currentEdgesPath;       // input file for the C module
    private FileFormat lastCoordsFormat = FileFormat.TEXT;
    private Path lastOutputPath;

    public GraphPresenter(MainFrame view, GraphFileReader reader, GraphProcessor processor) {
        this.view = view;
        this.reader = reader;
        this.processor = processor;
        bindEvents();
    }

    private void bindEvents() {
        view.addOpenEdgesListener(e -> openEdgesFile());
        view.addOpenCoordsTextListener(e -> openCoordinatesFile(FileFormat.TEXT));
        view.addOpenCoordsBinaryListener(e -> openCoordinatesFile(FileFormat.BINARY));

        ToolbarPanel toolbar = view.getToolbarPanel();
        toolbar.addPartitionListener(e -> partitionGraph());
        toolbar.addSaveListener(e -> saveSolution());
    }

    //loading: edges

    private void openEdgesFile() {
        File file = view.chooseOpenFile("Pliki wejściowe (krawędzie)", "txt", "csv");
        if (file == null) {
            return;
        }
        try {
            List<Edge> edges = reader.readEdges(file.toPath());
            applyEdges(edges);
            currentEdgesPath = file.toPath();
        } catch (NumberFormatException ex) {
            view.showError("Błędny format danych w pliku krawędzi:\n" + ex.getMessage());
        } catch (IOException ex) {
            view.showError("Błąd dostępu do pliku:\n" + ex.getMessage());
        }
    }

    /** Replaces the edges and gives each referenced node a default circle position. */
    private void applyEdges(List<Edge> edges) {
        currentEdges.clear();
        currentEdges.addAll(edges);

        Set<Integer> ids = new LinkedHashSet<>();
        for (Edge e : edges) {
            ids.add(e.getSourceId());
            ids.add(e.getTargetId());
        }
        currentNodes.clear();
        currentNodes.addAll(circleLayout(ids));
        refreshView();
    }

    /** Default circular layout — used until real coordinates arrive. */
    private List<Node> circleLayout(Set<Integer> ids) {
        List<Node> result = new ArrayList<>();
        int n = ids.size();
        if (n == 0) {
            return result;
        }
        int i = 0;
        for (Integer id : ids) {
            double angle = 2.0 * Math.PI * i / n;
            result.add(new Node(id,
                    DEFAULT_LAYOUT_RADIUS * Math.cos(angle),
                    DEFAULT_LAYOUT_RADIUS * Math.sin(angle)));
            i++;
        }
        return result;
    }

    // ---- loading: coordinates -------------------------------------------

    private void openCoordinatesFile(FileFormat format) {
        File file = (format == FileFormat.TEXT)
                ? view.chooseOpenFile("Współrzędne (tekst)", "txt")
                : view.chooseOpenFile("Współrzędne (binarne)", "bin", "dat");
        if (file == null) {
            return;
        }
        try {
            List<Node> coords = (format == FileFormat.TEXT)
                    ? reader.readCoordinatesText(file.toPath())
                    : reader.readCoordinatesBinary(file.toPath());

            applyCoordinates(coords);
            lastCoordsFormat = format;

        } catch (NumberFormatException ex) {
            view.showError("Błędny format danych: w pliku znajdują się "
                    + "wartości nieliczbowe.\n" + ex.getMessage());
        } catch (EOFException ex) {
            view.showError("Uszkodzony plik binarny: nieoczekiwany koniec pliku.");
        } catch (IOException ex) {
            view.showError("Błąd dostępu do pliku:\n" + ex.getMessage());
        }
    }

    /** Updates positions of existing nodes by id; adds new nodes for unknown ids. */
    private void applyCoordinates(List<Node> coords) {
        Map<Integer, Node> byId = new LinkedHashMap<>();
        for (Node n : currentNodes) {
            byId.put(n.getId(), n);
        }
        for (Node incoming : coords) {
            Node existing = byId.get(incoming.getId());
            if (existing != null) {
                existing.setX(incoming.getX());
                existing.setY(incoming.getY());
            } else {
                byId.put(incoming.getId(), incoming);
            }
        }
        currentNodes.clear();
        currentNodes.addAll(byId.values());
        refreshView();
    }

    // ---- refresh --------------------------------------------------------

    private void refreshView() {
        OutputGraph graph = new OutputGraph(
                new ArrayList<>(currentNodes),
                new ArrayList<>(currentEdges));
        view.getGraphPanel().setGraph(graph);

        // NullPointerException protection: save is enabled only when there is
        // something meaningful to save.
        view.getToolbarPanel().setSaveEnabled(!currentNodes.isEmpty());
    }

    // ---- processing -----------------------------------------------------

    private void partitionGraph() {
        if (currentEdges.isEmpty() || currentEdgesPath == null) {
            view.showError("Najpierw wczytaj plik wejściowy z krawędziami.");
            return;
        }

        ToolbarPanel toolbar = view.getToolbarPanel();

        int clusters;
        double margin;
        try {
            clusters = Integer.parseInt(toolbar.getClusterText());
            margin = Double.parseDouble(toolbar.getMarginText());
        } catch (NumberFormatException ex) {
            view.showError("Parametry podziału muszą być liczbami.");
            return;
        }

        if (clusters < 1) {
            view.showError("Liczba klastrów musi być większa od zera.");
            return;
        }
        if (margin < 0) {
            view.showError("Margines nie może być wartością ujemną.");
            return;
        }

        AlgorithmType algorithm = toolbar.getSelectedAlgorithm();
        FileFormat outputFormat = FileFormat.TEXT; // C module currently emits text
        Path outputPath = deriveOutputPath(currentEdgesPath);

        ProcessingConfig config = new ProcessingConfig();
        config.setInputPath(currentEdgesPath.toString());
        config.setOutputPath(outputPath.toString());
        config.setAlgorithm(algorithm);
        config.setClusterCount(clusters);
        config.setMargin(margin);
        config.setOutputFormat(outputFormat);

        try {
            processor.process(config);
            lastOutputPath = outputPath;
            lastCoordsFormat = outputFormat;

            List<Node> result = (outputFormat == FileFormat.TEXT)
                    ? reader.readCoordinatesText(outputPath)
                    : reader.readCoordinatesBinary(outputPath);

            applyCoordinates(result);
            view.showInfo("Podział zakończony pomyślnie.");

        } catch (ProcessingException ex) {
            view.showError("Moduł obliczeniowy zgłosił błąd:\n" + ex.getMessage());
        } catch (EOFException ex) {
            view.showError("Plik wynikowy modułu C jest uszkodzony.");
        } catch (NumberFormatException ex) {
            view.showError("Plik wynikowy zawiera nieprawidłowe dane.");
        } catch (IOException ex) {
            view.showError("Nie udało się odczytać pliku wynikowego:\n" + ex.getMessage());
        }
    }

    // ---- saving ---------------------------------------------------------

    private void saveSolution() {
        if (lastOutputPath == null) {
            view.showError("Brak rozwiązania do zapisania. Najpierw wykonaj podział.");
            return;
        }
        File target = view.chooseSaveFile();
        if (target == null) {
            return;
        }
        try {
            Files.copy(lastOutputPath, target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            view.showInfo("Zapisano rozwiązanie do:\n" + target.getAbsolutePath());
        } catch (IOException ex) {
            view.showError("Nie udało się zapisać pliku:\n" + ex.getMessage());
        }
    }

    private Path deriveOutputPath(Path input) {
        String name = input.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String base = (dot > 0) ? name.substring(0, dot) : name;
        String ext = (dot > 0) ? name.substring(dot) : "";
        return input.resolveSibling(base + "_output" + ext);
    }
}
