package ncku.pd2finalapp.ui.network.tasks;

import ncku.pd2finalapp.ReceiveAndSend.FortData;
import ncku.pd2finalapp.ui.map.FortDataModel;

//TODO: Change Exception to appropriate one
public class GetFortDataTask extends NetworkTask<FortDataModel, Exception> {

    @Override
    protected void task() {
        String response = new FortData().getFortData();
        try {
            FortDataModel model = FortDataModel.fromString(response);
            onSuccess(model);
        } catch (IllegalArgumentException e) {
            onFailure(e);
        }
    }
}
