package pw.androidthanatos.lithe2.controller

import android.util.TimeUtils
import pw.androidthanatos.lithe2.cache.CacheType
import pw.androidthanatos.lithe2.callback.CallBack
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 请求队列
 */
interface Request {

    fun addApi(api: String): Request

    fun addUrl(url: String): Request

    fun addHead(key: String, value: String): Request

    fun addHeads(map: HashMap<String,String>): Request

    fun addParam(key: String, value: String): Request

    fun addParams(map: HashMap<String,String>): Request


    fun addTag(tag: String): Request

    fun addBody(value: ByteArray): Request

    fun addRetry(num: Int): Request

    fun addCacheTimeOut(time: Int): Request

    fun addCallBack(callBack: CallBack)

    fun addAsyncCallBack(callBack: CallBack)

    fun addCacheType(cacheType: CacheType): Request

}