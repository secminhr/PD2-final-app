package ncku.pd2finalapp.ui.network.repositories;

//Provide a abstraction of the repository, can further separate ui and data access.
public interface TaskRepository {
    String infoCheckData();
    String loginCheckData(String username, String password);
    String registerCheckData(String username, String nickname, String password, String faction);

    TaskRepository current = new RealRepo();
}
