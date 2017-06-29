package pw.androidthanatos.lithe2.callback

/**
 * Created by liuxiongfei on 2017/6/28.
 */
interface ImageCallback {

    fun onProgress(progress: Int)

    fun onComplate()

    fun onError()
}