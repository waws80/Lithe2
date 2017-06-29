package pw.androidthanatos.lithe2

import android.content.Context
import pw.androidthanatos.lithe2.cache.ImageDoubleCache
import pw.androidthanatos.lithe2.controller.DataRequest
import pw.androidthanatos.lithe2.controller.ImageRequest
import pw.androidthanatos.lithe2.generateconfig.GenerateConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * Lithe2网络请求框架
 */
object Lithe {

    internal lateinit var context: Context

    internal var config: GenerateConfig? = null

    internal val concurrentlist: ConcurrentHashMap<String,String> by lazy {
        ConcurrentHashMap<String,String>()
    }

    internal val imageRequest = ImageRequest()

    /**
     * 对全局上下文对象进行初始化
     */
    fun install(context: Context){
        this.context = context
    }

    /**
     * 添加全局的一些基本配置
     */
    fun addGenerateConfig(config: GenerateConfig){
        this.config = config
    }

    fun removeRequest(tag: String){
        concurrentlist.remove(tag)
    }

    fun removeAll(){
        concurrentlist.forEach {
            concurrentlist.remove(it.key)
        }
    }

    /**
     * get请求操作
     */
    fun get() = DataRequest("GET")

    /**
     * post请求操作
     */
    fun post() = DataRequest("POST")

    /**
     * put请求操作
     */
    fun put() = DataRequest("PUT")

    /**
     * delete请求操作
     */
    fun delete() = DataRequest("DELETE")

    /**
     * 上传请求操作
     */
    fun upload(content_type: String = "") = DataRequest("POST",content_type)

    /**
     * 下载请求操作
     */
    fun download() = DataRequest("GET")

    /**
     * 图片请求操作
     */
    fun loadImage() =imageRequest

    fun removeMemeryCache() = ImageDoubleCache.removeMemeryCache()
}