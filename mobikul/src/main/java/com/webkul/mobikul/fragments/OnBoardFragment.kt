package com.webkul.mobikul.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.libraltraders.android.R
import com.libraltraders.android.databinding.FragmentOnBoardBinding
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.Utils
import com.webkul.mobikul.helpers.darken
import com.webkul.mobikul.models.homepage.OnBoardListData

class OnBoardFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentOnBoardBinding

    companion object {
        fun newInstance(data: OnBoardListData): OnBoardFragment {
            val addressListFragment = OnBoardFragment()
            val bundle = Bundle()
            bundle.putParcelable(BundleKeysHelper.BUNDLE_KEY_ON_BOARD_DATA, data)
            addressListFragment.arguments = bundle
            return addressListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_board, container, false)
        return mContentViewBinding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.data = requireArguments().getParcelable(BundleKeysHelper.BUNDLE_KEY_ON_BOARD_DATA)
//        loadImage(mContentViewBinding.onBoardIv, mContentViewBinding.data?.image, mContentViewBinding.data?.imageDominantColor)
    }


    private fun loadImage(view: ImageView, imageUrl: String?, placeholder: String?) {
        Log.d("Tag", "load==> $imageUrl")
        if (placeholder.isNullOrBlank()) {
            Glide.with(view.context)
                    .load(imageUrl ?: "")
//                    .override(Utils.screenWidth - 300, Utils.screenWidth - 300)
                    .thumbnail(0.01f)
                    .timeout(2 * 60000)
                    .error(R.drawable.placeholder)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.placeholder))
                    .into(view)

        } else {
            Glide.with(view.context)
                    .load(imageUrl ?: "")
//                    .override(Utils.screenWidth - 300, Utils.screenWidth - 300)
                    .thumbnail(0.01f)
                    .timeout(2 * 60000)
//                    .error(ColorDrawable(Color.parseColor(placeholder)))
//                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions()
                            .placeholder(ColorDrawable(darken(placeholder,0.9))))
                    .into(view)

        }

    }

}
