package pw.androidthanatos.lithe2.converter

import pw.androidthanatos.lithe2.cache.Cache
import pw.androidthanatos.lithe2.callback.CallBack
import pw.androidthanatos.lithe2.chain.Chain
import javax.net.ssl.SSLSocketFactory

/**
 * 网络请求类型转换器
 */
interface Converter {

    fun call(chain: Chain, connectTimeOut: Int, readTimeOut: Int, sslSocketFactory: SSLSocketFactory?, callBack: CallBack?, cache: Cache?, async: Boolean, retry: Int, tag: String, content_type: String)
}