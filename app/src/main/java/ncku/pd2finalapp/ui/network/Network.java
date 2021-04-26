package ncku.pd2finalapp.ui.network;

//Provide an interface to interact with networking part in a non-blocking way
public class Network {
    public static NetworkTask<LoginTask.LoginFailedException> login(String username, String password) {
        return new LoginTask(username, password);
    }

    public static NetworkTask<RegisterTask.UsernameExistsException> register(String username, String nickname, String password, String faction) {
        return new RegisterTask(username, nickname, password, faction);
    }
}
