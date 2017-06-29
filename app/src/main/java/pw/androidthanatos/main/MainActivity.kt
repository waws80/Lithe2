package pw.androidthanatos.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.cache.CacheType
import pw.androidthanatos.lithe2.callback.CallBack
import pw.androidthanatos.lithe2.callback.ImageCallback
import pw.androidthanatos.lithe2.converter.DefaultConverter
import pw.androidthanatos.lithe2.generateconfig.GenerateConfig
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv_text = findViewById(R.id.tv_text) as TextView
        val iv = findViewById(R.id.iv)
        val tv = findViewById(R.id.tv) as TextView

        Lithe.get().addCacheTimeOut(10)
                .addRetry(5)
                .addCacheType(CacheType.DOUBLE)
                .addTag("haha")
                .addCallBack(object : CallBack{
                    override fun onSuccessful(result: String) {
                        tv_text.text = result
                    }

                    override fun onError(message: String) {
                        tv_text.text = message
                    }
                })

        Lithe.loadImage().addUrl("https://raw.githubusercontent.com/waws80/waws80.github.io/master/a.jpg")
                .addTarget(iv)
                .addErrorImage(R.mipmap.ic_launcher_round)
                .addCallBack(object : ImageCallback {
            override fun onProgress(progress: Int) {
                log("image-progress: $progress")
                tv.text = "$progress %"
                if (progress == 100){
                    tv.visibility = View.GONE
                }
            }
            override fun onComplate() {
                log("image-onComplate:")
            }

            override fun onError() {
                log("image-onError:")
            }
        }).load()

    }


    fun log(msg: String){
        Log.d("TAG",msg)
    }
}
