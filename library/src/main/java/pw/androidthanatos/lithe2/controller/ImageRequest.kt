package pw.androidthanatos.lithe2.controller

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.MainThread
import pw.androidthanatos.lithe2.callback.UIHandler as MainHandler
import android.view.View
import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.cache.ImageDoubleCache
import pw.androidthanatos.lithe2.callback.CallBack
import pw.androidthanatos.lithe2.callback.ImageCallback
import pw.androidthanatos.lithe2.image.BitmapUtils
import pw.androidthanatos.lithe2.util.SignUtil
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Lithe图片请求
 */
class ImageRequest {

    private lateinit var url: String

    private lateinit var view: View

    private var isCache:Boolean = true

    private lateinit var cache: ImageDoubleCache

    private var net: Boolean = true

    private var dir: String = ""

    private var percent: Int = 0

    private var callback: ImageCallback? = null

    private var id: Int = -1


    private val UIHandler: Handler by lazy {
        object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                val holder: ImageHolder = msg!!.obj as ImageHolder
                if (holder.url == holder.target!!.tag){
                    val draw =BitmapDrawable(holder.bitmap)
                    holder.target!!.background = draw
                }

            }
        }
    }



    fun addUrl(url: String): ImageRequest{
        this.url = url
        return this
    }

    fun addTarget(view: View): ImageRequest{
        this.view = view
        return this
    }

    fun closeCache(): ImageRequest{
        this.isCache = false
        return this
    }

    fun fromNet(net: Boolean = true): ImageRequest{
        this.net = net
        return this
    }

    fun addCacheDir(dir: String): ImageRequest{
        this.dir = dir
        return this
    }

    fun addGiePercent(percent: Int): ImageRequest{
        this.percent = percent
        return this
    }

    fun addErrorImage(id: Int): ImageRequest{
        this.id = id
        return this
    }

    fun load() {
        Thread().run {
            if (dir.isNotEmpty() && percent >0){
                cache = ImageDoubleCache.getImageDoubleCache(dir,percent)
            }else if (dir.isNotEmpty() && percent == 0){
                cache = ImageDoubleCache.getImageDoubleCache(cacheDir = dir)
            }else if (dir.isEmpty() && percent >0){
                cache = ImageDoubleCache.getImageDoubleCache(percent = percent)
            }else{
                cache = ImageDoubleCache.getImageDoubleCache()
            }
            view.tag = url
            if (isCache){
                val bitmap = cache.get(SignUtil.getCacheKey(url, HashMap(),HashMap())) as Bitmap?
                if (bitmap != null){
                    post(url,view,bitmap)
                }else{
                    if (net){
                        //网络获取图片
                        loadNet()
                    }else{
                        //本地获取图片
                        loadLocal()
                    }
                }
            }else{
                if (net){
                    //网络获取图片
                    loadNet()
                }else{
                    //本地获取图片
                    loadLocal()
                }
            }
        }
    }

    fun addCallBack(callback: ImageCallback): ImageRequest{
        this.callback = callback
        return this
    }

    /**
     * 从本地获取数据
     */
    @SuppressLint("NewApi")
    private fun loadLocal() {

        val bitmap = BitmapUtils.getLocalBitmap(url,view)
        if (bitmap != null){
            if (isCache){
                cache.set(SignUtil.getCacheKey(url, HashMap(),HashMap()),bitmap)
                val cabit: Bitmap? = cache.get(SignUtil.getCacheKey(url, HashMap(),HashMap())) as Bitmap?
                if (cabit != null){
                    post(url,view,cabit)
                }else{
                    post(url,view,bitmap)
                }
            }else{
                post(url,view,bitmap)
            }
        }else{
            MainHandler.HANDLER.post {
                if (id != -1){
                    val draw = view.context.resources.getDrawable(id)
                    view.background = draw
                }
                callback?.onError()
            }
        }

    }

    /**
     * 从网络获取数据
     */
    private fun loadNet(){
        Lithe.download().addUrl(this.url).addAsyncCallBack(object : CallBack{
            override fun onSuccessful(result: String) {}

            override fun onComplate() {
                MainHandler.HANDLER.post {
                    callback?.onComplate()
                }
            }

            override fun onProgress(progress: Int) {
                MainHandler.HANDLER.post {
                    callback?.onProgress(progress)
                }
            }

            @SuppressLint("NewApi")
            override fun onSuccessful(result: String, stream: InputStream) {
                val buffer = stream.use { it.readBytes() }
                val bitmap = BitmapUtils.getNetBitmap(buffer,view)
                if (bitmap != null){
                    if (isCache){
                        cache.set(SignUtil.getCacheKey(url, HashMap(),HashMap()),bitmap)
                        val cabit: Bitmap? = cache.get(SignUtil.getCacheKey(url, HashMap(),HashMap())) as Bitmap?
                        if (cabit != null){
                            post(url,view,cabit)
                        }else{
                            post(url,view,bitmap)
                        }
                    }else{
                        post(url,view,bitmap)
                    }

                }else{
                    MainHandler.HANDLER.post {
                        if (id != -1){
                            val draw = view.context.resources.getDrawable(id)
                            view.background = draw
                        }
                        callback?.onError()
                    }
                }
            }

            @SuppressLint("NewApi")
            override fun onError(message: String) {
                MainHandler.HANDLER.post {
                    if (id != -1){
                        val draw = view.context.resources.getDrawable(id)
                        view.background = draw
                    }
                    callback?.onError()
                }
            }

        })
    }


    private fun post(url: String,target: View,bitmap: Bitmap){
        val msg = Message.obtain()
        val holder = ImageHolder()
        holder.url = url
        holder.target = target
        holder.bitmap = bitmap
        msg.obj = holder
        UIHandler.sendMessage(msg)
    }

    private class ImageHolder{
        var url: String? = null
        var target: View? = null
        var bitmap: Bitmap? = null
    }





}