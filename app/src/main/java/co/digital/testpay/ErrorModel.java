package co.digital.testpay;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by clickapps on 29/8/18.
 */

public class ErrorModel {
    @SerializedName("message")
    private String errorMessage;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error_code")
    private String status_code;


    public boolean getSuccess() {
        return BooleanUtils.toBooleanDefaultIfNull(success, false);
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    public int getStatus_code() {
        return NumberUtils.toInt(status_code, -1);
    }
}
