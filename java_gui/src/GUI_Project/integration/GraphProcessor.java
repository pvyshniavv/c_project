package GUI_Project.integration;

import GUI_Project.model.ProcessingConfig;

/**
 * Contract for modules that process (partition / lay out) graphs.
 * Decoupling the presenter from a concrete implementation keeps coupling low
 * and allows swapping the C module for, e.g., a pure-Java implementation.
 */
public interface GraphProcessor {

    /**
     * Runs the processing step described by {@code config}. The result is
     * expected to be written to {@link ProcessingConfig#getOutputPath()}.
     *
     * @throws ProcessingException if the module fails (bad exit code,
     *                             missing executable, etc.)
     */
    void process(ProcessingConfig config) throws ProcessingException;
}
