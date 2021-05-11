package ncku.pd2finalapp.ui.network.repositories;

import ncku.pd2finalapp.ReceiveAndSend.InfoCheck;
import ncku.pd2finalapp.ReceiveAndSend.LoginCheck;
import ncku.pd2finalapp.ReceiveAndSend.RegisterCheck;

//Repo that connect to a server.
class RealRepo implements TaskRepository {
    @Override
    public String infoCheckData() {
        return new InfoCheck().InfoCheckData();
    }

    @Override
    public String loginCheckData(String username, String password) {
        return new LoginCheck().LoginCheckData(username, password);
    }

    @Override
    public String registerCheckData(String username, String nickname, String password, String faction) {
        return new RegisterCheck().RegisterCheckData(username, nickname, password, faction);
    }
}
