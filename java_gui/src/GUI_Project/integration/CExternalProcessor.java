package GUI_Project.integration;

import GUI_Project.model.ProcessingConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link GraphProcessor} implementation that launches the compiled C module
 * through {@link ProcessBuilder} with the appropriate command-line flags.
 */
public class CExternalProcessor implements GraphProcessor {

    /** Path to the compiled C executable (e.g. ../c_backend/graph_tool). */
    private final File executable;

    public CExternalProcessor(File executable) {
        this.executable = executable;
    }

    @Override
    public void process(ProcessingConfig config) throws ProcessingException {
        // --- Guard: the executable must exist before we try to launch it. ---
        if (executable == null || !executable.exists()) {
            throw new ProcessingException(
                    "Environment configuration error: C executable not found at "
                            + (executable == null ? "null" : executable.getAbsolutePath()));
        }

        List<String> command = buildCommand(config);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true); // merge stderr into stdout

        try {
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
        }
    }

    /** Builds the argument list passed to the C executable. */
    private List<String> buildCommand(ProcessingConfig config) {
        List<String> command = new ArrayList<>();
        command.add(executable.getAbsolutePath());
        command.add(config.getAlgorithm().getFlag());
        command.add("--input");
        command.add(config.getInputPath());
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
