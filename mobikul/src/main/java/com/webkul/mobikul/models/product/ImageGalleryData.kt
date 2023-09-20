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

package com.webkul.mobikul.models.product


import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.regex.Pattern


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ImageGalleryData() : Parcelable {

    @JsonProperty("smallImage")

    var smallImage: String ?= ""

    @JsonProperty("largeImage")

    var largeImage: String ?= ""

    @JsonProperty("dominantColor")

    var dominantColor: String ?= ""

    @JsonProperty("isVideo")

    var isVideo: Boolean = false

    @JsonProperty("videoUrl")

    var videoUrl: String? = ""

    constructor(parcel: Parcel) : this() {
        smallImage = parcel.readString()
        largeImage = parcel.readString()
        dominantColor = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(smallImage)
        parcel.writeString(largeImage)
        parcel.writeString(dominantColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageGalleryData> {
        override fun createFromParcel(parcel: Parcel): ImageGalleryData {
            return ImageGalleryData(parcel)
        }

        override fun newArray(size: Int): Array<ImageGalleryData?> {
            return arrayOfNulls(size)
        }
    }


    fun getYoutubeThumbnailUrlFromVideoUrl(): String {
        return "http://img.youtube.com/vi/" + videoUrl?.let { getYoutubeVideoIdFromUrl(it) } + "/0.jpg"
    }

    fun getYoutubeVideoIdFromUrl(url: String): String? {
        var videoId: String? = null
        val regex = "http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\\n]+)"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            videoId = matcher.group(1)
        }
        return videoId
    }
}