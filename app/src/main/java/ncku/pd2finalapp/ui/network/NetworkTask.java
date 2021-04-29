package ncku.pd2finalapp.ui.network;

import android.os.Handler;
import android.os.Looper;

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
    //We use Looper.getMainLooper to make them run on main (ui) thread.
    //With this, we can further hide the detail that we're using new thread for each task, and simplify ui codes
    protected void onSuccess() {
        new Handler(Looper.getMainLooper()).post(() -> successCallback.onSuccess());
    }
    protected void onFailure(E exception) {
        new Handler(Looper.getMainLooper()).post(() -> failureCallback.onFailure(exception));
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
