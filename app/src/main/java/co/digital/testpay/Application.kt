package co.digital.testpay

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDex
import com.google.android.gms.security.ProviderInstaller
import org.apache.http.conn.ssl.SSLSocketFactory
import webconnect.com.webconnect.ApiConfiguration
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

/**
 * Created by clickapps on 29/8/18.
 */
class Application :  Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // only for gingerbread and newer versions
            ProviderInstaller.installIfNeeded(this);

        }



        ApiConfiguration.Builder(this)
                .baseUrl(Constants.BASE_URL)
                .timeOut(2 * 60 * 1000,2 * 60 * 1000)
                .debug(true)
                .config()
    }



}