package pw.androidthanatos.lithe2.converter

import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.cache.Cache
import pw.androidthanatos.lithe2.callback.CallBack
import pw.androidthanatos.lithe2.callback.UIHandler
import pw.androidthanatos.lithe2.chain.Chain
import pw.androidthanatos.lithe2.util.SignUtil
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

/**
 *默认网络转换器
 */
class DefaultConverter : Converter {

    private var mRetry: Int = 0

    private lateinit var conn: HttpURLConnection
    override fun call(chain: Chain, connectTimeOut: Int, readTimeOut: Int, sslSocketFactory: SSLSocketFactory?, callBack: CallBack?, cache: Cache?, async: Boolean, retry: Int, tag: String, content_type: String) {
        this.mRetry = retry
        var c_url = chain.url
        chain.params.forEach { c_url += c_url+"?${it.key}=${it.value}&" }
        val len = if (chain.params.isEmpty()) c_url.length else c_url.length-1
        c_url = c_url.substring(0,len)
        if (c_url.startsWith("https:")){
            conn = URL(c_url).openConnection() as HttpsURLConnection
            if (sslSocketFactory != null) (conn as HttpsURLConnection).sslSocketFactory = sslSocketFactory
        }else if (c_url.startsWith("http")){
            conn = URL(c_url).openConnection() as HttpURLConnection
        }

        conn.requestMethod = chain.method
        conn.connectTimeout = connectTimeOut
        conn.readTimeout = readTimeOut
        chain.head.forEach {
            conn.addRequestProperty(it.key,it.value)
        }
        if (content_type.isNotEmpty()) conn.setRequestProperty("Content-Type",content_type)
        conn.doInput = true
        if (chain.method != "GET" && chain.body.isNotEmpty()){
            conn.doOutput = true
            val contentLength = chain.body.size
            val stream = chain.body.inputStream()
            val byteArray = ByteArray(DEFAULT_BUFFER_SIZE)
            var currentLength = 0
            var baytes = stream.read(byteArray)
            val dos = DataOutputStream(conn.outputStream)
            while (baytes > 0){
                dos.write(byteArray,0,baytes)
                currentLength += baytes
                val progress = (currentLength.toFloat()/contentLength.toFloat()*100).toInt()
                if (async){
                    callBack!!.onUpload(progress)
                }else{
                    UIHandler.HANDLER.post { callBack!!.onUpload(progress) }
                }
                baytes = stream.read(byteArray)
            }
            dos.flush()
        }

        try {

            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_OK){

                val stream = conn.inputStream
                val contentLength = conn.contentLength
                var currentLength = 0
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytes = stream.read(buffer)
                val bos = ByteArrayOutputStream()
                while (bytes > 0){
                    bos.write(buffer,0,bytes)
                    currentLength += bytes
                    val progress = (currentLength.toFloat()/contentLength.toFloat()*100).toInt()
                    if (async){
                        if (contentLength != -1 && isWork(tag)){
                            callBack!!.onProgress(progress)
                        }
                    }else{
                        if (contentLength != -1 && isWork(tag)){
                            UIHandler.HANDLER.post { callBack!!.onProgress(progress) }
                        }
                    }
                    bytes = stream.read(buffer)
                }
                if (contentLength <1 && isWork(tag)){
                    if (async){
                        callBack!!.onProgress(100)
                    }else{
                        UIHandler.HANDLER.post { callBack!!.onProgress(100) }
                    }
                }
                val res = bos.toString("UTF-8")
                cache?.set(SignUtil.getCacheKey(chain.url,chain.params,chain.head),res)
                if (async && isWork(tag)){
                    callBack!!.onComplate()
                    callBack.onSuccessful(res,bos.toByteArray().inputStream())
                    callBack.onSuccessful(res)
                }else if (isWork(tag)){
                    UIHandler.HANDLER.post {
                        callBack!!.onComplate()
                        callBack.onSuccessful(res,bos.toByteArray().inputStream())
                        callBack.onSuccessful(res)
                    }
                }
                Lithe.concurrentlist.remove(tag)

            }else{
                if (this.mRetry >0 && isWork(tag)){
                    this.mRetry--
                    call(chain, connectTimeOut, readTimeOut, sslSocketFactory, callBack, cache, async, retry, tag, content_type)

                } else if (isWork(tag)){
                    if (async){
                        callBack!!.onError("请求错误码：$code")
                    }else{
                        UIHandler.HANDLER.post {
                            callBack!!.onError("请求错误码：$code")
                        }
                    }
                }
            }

        }catch (e: SocketTimeoutException){
            if (async){
                callBack!!.onError("网络链接超时")
            }else{
                UIHandler.HANDLER.post {
                    callBack!!.onError("网络链接超时")
                }
            }
        }


    }

    fun isWork(tag: String): Boolean {
        if (tag.isEmpty()) return true
        return (Lithe.concurrentlist.containsKey(tag))
    }
}