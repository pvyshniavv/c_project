package GUI_Project.model;

/**
 * Configuration holder. Stores file paths and the runtime parameters
 * chosen by the user before launching the external C module.
 */
public class ProcessingConfig {

    private String inputPath;
    private String outputPath;
    private AlgorithmType algorithm = AlgorithmType.TUTTE;
    private FileFormat outputFormat = FileFormat.BINARY;

    /** Number of clusters the graph should be split into (must be >= 1). */
    private int clusterCount = 2;

    /** Allowed imbalance margin between clusters (must be >= 0). */
    private double margin = 0.0;

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    public FileFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(FileFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public int getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        this.clusterCount = clusterCount;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
