package pw.androidthanatos.lithe2.generateconfig

import pw.androidthanatos.lithe2.Interceptor
import pw.androidthanatos.lithe2.converter.Converter
import javax.net.ssl.SSLSocketFactory

/**
 * 基本的全局配置信息
 */
data class GenerateConfig(val baseUrl: String,
                          val baseHead: HashMap<String,String>,
                          val connectTimeOut: Int,
                          val readTimeOut: Int,
                          val converter: Converter,
                          val interceptor: Interceptor?,
                          val sslSocketFactory: SSLSocketFactory?)