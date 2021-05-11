package ncku.pd2finalapp.ui.network.tasks;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

//Generic type R is the type the SuccessCallback will consume.
//If the task has nothing to return, use Void as R and call onSuccess(null)

//The generic type E here is for task that only receive one type of error.
//For those may receive multiple types of errors, just use Exception.
public abstract class NetworkTask<R, E extends Exception> {

    private SuccessCallback<R> successCallback = (result) -> {};
    private FailureCallback<E> failureCallback = (exception) -> {};

    public NetworkTask<R, E> setOnSuccessCallback(SuccessCallback<R> callback) {
        successCallback = callback;
        return this;
    }
    public NetworkTask<R, E> setOnFailureCallback(FailureCallback<E> callback) {
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
    protected void onSuccess(R result) {
        new Handler(Looper.getMainLooper()).post(() -> successCallback.onSuccess(result));
    }
    protected void onFailure(E exception) {
        new Handler(Looper.getMainLooper()).post(() -> failureCallback.onFailure(exception));
    }

    @FunctionalInterface
    public interface SuccessCallback<Result> {
        void onSuccess(Result r);
    }

    @FunctionalInterface
    public interface FailureCallback<Err extends Exception> {
        void onFailure(Err e);
    }
}
