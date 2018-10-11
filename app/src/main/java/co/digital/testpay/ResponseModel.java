package co.digital.testpay;

import com.google.gson.annotations.SerializedName;

/**
 * Created by clickapps on 29/8/18.
 */

public class ResponseModel {
    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
