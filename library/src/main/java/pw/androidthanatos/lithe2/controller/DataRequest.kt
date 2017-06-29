package pw.androidthanatos.lithe2.controller

import android.util.Base64
import android.util.Log
import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.cache.*
import pw.androidthanatos.lithe2.callback.CallBack
import pw.androidthanatos.lithe2.callback.UIHandler
import pw.androidthanatos.lithe2.chain.Chain
import pw.androidthanatos.lithe2.generateconfig.GenerateConfig
import pw.androidthanatos.lithe2.threadpool.ThreadPool
import pw.androidthanatos.lithe2.util.NetWorkUtil
import pw.androidthanatos.lithe2.util.SignUtil
import java.nio.charset.Charset

/**
 * Lithe网络请求队列实现类
 */
class DataRequest(private val method: String , private val content_type: String = ""): Request {

    private var  config: GenerateConfig

    private var api: String = ""

    private var url: String = ""

    private val head: HashMap<String,String> = HashMap()

    private val params: HashMap<String,String> = HashMap()

    private var body: ByteArray = ByteArray(0)

    private var retry: Int = 0

    private var cacheTimeOut: Int = 0

    private var callBack: CallBack? = null

    private var async: Boolean = false

    private var cacheType = CacheType.NONE

    private var tag: String = ""

    init {
        if (Lithe.config == null) throw IllegalArgumentException("请先初始化Lithe2")
        config = Lithe.config!!
    }

    override fun addApi(api: String): Request {
        this.api = api
        return this
    }

    override fun addUrl(url: String): Request {
        this.url = url
        return this
    }

    override fun addHead(key: String, value: String): Request {
        this.head.put(key, value)
        return this
    }

    override fun addHeads(map: HashMap<String, String>): Request {
        this.head.putAll(map)
        return this
    }

    override fun addParam(key: String, value: String): Request {
        this.params.put(key, value)
        return this
    }

    override fun addParams(map: HashMap<String, String>): Request {
        this.params.putAll(map)
        return this
    }

    override fun addBody(value: ByteArray): Request {
        this.body = value
        return this
    }

    override fun addTag(tag: String): Request {
        Lithe.concurrentlist.put(tag,tag)
        this.tag =tag
        return this
    }

    override fun addCacheType(cacheType: CacheType): Request {
        this.cacheType = cacheType
        return this
    }

    override fun addRetry(num: Int): Request {
        this.retry = num
        return this
    }

    override fun addCacheTimeOut(second: Int): Request {
        this.cacheTimeOut = second
        return this
    }

    override fun addCallBack(callBack: CallBack) {
        this.callBack = callBack
        ThreadPool.addRequest(builder())
    }

    override fun addAsyncCallBack(callBack: CallBack) {
        this.callBack = callBack
        this.async = true
        ThreadPool.addRequest(builder())
    }

    private fun buildChain(): Chain {
        var c_url = ""
        val baseUrl = config.baseUrl
        if (baseUrl.isEmpty() && this.url.isEmpty()) throw IllegalArgumentException("请填写网络请求路径")
        if (baseUrl.isEmpty() && this.url.isNotEmpty() && this.url.startsWith("http")) c_url = this.url
        if (baseUrl.isNotEmpty() && this.url.isNotEmpty() && this.url.startsWith("http")) c_url = this.url
        if (baseUrl.isNotEmpty() && this.url.isEmpty() && baseUrl.startsWith("http")) c_url = baseUrl+ this.api
        //params.forEach { c_url += c_url+"?${it.key}=${it.value}&" }
        //c_url = c_url.substring(0,c_url.length-1)
        this.head.put("Accept-Encoding", "identity")
        this.head.put("Connection", "keep-alive")
        this.head.put("Charsert", "UTF-8")

       return Chain(c_url,this.method,this.head,this.body,this.params)
    }

    private fun builder() = Runnable {
        val inteceptor = config.interceptor
        var chain: Chain = buildChain()
        if (inteceptor != null) chain = inteceptor.interceptor(chain)
        val array = SignUtil.getCacheKey(chain.url,chain.params,chain.head)
        var cache: Cache? = null
        when(this.cacheType){
            CacheType.NONE -> cache = null
            CacheType.DISK -> cache = DataDiskCache()
            CacheType.DOUBLE -> cache = DataDoubleCache()
            else -> cache = null
        }

        if (this.async){
            callBack!!.onStart()
        }else{
            UIHandler.HANDLER.post{ callBack!!.onStart()}
        }

        var data: String = ""
        var cacheTime = -1
        if (cache != null){
            if (cache.get(array).toString().split(":").isNotEmpty() && cache.get(array).toString().split(":").size > 1){
                data = String(Base64.decode(cache.get(array).toString().split(":")[1],Base64.DEFAULT), Charset.forName("UTF-8"))
                cacheTime = cache.get(array).toString().split(":")[0].toInt()
            }

        }

        if ( cacheTimeOut > 0 && cacheTime != -1 &&
                (System.currentTimeMillis()/1000).toInt() - cacheTime < cacheTimeOut &&
                data.isNotEmpty()){
            Log.w("Lithe","缓存 $cacheTimeOut    ${System.currentTimeMillis()/1000 - cacheTime}")
            if (this.async){
                callBack!!.onComplate()
                callBack!!.onSuccessful(data,data.toByteArray().inputStream())
                callBack!!.onSuccessful(data)
            }else{
                UIHandler.HANDLER.post{
                    callBack!!.onComplate()
                    callBack!!.onSuccessful(data,data.toByteArray().inputStream())
                    callBack!!.onSuccessful(data)
                }
            }

            return@Runnable
        }

        if (NetWorkUtil.isNetWorkAvailable(Lithe.context)){
            //保证每个网络请求的单独性
            val converter = config.converter::class.java.newInstance()
            converter.call(chain,config.connectTimeOut,config.readTimeOut,config.sslSocketFactory,
                    this.callBack,cache,this.async,this.retry,this.tag,content_type)
        }else{
            if (this.async){
                callBack!!.onError("当前网络异常，请检查网络")
            }else{
                UIHandler.HANDLER.post { callBack!!.onError("当前网络异常，请检查网络") }
            }
        }

    }
}