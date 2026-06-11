package GUI_Project.integration;

import GUI_Project.io.GraphFileReader;
import GUI_Project.model.Edge;
import GUI_Project.model.OutputGraph;
import GUI_Project.model.ProcessingConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@link GraphProcessor} implementation that launches the compiled C module
 * through {@link ProcessBuilder} with the appropriate command-line flags.
 */
public class CExternalProcessor implements GraphProcessor {

    private static final String BINARY_EXTENSION = ".bin";
    private static final String DATA_EXTENSION = ".dat";

    /** Path to the compiled C executable (e.g. ../c_backend/graph_tool). */
    private final File executable;
    private final GraphFileReader graphFileReader;

    public CExternalProcessor(File executable) {
        this(executable, new GraphFileReader());
    }

    public CExternalProcessor(File executable, GraphFileReader graphFileReader) {
        this.executable = executable;
        this.graphFileReader = graphFileReader;
    }

    @Override
    public void process(ProcessingConfig config) throws ProcessingException {
        // --- Guard: the executable must exist before we try to launch it. ---
        if (executable == null || !executable.exists()) {
            throw new ProcessingException(
                    "Environment configuration error: C executable not found at "
                            + (executable == null ? "null" : executable.getAbsolutePath()));
        }

        Path preparedInput = null;

        try {
            preparedInput = prepareInputForC(config.getInputPath());
            List<String> command = buildCommand(config, preparedInput.toString());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true); // merge stderr into stdout

            Process process = builder.start();

            // Drain output so the child process never blocks on a full pipe.
            String output = readProcessOutput(process);

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ProcessingException(
                        "C module finished with exit code " + exitCode
                                + ". Output:\n" + output);
            }
        } catch (IOException e) {
            // start() failed — typically a broken path or permission issue.
            throw new ProcessingException(
                    "Failed to start the C module: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessingException("C module execution was interrupted.", e);
        } finally {
            deleteTemporaryInput(preparedInput, config.getInputPath());
        }
    }

    /** Builds the argument list passed to the C executable. */
    private List<String> buildCommand(ProcessingConfig config, String inputPath) {
        List<String> command = new ArrayList<>();
        command.add(executable.getAbsolutePath());
        command.add(config.getAlgorithm().getFlag());
        command.add("--input");
        command.add(inputPath);
        command.add("--output");
        command.add(config.getOutputPath());
        command.add("--parts");
        command.add(String.valueOf(config.getClusterCount()));
        command.add("--margin");
        command.add(String.valueOf(config.getMargin()));
        command.add("--format");
        command.add(config.getOutputFormat().name().toLowerCase());
        return command;
    }

    private Path prepareInputForC(String inputPath) throws IOException {
        Path input = Path.of(inputPath);
        if (!isBinaryGraphFile(input)) {
            return input;
        }

        OutputGraph graph = graphFileReader.readBinaryGraph(input);
        Path textInput = Files.createTempFile("graph-input-", ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(textInput, StandardCharsets.UTF_8)) {
            for (Edge edge : graph.getEdges()) {
                writer.write(edge.getName());
                writer.write(' ');
                writer.write(Integer.toString(edge.getSourceId()));
                writer.write(' ');
                writer.write(Integer.toString(edge.getTargetId()));
                writer.write(' ');
                writer.write(String.format(Locale.US, "%.17g", edge.getWeight()));
                writer.newLine();
            }
        }
        return textInput;
    }

    private boolean isBinaryGraphFile(Path input) {
        String fileName = input.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(BINARY_EXTENSION) || fileName.endsWith(DATA_EXTENSION);
    }

    private void deleteTemporaryInput(Path preparedInput, String originalInputPath) {
        if (preparedInput == null || preparedInput.equals(Path.of(originalInputPath))) {
            return;
        }
        try {
            Files.deleteIfExists(preparedInput);
        } catch (IOException ignored) {
            // Best-effort cleanup: processing result is more important than a temp file.
        }
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
