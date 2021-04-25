package ncku.pd2finalapp.login;

interface LoginRepo {
    void login(String username, String password);
    void setOnSuccessCallback(SuccessCallback callback);
    void setOnFailureCallback(FailureCallback callback);
    LoginRepo instance = new LoginRepoImpl();

    @FunctionalInterface
    interface SuccessCallback {
        void onSuccess();
    }

    @FunctionalInterface
    interface FailureCallback {
        void onFailure(Exception e);
    }

    class LoginFailedException extends Exception {
        public LoginFailedException() {
            super("Login failed. Username or password is incorrect.");
        }
    }
}
