package GUI_Project.model;

public class ProcessingConfig {
    private String inputFileName;
    private String outputFileName;
    private AlgorithmType algorithmUsed;
    private FileFormat outputFileFormat;

    public ProcessingConfig(String inputFileName, String outputFileName, AlgorithmType algorithmUsed, FileFormat outputFileFormat) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.algorithmUsed = algorithmUsed;
        this.outputFileFormat = outputFileFormat;
    }

    public String getInputFileName() { return inputFileName; }
    public String getOutputFileName() { return outputFileName; }
    public AlgorithmType getAlgorithmUsed() { return algorithmUsed; }
    public FileFormat getOutputFileFormat() { return outputFileFormat; }

    public void setInputFileName(String inputFileName) { this.inputFileName = inputFileName; }
    public void setOutputFileName(String outputFileName) { this.outputFileName = outputFileName; }
    public void setAlgorithmUsed(AlgorithmType algorithmUsed) { this.algorithmUsed = algorithmUsed; }
    public void setOutputFileFormat(FileFormat outputFileFormat) { this.outputFileFormat = outputFileFormat; }
}
