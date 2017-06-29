package pw.androidthanatos.lithe2.sslsocketfactory

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * https忽略签名证书
 */
class DefaultSSLSocketFactory {

    fun getSSLSocketFactory(): SSLSocketFactory {

        val tm = arrayOf(CustomTrustManager())
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null,tm, SecureRandom())
        return sslContext.socketFactory
    }

    inner class CustomTrustManager: X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

    }

}