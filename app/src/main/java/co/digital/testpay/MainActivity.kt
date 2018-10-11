package co.digital.testpay

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.payfort.fort.android.sdk.base.FortSdk
import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager
import com.payfort.sdk.android.dependancies.base.FortInterfaces
import com.payfort.sdk.android.dependancies.models.FortRequest
import kotlinx.android.synthetic.main.activity_main.*
import webconnect.com.webconnect.WebConnect
import webconnect.com.webconnect.listener.OnWebCallback
import java.security.MessageDigest
/**
 * this activity is PayFort Demo
 *
 * */
class MainActivity : AppCompatActivity(), OnWebCallback {

    var fortCallback: FortCallBackManager? = null
    val TAG = "payTag"
    lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /**
         * get device id using there SDK.
         * */
        deviceId = FortSdk.getDeviceId(this)

        /**
         * initialize Fort callback
         * */
        initFortCallback()

        /**
         * set onClick listener
         * */
        btn.setOnClickListener {
            /**
             * start purchase operation by getting access_token first
             * */
            getToknSdk()
        }
    }




    /**
     * return hashing of any string
     * note: we use SHA-256 Cryptographic Hash Algorithm
     *  but you should use same one selected in your payfort account.
     * */
    fun getHashString(t: String): String {
        val bytes = t.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashSt = digest.fold("", { str, it -> str + "%02x".format(it) })
        return hashSt
    }


    /**
     * get access token from payfort server for each purchase operation
     *  here I'm using retrofit library to call api,, you can use whatEver you are comfortable with it.
     * */
    private fun getToknSdk() {
        textOne.text = "get signature..."
        val param = linkedMapOf<String, String>()

        /**
         * You should make one string with no space e.g key1=value1key2=value2
         * you should include all params
         * you should order them ASC
         * you should put  SHA request phrase ' in this example => Hello' at the beginning  e.g  Hellokey1=value1key2=value2
         * you should put  SHA response phrase ' in this example => Hello' at the end  e.g  Hellokey1=value1key2=value2Hellow
         *  check Docs for more details
         * */
        /**
         * you should get 'access_code' from your payfort account
         * */

        param.put("access_code", "MedoIi22p98sdqXbrtPt")
        param.put("device_id", deviceId)
        param.put("language", "en")
        /**
         * you should get 'merchant_identifier' from your payfort account
         * */
        param.put("merchant_identifier", "WwmmkkwW")
        param.put("service_command", "SDK_TOKEN")

        val sb = StringBuilder()
        /**
         * change 'Hello' by what you got from payFort.
        you will find it in your account*/
        sb.append("Hello")
        param.forEach { (k, v) ->
            sb.append("$k=$v")
//            sb.append(k)
//            sb.append("=")
//            sb.append(v)
        }
        sb.append("Hello")
        param.put("signature", getHashString(sb.toString()))

        /**
         * calling api https://sbpaymentservices.payfort.com/FortAPI/paymentApi
         *
         * */
        WebConnect.with(this, "paymentApi")
                .post()
                /**
                 * put url depending on the Environment you targeting
                 * */
                .baseUrl("https://sbpaymentservices.payfort.com/FortAPI/")
                .bodyParam(param)
                .taskId(11)
                .callback(WebCallback(this, this), ResponsePay::class.java, ErrorModel::class.java)
                .connect()

    }

    /**
     * start buy operation via SDK
     * put required keys and there values
     * */
    fun startPayFortSdk(sign: String) {
        val model = FortRequest()
        /**
         * to see response in log
         * */
        model.isShowResponsePage = true
        val hash = hashMapOf<String, String>()

//        getHashString.put("command", "PURCHASE")
        hash.put("command", "AUTHORIZATION")
        hash.put("customer_email", "m.salem@clickapps.co")
        hash.put("currency", "AED")
        /**
        = 10000 => 100, should be multi by some value depending on your currency, check payfort Docs fore more detail
         */
        hash.put("amount", "10000")
        hash.put("language", "en")
        /**
         * merchant_reference represented purchase id, it should be unique
         * here we let user to entered as our test requirement.
         * */
        val x = editOne.text.toString()
        hash.put("merchant_reference", x)
        /**
         * you can also add any option key-value pairs
         * */
//        getHashString.put("customer_name", "Sam")
//        getHashString.put("customer_ip", "172.150.16.10")
//        getHashString.put("payment_option", "VISA")
//        getHashString.put("eci", "ECOMMERCE")
//        getHashString.put("order_description", "DESCRIPTION")
        hash.put("sdk_token", sign)


        model.requestMap = hash.toMap()



        /**
         * start SDK
         *
         * user will input his info e.g card number...
         * then will  receive the result in callbacks bellow
         * */
        FortSdk
                .getInstance()
                .registerCallback(this, model,
                        FortSdk.ENVIRONMENT.TEST, 5,
                        fortCallback, true, object : FortInterfaces.OnTnxProcessed {
                    override fun onSuccess(p0: MutableMap<String, Any>?, p1: MutableMap<String, Any>?) {
                        Log.d(TAG, "onSuccess")
                        Log.d(TAG, p0.toString())
                        Log.d(TAG, p1.toString())
                    }

                    override fun onFailure(p0: MutableMap<String, Any>?, p1: MutableMap<String, Any>?) {
                        Log.d(TAG, "onFailure")
                        Log.d(TAG, p0.toString())
                        Log.d(TAG, p1.toString())
                        Toast.makeText(this@MainActivity, "Error: ${p1?.get("response_message")}", Toast.LENGTH_LONG).show()


                    }

                    override fun onCancel(p0: MutableMap<String, Any>?, p1: MutableMap<String, Any>?) {
                        Log.d(TAG, "onCancel")
                        Log.d(TAG, p0.toString())
                        Log.d(TAG, p1.toString())
                    }

                })
    }

    fun initFortCallback() {
        fortCallback = FortCallBackManager.Factory.create();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fortCallback!!.onActivityResult(requestCode, resultCode, data);
    }


    //region apis callback
    override fun <T : Any?> onSuccess(`object`: T?, taskId: Int) {
        textOne.text = "TRY"

        if (`object` is ResponsePay) {
            if (`object`.sdk_token.isEmpty()) {
                Toast.makeText(this, "Error: ${`object`.response_message}", Toast.LENGTH_LONG).show()
            } else {
                startPayFortSdk(`object`.sdk_token)
            }
        }


    }

    override fun <T : Any?> onError(`object`: T?, error: String?, taskId: Int) {
        textOne.text = "TRY"
        Toast.makeText(this, "onError $error", Toast.LENGTH_SHORT).show()

    }

    //endregion
}
