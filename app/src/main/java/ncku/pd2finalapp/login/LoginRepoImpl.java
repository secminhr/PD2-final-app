package ncku.pd2finalapp.login;

class LoginRepoImpl implements LoginRepo {

    //provide empty callback as default to avoid null handling
    private SuccessCallback successCallback = () -> {};
    private FailureCallback failureCallbacks = (exception) -> {};

    @Override
    public void setOnSuccessCallback(SuccessCallback callback) {
        successCallback = callback;
    }

    @Override
    public void setOnFailureCallback(FailureCallback callback) {
        failureCallbacks = callback;
    }

    private int counter = 0;
    @Override
    public void login() {
        //TODO: adapt to make network request
        //to test both callback, we make it first fail, and then success
        if (counter == 0) {
            failureCallbacks.onFailure(new LoginFailedException());
            counter++;
        } else {
            successCallback.onSuccess();
        }
    }
}
