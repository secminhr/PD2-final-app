package ncku.pd2finalapp.ui.network.tasks;

public class NoException extends Exception {
    public NoException() {
        super("This exception should never happen");
    }
}
