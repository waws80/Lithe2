package pw.androidthanatos.lithe2.threadpool

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by liuxiongfei on 2017/6/28.
 */
object ThreadPool {

    private val threadPoolSize: Int = Runtime.getRuntime().availableProcessors()*2+1

    private val queue: LinkedBlockingQueue<Runnable> by lazy {
        LinkedBlockingQueue<Runnable>()
    }

    private val threadPool = Executors.newFixedThreadPool(threadPoolSize)

    private val backgroundHandler: Handler by lazy {
        object : Handler(Looper.myLooper()){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                threadPool.execute(queue.remove())
            }
        }
    }

    internal fun addRequest(runnable: Runnable){
        queue.add(runnable)
        backgroundHandler.sendEmptyMessage(0x100)
    }
}