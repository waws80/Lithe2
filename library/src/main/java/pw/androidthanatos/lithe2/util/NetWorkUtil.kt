package pw.androidthanatos.lithe2.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by liuxiongfei on 2017/6/28.
 */
/**
 * 判断当前设备是否有网
 * true  有网   false 没网
 */

object NetWorkUtil{

    fun isNetWorkAvailable(context: Context): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nk = cm.allNetworkInfo
        var a = false
        if (nk != null && nk.isNotEmpty()){
            nk.forEach {
                if (it.state == NetworkInfo.State.CONNECTED){
                    a = true
                }
            }
        }
        return a
    }

}
