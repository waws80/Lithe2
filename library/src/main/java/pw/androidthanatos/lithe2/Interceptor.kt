package pw.androidthanatos.lithe2

import pw.androidthanatos.lithe2.chain.Chain

/**
 * Lithe网络请求拦截器
 */
interface Interceptor {

    fun interceptor(chain: Chain): Chain
}