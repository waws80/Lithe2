package pw.androidthanatos.lithe2.cache

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import net.bither.util.NativeUtil
import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.util.MD5Utils
import java.io.File

/**
 * Lithe网络请求数据本地缓存
 */

private val sp: SharedPreferences by lazy {
    Lithe.context.getSharedPreferences("Lithe-data-cache",Context.MODE_PRIVATE)
}

class DataDiskCache: Cache{

    override fun set(key: String, value: Any) {
       with(sp.edit()){
           putString(key,value as String)
       }.apply()

    }

    override fun get(key: String): Any? {
       val a =  sp.getString(key,"")
        return a
    }

}

class ImageDiskCache: Cache {

    private var  PERCENT=90

    private var cacheDir: String


    constructor (cacheDir: String = "Lithe-image-cache", percent: Int = 70) {
        this.cacheDir = cacheDir
        if (percent in 1..99){
            PERCENT=percent
        }

    }

    override fun set(key: String, value: Any) {
        val bitmap = value as Bitmap
        val f = File("sdcard/"+cacheDir)
        if (!f.exists()){
            f.mkdirs()
        }
        val file = File("sdcard/"+cacheDir, MD5Utils.getMd5(key)+".jpg")
        NativeUtil.compressBitmap(bitmap,PERCENT,file.absolutePath,true)
    }

    override fun get(key: String): Any? {
        val file= File("sdcard/"+cacheDir, MD5Utils.getMd5(key)+".jpg")
        Log.w("Lithe-image-diskCache", "getCache: find image in $key diskcache")
        return BitmapFactory.decodeFile(file.absolutePath)
    }


}

