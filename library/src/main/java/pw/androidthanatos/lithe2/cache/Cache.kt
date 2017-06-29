package pw.androidthanatos.lithe2.cache

/**
 * Lithe缓存接口
 */
interface Cache {

    fun set(key: String, value: Any)

    fun get(key: String): Any?
}