package ncku.pd2finalapp.ui.network;

import java.util.concurrent.Executors;

//The generic here is for task that only receive one type of error.
//For those may receive multiple types of errors, just use Exception.
public abstract class NetworkTask<E extends Exception> {

    private SuccessCallback successCallback = () -> {};
    private FailureCallback<E> failureCallback = (exception) -> {};

    public NetworkTask<E> setOnSuccessCallback(SuccessCallback callback) {
        successCallback = callback;
        return this;
    }
    public NetworkTask<E> setOnFailureCallback(FailureCallback<E> callback) {
        failureCallback = callback;
        return this;
    }
    public void execute() {
        //since task is blocking, we wrap it in a future to avoid screen from freezing
        Executors.newSingleThreadExecutor().submit(this::task);
    }

    protected abstract void task();
    protected void onSuccess() {
        successCallback.onSuccess();
    }
    protected void onFailure(E exception) {
        failureCallback.onFailure(exception);
    }

    @FunctionalInterface
    public interface SuccessCallback {
        void onSuccess();
    }

    @FunctionalInterface
    public interface FailureCallback<Err extends Exception> {
        void onFailure(Err e);
    }
}
