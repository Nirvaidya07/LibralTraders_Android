package com.webkul.mobikul.activities

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.libraltraders.android.R
import com.libraltraders.android.databinding.ActivityMediaPlayerWebviewBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_MEDIA_URL
import java.util.regex.Pattern


class MediaPlayerWebviewActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMediaPlayerWebviewBinding
    internal var isFirstCall = true
    private var progress: ProgressDialog? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_media_player_webview)

        mBinding.webview.settings.pluginState = WebSettings.PluginState.ON
        mBinding.webview.settings.javaScriptEnabled = true
        mBinding.webview.settings.safeBrowsingEnabled=true
        // mBinding.webview.setInitialScale(1);
        mBinding.webview.settings.loadWithOverviewMode = true
        mBinding.webview.settings.useWideViewPort = true
        if (Build.VERSION.SDK_INT >= 17) {
            mBinding.webview.settings.mediaPlaybackRequiresUserGesture = false
        }


        mBinding.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (isFirstCall) {
                    progress = ProgressDialog.show(this@MediaPlayerWebviewActivity, null, getString(R.string.loading), true)
                    progress!!.setCanceledOnTouchOutside(false)
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progress != null)
                    progress!!.dismiss()
                isFirstCall = false
            }
        }
        mBinding.webview.webChromeClient = WebChromeClient()


        val videoID = intent.getStringExtra(BUNDLE_KEY_MEDIA_URL)?.let { getVideoIdFromYoutubeUrl(it) }

        Log.d("TEST_LOG", "VID==>${videoID}")

        val webContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body bgcolor=\"#000000\" style=\"padding:0px;margin: auto;border:0px;\">" +
                "<div  style=\" position: relative;width: 100%;height: 0;padding-bottom: 56.25%;\">" +
                "<iframe class=\"youtube-player\"" +
                "marginwidth=\"0\"" +
                "marginheight=\"0\"" +
                "hspace=\"0\"" +
                "vspace=\"0\"" +
                "frameborder=\"0\"" +
                "scrolling=\"no\"" +
                "display=\"block\"" +
                "style=\"position: absolute;top: 0;left: 0;width:100%;height: 100%;\"" + "id=\"ytplayer\" type=\"text/html\" " +
                "src=\"http://www.youtube.com/embed/" + videoID + "?&theme=dark&autohide=2&modestbranding=1&enablejsapi=1&rel=0&showinfo=0&autoplay=1&fs=0\""+ "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n"+
        "</div>"+ "</body>\n"+ "</html>\n"

        if (videoID != null) {
            Log.d("TEST_LOG", "videoID != null==>${videoID}")
//            mBinding.webview.loadData(getHTML(videoID), "text/html", "utf-8")
            mBinding.webview.loadDataWithBaseURL("", webContent, "text/html; charset=utf-8", "UTF-8", "")
        } else {
            Log.d("TEST_LOG", "videoID == null==>${videoID}")
            intent.getStringExtra(BUNDLE_KEY_MEDIA_URL)?.let { mBinding.webview.loadUrl(it) }
        }


    }

    fun getHTML(videoId: String): String {

        return ("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body bgcolor=\"#000000\" style=\"padding:0px;margin: auto;border:0px;\">" +
                "<div  style=\" position: relative;width: 100%;height: 0;padding-bottom: 56.25%;\">" +
                "<iframe class=\"youtube-player\"" +
                "marginwidth=\"0\"" +
                "marginheight=\"0\"" +
                "hspace=\"0\"" +
                "vspace=\"0\"" +
                "frameborder=\"0\"" +
                "scrolling=\"no\"" +
                "display=\"block\"" +
                "style=\"position: absolute;top: 0;left: 0;width:100%;height: 100%;\""

                + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"http://www.youtube.com/embed/" + videoId
                + "?&theme=dark&autohide=2&modestbranding=1&enablejsapi=1&rel=0&showinfo=0&autoplay=1&fs=0\""
                + "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n"
                +
                "</div>"
                + "</body>\n"
                + "</html>\n")
    }

    fun getVideoIdFromYoutubeUrl(url: String): String? {
        var videoId: String? = null
        val regex = "http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\\n]+)"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            videoId = matcher.group(1)
        }
        return videoId
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onPause() {
        super.onPause()
        if (progress != null) {
            progress!!.dismiss()
        }
    }
}




