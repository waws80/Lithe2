package pw.androidthanatos.lithe2.cache

import android.util.Base64
import pw.androidthanatos.lithe2.util.MD5Utils
import pw.androidthanatos.lithe2.util.SignUtil
import java.nio.charset.Charset

/**
 * 本地缓存
 */


class DataDoubleCache: Cache{


    private val dis = DataDiskCache()


    override fun set(key: String, value: Any) {
        val a = (System.currentTimeMillis()/1000).toString()
        val b = Base64.encodeToString(value.toString().toByteArray(Charset.forName("UTF-8")),Base64.DEFAULT)
        dis.set(key, a+":"+b)
    }

    override fun get(key: String): Any {
        if (dis.get(key) != null){
            val a = dis.get(key)
            return a.toString()
        }else{
            return ""
        }
    }
}

class ImageDoubleCache private constructor(cacheDir: String = "Lithe-image-cache", percent: Int = 70 ): Cache{

    private val me = BitmapMemeryCache()

    private val dis = ImageDiskCache(cacheDir, percent)

    companion object {
        fun getImageDoubleCache(cacheDir: String = "Lithe-image-cache", percent: Int = 70 )
        = ImageDoubleCache(cacheDir, percent)

        fun removeMemeryCache(){
            getImageDoubleCache().removeMemeryCache()
        }
    }
    override fun set(key: String, value: Any) {
        me.set(key, value)
        dis.set(key, value)
    }

    override fun get(key: String): Any? {
        if (me.get(key) != null) {
            return me.get(key)
        }else if (dis.get(key) != null){
            return dis.get(key)
        }else{
            return null
        }
    }

    fun removeMemeryCache(){
        me.removeCache()
    }

}