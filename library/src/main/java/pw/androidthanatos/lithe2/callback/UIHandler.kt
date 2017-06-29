package pw.androidthanatos.lithe2.callback

import android.os.Handler
import android.os.Looper

/**
 * 切换线程
 */
object UIHandler {

    val HANDLER = Handler(Looper.getMainLooper())
}