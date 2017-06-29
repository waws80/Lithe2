package pw.androidthanatos.lithe2.callback

import java.io.InputStream

/**
 * 请求数据成功后的回调
 */
interface CallBack {

    fun onStart(){}

    fun onComplate(){}

    fun onProgress(progress: Int){}

    fun onUpload(progress: Int){}

    fun onSuccessful(result: String,stream: InputStream){}

    fun onSuccessful(result: String)

    fun onError(message: String)
}