package pw.androidthanatos.lithe2.util

/**
 * 请求签名
 */
object SignUtil {

    fun getCacheKey(url: String,params: HashMap<String,String>,head: HashMap<String,String>): String{

        val a = url
        var b =""
        params.forEach { b += "${it.key}${it.value}" }
        var c = ""
        head.forEach { c += "${it.key}${it.value}" }

       return MD5Utils.getMd5(a+b+c)
    }
}