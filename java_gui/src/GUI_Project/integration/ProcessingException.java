package GUI_Project.integration;

/**
 * Checked exception describing a failure while running the external
 * computation module (non-zero exit code, missing executable, etc.).
 *
 * NOTE: this file is an addition not listed in the original project tree.
 * If you prefer to stay strictly within the documented structure, you may
 * replace it with a plain {@link java.io.IOException}.
 */
public class ProcessingException extends Exception {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
