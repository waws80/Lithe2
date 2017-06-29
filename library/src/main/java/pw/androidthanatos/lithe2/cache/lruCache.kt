package pw.androidthanatos.lithe2.cache

import android.graphics.Bitmap
import android.support.v4.util.LruCache
import java.nio.charset.Charset

/**
 * Lithe内存缓存
 */
private val maxSize =  (Runtime.getRuntime().maxMemory()/1024/8).toInt()
private val bitmapMemeryCache: LruCache<String,Bitmap> by lazy {
    object : LruCache<String,Bitmap>(maxSize){
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value!!.rowBytes * value.height
        }
    }
}

private val dataMemeryCache: LruCache<String,ByteArray> by lazy {
    object : LruCache<String,ByteArray>(maxSize/4){
        override fun sizeOf(key: String?, value: ByteArray?): Int {
            return value!!.size
        }
    }
}

class BitmapMemeryCache: Cache{

    override fun set(key: String, value: Any) {
        bitmapMemeryCache.put(key,value as Bitmap)
    }

    override fun get(key: String): Any? = bitmapMemeryCache[key]

    fun removeCache(){
        bitmapMemeryCache.evictAll()
    }

}


