/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import android.content.Intent
import android.view.View
import com.webkul.mobikul.activities.FullScreenImageScrollActivity
import com.webkul.mobikul.activities.MediaPlayerWebviewActivity
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_MEDIA_URL
import com.webkul.mobikul.models.product.ImageGalleryData


class ProductSliderHandler(val mListData: ArrayList<ImageGalleryData>) {

    fun onClickProductImage(view: View, productName: String, currentPosition: Int, isVideo: Boolean, videoUrl: String) {
        if (isVideo) {
            val intent = Intent(view.context, MediaPlayerWebviewActivity::class.java)
            intent.putExtra(BUNDLE_KEY_MEDIA_URL, videoUrl)
            view.context.startActivity(intent)
        } else {
            try {
                val largeImageList = java.util.ArrayList<String>()
                val smallImageList = java.util.ArrayList<String>()
                for (eachImageGalleryItem in mListData) {
                    eachImageGalleryItem.largeImage?.let { largeImageList.add(it) }
                    eachImageGalleryItem.smallImage?.let { smallImageList.add(it) }
                }
                val intent = Intent(view.context, FullScreenImageScrollActivity::class.java)
                intent.putExtra(FullScreenImageScrollActivity.KEY_CURRENT_ITEM_POSITION, currentPosition)
                intent.putParcelableArrayListExtra(FullScreenImageScrollActivity.KEY_IMAGE_GALLERY_DATA, mListData)
                intent.putStringArrayListExtra(FullScreenImageScrollActivity.KEY_SMALL_IMAGE_GALLERY_DATA, smallImageList)
                intent.putStringArrayListExtra(FullScreenImageScrollActivity.KEY_LARGE_IMAGE_GALLERY_DATA, largeImageList)
                view.context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}