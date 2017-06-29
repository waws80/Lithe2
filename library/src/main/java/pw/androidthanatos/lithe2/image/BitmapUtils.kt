package pw.androidthanatos.lithe2.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View
import android.widget.ImageView;

/**
 * 图片处理类
 */

object BitmapUtils {


    private fun getBitmap(path: String , view: View):Bitmap {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds=true
        BitmapFactory.decodeFile(path,options)
        val imageSize = ImageSizeUtil.getImageViewSize(view)
        options.inSampleSize= ImageSizeUtil.caculateInSampleSize(options,imageSize.width!!,imageSize.height!!)
        options.inJustDecodeBounds=false
        return BitmapFactory.decodeFile(path,options)
    }

    private fun  getBitmapFromByte( path: ByteArray, view: View): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds=true
        BitmapFactory.decodeByteArray(path,0,path.size)
        val imageSize = ImageSizeUtil.getImageViewSize(view)
        options.inSampleSize= ImageSizeUtil.caculateInSampleSize(options,imageSize.width!!,imageSize.height!!)
        options.inJustDecodeBounds=false
        return  BitmapFactory.decodeByteArray(path,0,path.size)
    }


     fun getNetBitmap(byteArray: ByteArray,  view: View): Bitmap? =  getBitmapFromByte(byteArray,view)


     fun getLocalBitmap( path: String,  view: View): Bitmap? = getBitmap(path,view)


}
