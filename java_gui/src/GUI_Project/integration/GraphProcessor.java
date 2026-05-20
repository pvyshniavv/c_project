package GUI_Project.integration;

import GUI_Project.model.OutputGraph;
import GUI_Project.model.ProcessingConfig;

public interface GraphProcessor{

    OutputGraph processGraph(ProcessingConfig config) throws Exception;
}
