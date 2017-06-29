package pw.androidthanatos.lithe2.chain

/**
 * Lithe网络请求拦截信息
 */
data class Chain (var url: String,
                  var method: String,
                  var head: HashMap<String,String>,
                  var body:ByteArray,
                  var params: HashMap<String,String>)