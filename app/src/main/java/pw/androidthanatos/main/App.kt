package pw.androidthanatos.main

import android.app.Application
import pw.androidthanatos.lithe2.Lithe
import pw.androidthanatos.lithe2.converter.DefaultConverter
import pw.androidthanatos.lithe2.generateconfig.GenerateConfig
import pw.androidthanatos.lithe2.sslsocketfactory.DefaultSSLSocketFactory

/**
 * Created by liuxiongfei on 2017/6/29.
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()
        Lithe.install(this.applicationContext)
        val config = GenerateConfig("https://www.baidu.com", //baseURl
                HashMap(), //全局头部
                6000,  //链接超时时间
                6000,  //读取数据超时时间
                DefaultConverter(),  //网络请求转换器
                null,  //拦截器
                DefaultSSLSocketFactory().getSSLSocketFactory() //默认https签名证书
        )
        //添加全局基本配置
        Lithe.addGenerateConfig(config)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Lithe.removeMemeryCache()
    }

}