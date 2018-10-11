package co.digital.testpay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

//import com.laan.provider.models.ErrorModel;
//import com.laan.provider.models.PublishModel;
//import com.laan.provider.utils.LaanConstant;
//
//import org.greenrobot.eventbus.EventBus;

import webconnect.com.webconnect.listener.OnWebCallback;

/**
 * Created by clickapps on 29/8/18.
 */

public class WebCallback implements OnWebCallback {

    private OnWebCallback callback;
    private Context context;

    private WebCallback() {

    }

    public WebCallback(@NonNull Context context, @NonNull OnWebCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public <T> void onSuccess(@Nullable T object, int taskId) {
        if (callback != null) {
            Log.d("api","onSuccess resopnse: " + object);
            callback.onSuccess(object, taskId);

        }
    }

    @Override
    public <T> void onError(@Nullable T object, String error, int taskId) {
        if (object instanceof ErrorModel &&(
                 ((ErrorModel) object).getStatus_code() == Constants.EXPIRE_TOKEN_STATUS_CODE
                || ((ErrorModel) object).getStatus_code() == Constants.INVALID_TOKEN_STATUS_CODE
                || ((ErrorModel) object).getStatus_code() == Constants.INVALID_JWT_TOKEN_STATUS_CODE
                || ((ErrorModel) object).getStatus_code() == Constants.TOKEN_INVALID_TOKEN_STATUS_CODE
                || ((ErrorModel) object).getStatus_code() == Constants.UNAUTHORIZED_TOKEN_STATUS_CODE
                || ((ErrorModel) object).getStatus_code() == Constants.AUTHORIZED_ERROR_TOKEN_STATUS_CODE
                )){

            showSessionExpireDialog((ErrorModel) object);
        }
        if(object instanceof Throwable){
            ErrorModel model = new ErrorModel();
            model.setErrorMessage(error);
            object = (T) model;
        }
        if (callback != null) {
            callback.onError(object, error, taskId);
        }
    }

    private void showSessionExpireDialog(ErrorModel errorModel) {
//        PublishModel<ErrorModel> publishModel = new PublishModel<>();
//        publishModel.setModel(errorModel);
//        publishModel.setTaskId(errorModel.getStatus_code());
//        EventBus.getDefault().post(publishModel);
    }
}
