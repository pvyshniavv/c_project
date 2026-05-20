package GUI_Project.integration;

import GUI_Project.model.FileFormat;
import GUI_Project.model.OutputGraph;
import GUI_Project.model.ProcessingConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CExternalProcessor implements GraphProcessor {
    private final String executablePath;

    public CExternalProcessor(String executablePath) {
        this.executablePath = executablePath;
    }

    @Override
    public OutputGraph processGraph(ProcessingConfig config) throws Exception {
        List<String> command = new ArrayList<String>();
        command.add(executablePath);

    if(config.getInputFileName() != null && !config.getInputFileName().trim().isEmpty()){
        command.add("-i");
        command.add(config.getInputFileName());
    }
    if(config.getOutputFileName() != null && !config.getOutputFileName().trim().isEmpty()){
        command.add("-o");
        command.add(config.getOutputFileName());
    }
    if(config.getOutputFileFormat() == FileFormat.TEXT) {
        command.add("-h");
    }
    else {
        command.add("-b");
    }

    command.add("-a");
    command.add(config.getAlgorithmUsed().name());

    ProcessBuilder processBuilder = new ProcessBuilder(command);

    processBuilder.directory(new File(System.getProperty("user.dir")));

    Process process = processBuilder.start();

    int exitCode = process.waitFor();

    if(exitCode != 0) {
        throw new Exception("C-program execution failed with the following error" + exitCode);
    }
        return null;
    }
}

