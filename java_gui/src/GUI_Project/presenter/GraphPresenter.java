package GUI_Project.presenter;

import GUI_Project.io.GraphFileReader;
import GUI_Project.model.Edge;
import GUI_Project.model.Node;
import GUI_Project.model.OutputGraph;
import GUI_Project.view.MainFrame;
import GUI_Project.view.ToolbarPanel;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Central connector of the system (MVP "Presenter").
 * <p>
 * Captures events from the View, initiates file reads through the IO package,
 * runs the partitioning (Java K-means by default), and pushes the resulting
 * {@link OutputGraph} back to the View. All critical operations are wrapped in
 * try-catch here so the application never crashes and the user always sees a
 * readable message.
 */
public class GraphPresenter {

    private static final double DEFAULT_LAYOUT_RADIUS = 100.0;

    private final MainFrame view;
    private final GraphFileReader reader;
    private final KMeansPartitioner partitioner = new KMeansPartitioner();

    private final List<Edge> currentEdges = new ArrayList<>();
    private final List<Node> currentNodes = new ArrayList<>();

    public GraphPresenter(MainFrame view, GraphFileReader reader) {
        this.view = view;
        this.reader = reader;
        bindEvents();
    }

    private void bindEvents() {
        view.addOpenEdgesListener(e -> openEdgesFile());
        view.addOpenCoordsTextListener(e -> openCoordinatesTextFile());
        view.addOpenBinaryGraphListener(e -> openBinaryGraphFile());

        ToolbarPanel toolbar = view.getToolbarPanel();
        toolbar.addPartitionListener(e -> partitionGraph());
        toolbar.addSaveListener(e -> saveSolution());
        toolbar.addResetViewListener(e -> view.getGraphPanel().resetView());
    }

    // ---- loading: text edges --------------------------------------------

    private void openEdgesFile() {
        File file = view.chooseOpenFile("Pliki wejściowe (krawędzie)", "txt", "csv");
        if (file == null) {
            return;
        }
        try {
            List<Edge> edges = reader.readEdges(file.toPath());
            applyEdges(edges);
        } catch (NumberFormatException ex) {
            view.showError("Błędny format danych w pliku krawędzi:\n" + ex.getMessage());
        } catch (IOException ex) {
            view.showError("Błąd dostępu do pliku:\n" + ex.getMessage());
        }
    }

    /**
     * Replaces the edges. Node positions are preserved for ids that already
     * existed; new ids get a position on a default circle layout. Cluster
     * assignments are cleared because the topology has changed.
     */
    private void applyEdges(List<Edge> edges) {
        currentEdges.clear();
        currentEdges.addAll(edges);

        Map<Integer, Node> previous = new LinkedHashMap<>();
        for (Node n : currentNodes) {
            previous.put(n.getId(), n);
        }

        Set<Integer> ids = new LinkedHashSet<>();
        for (Edge e : edges) {
            ids.add(e.getSourceId());
            ids.add(e.getTargetId());
        }

        List<Node> next = new ArrayList<>();
        List<Integer> freshIds = new ArrayList<>();
        for (Integer id : ids) {
            Node prev = previous.get(id);
            if (prev != null) {
                next.add(prev);
            } else {
                freshIds.add(id);
            }
        }
        int total = ids.size();
        int startIndex = next.size();
        for (int i = 0; i < freshIds.size(); i++) {
            double angle = 2.0 * Math.PI * (startIndex + i) / total;
            next.add(new Node(freshIds.get(i),
                    DEFAULT_LAYOUT_RADIUS * Math.cos(angle),
                    DEFAULT_LAYOUT_RADIUS * Math.sin(angle)));
        }

        currentNodes.clear();
        currentNodes.addAll(next);
        clearClusters();
        refreshView();
    }

    // ---- loading: text coordinates --------------------------------------

    private void openCoordinatesTextFile() {
        File file = view.chooseOpenFile("Współrzędne (tekst)", "txt");
        if (file == null) {
            return;
        }
        try {
            List<Node> coords = reader.readCoordinatesText(file.toPath());
            applyCoordinates(coords);
        } catch (NumberFormatException ex) {
            view.showError("Błędny format danych: w pliku znajdują się "
                    + "wartości nieliczbowe.\n" + ex.getMessage());
        } catch (IOException ex) {
            view.showError("Błąd dostępu do pliku:\n" + ex.getMessage());
        }
    }

    /** Updates positions by id; adds new nodes for unknown ids; clears clusters. */
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
        clearClusters();
        refreshView();
    }

    // ---- loading: binary graph ------------------------------------------

    private void openBinaryGraphFile() {
        File file = view.chooseOpenFile("Pliki binarne grafu (z modułu C)", "bin", "dat");
        if (file == null) {
            return;
        }
        try {
            OutputGraph graph = reader.readBinaryGraph(file.toPath());
            applyBinaryGraph(graph);
        } catch (EOFException ex) {
            view.showError("Uszkodzony plik binarny: " + ex.getMessage());
        } catch (IOException ex) {
            view.showError("Błąd dostępu do pliku:\n" + ex.getMessage());
        }
    }

    /**
     * Replaces both the topology AND the positions with the contents of the
     * binary file. Cluster assignments are reset (new positions invalidate
     * any previous partition).
     */
    private void applyBinaryGraph(OutputGraph graph) {
        currentEdges.clear();
        currentEdges.addAll(graph.getEdges());

        currentNodes.clear();
        for (Node n : graph.getNodes()) {
            // Build fresh Node instances so internal mutation (cluster, x, y)
            // stays isolated from the immutable lists exposed by OutputGraph.
            currentNodes.add(new Node(n.getId(), n.getX(), n.getY()));
        }
        refreshView();
    }

    private void clearClusters() {
        for (Node n : currentNodes) {
            n.setCluster(Node.UNASSIGNED);
        }
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

    // ---- partition (Java K-means) ---------------------------------------

    private void partitionGraph() {
        if (currentNodes.isEmpty()) {
            view.showError("Najpierw wczytaj graf "
                    + "(plik krawędzi i/lub współrzędnych albo plik binarny).");
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
        if (clusters > currentNodes.size()) {
            view.showError("Liczba klastrów (" + clusters
                    + ") nie może być większa niż liczba węzłów ("
                    + currentNodes.size() + ").");
            return;
        }

        int[] assignment = partitioner.partition(currentNodes, clusters, margin);
        for (int i = 0; i < currentNodes.size(); i++) {
            currentNodes.get(i).setCluster(assignment[i]);
        }
        refreshView();
        view.showInfo("Podział zakończony pomyślnie.\nLiczba klastrów: " + clusters);
    }

    // ---- saving ---------------------------------------------------------

    private void saveSolution() {
        if (currentNodes.isEmpty()) {
            view.showError("Brak danych do zapisania.");
            return;
        }
        File target = view.chooseSaveFile();
        if (target == null) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(target.toPath())) {
            writer.write("# Wynik aplikacji GUI_Project");
            writer.newLine();
            writer.write("# Algorytm: " + view.getToolbarPanel().getSelectedAlgorithm());
            writer.newLine();
            writer.write("# Liczba wezlow: " + currentNodes.size()
                    + ", liczba krawedzi: " + currentEdges.size());
            writer.newLine();
            writer.newLine();

            writer.write("# Wezly: id x y cluster");
            writer.newLine();
            for (Node n : currentNodes) {
                writer.write(String.format(Locale.US, "%d %.6f %.6f %d",
                        n.getId(), n.getX(), n.getY(), n.getCluster()));
                writer.newLine();
            }
            writer.newLine();

            writer.write("# Krawedzie: nazwa source target waga");
            writer.newLine();
            for (Edge e : currentEdges) {
                writer.write(String.format(Locale.US, "%s %d %d %.3f",
                        e.getName(), e.getSourceId(), e.getTargetId(), e.getWeight()));
                writer.newLine();
            }
            view.showInfo("Zapisano rozwiązanie do:\n" + target.getAbsolutePath());

        } catch (IOException ex) {
            view.showError("Nie udało się zapisać pliku:\n" + ex.getMessage());
        }
    }
}
