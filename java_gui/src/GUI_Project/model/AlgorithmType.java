package GUI_Project.model;

public enum AlgorithmType {

    TUTTE("Tutte", "--tutte"),
    SPECTRAL_LAYOUT("SpectralLayout", "--spectral");

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