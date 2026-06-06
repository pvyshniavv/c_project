package GUI_Project.integration;

import GUI_Project.model.ProcessingConfig;

public interface GraphProcessor {

    void process(ProcessingConfig config) throws ProcessingException;
}
