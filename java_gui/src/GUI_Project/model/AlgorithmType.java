package GUI_Project.model;
/**
 * Available layout / partitioning algorithms.
 * The {@code flag} value is passed to the external C module on the command line.
 */
public enum AlgorithmType {

    TUTTE("Tutte", "-a alg1"),
    SPECTRAL_LAYOUT("SpectralLayout", "-a alg2");

    private final String displayName;
    private final String flag;

    AlgorithmType(String displayName, String flag) {
        this.displayName = displayName;
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return displayName;
    }
}