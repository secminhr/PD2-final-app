package ncku.pd2finalapp.ui.network.tasks;

import ncku.pd2finalapp.ReceiveAndSend.FortData;
import ncku.pd2finalapp.ui.map.FortDataModel;

//TODO: Change Exception to appropriate one
//TODO: adapt to backend
public class GetFortDataTask extends NetworkTask<FortDataModel, Exception> {

    @Override
    protected void task() {
        String response = new FortData().getFortData();
        try {
            //FortDataModel model = FortDataModel.fromString(response);
            //onSuccess(model);
            onSuccess(new FortDataModel());
        } catch (IllegalArgumentException e) {
            onFailure(e);
        }
    }
}
