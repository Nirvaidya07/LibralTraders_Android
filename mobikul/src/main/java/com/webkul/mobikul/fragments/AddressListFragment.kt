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

package com.webkul.mobikul.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.webkul.mobikul.R
import com.webkul.mobikul.adapters.CheckoutShippingAddressRvAdapter
import com.webkul.mobikul.databinding.FragmentAddressListBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CHECKOUT_ADDRESS_LIST
import com.webkul.mobikul.models.checkout.CheckoutAddressInfoResponseModel

class AddressListFragment : BaseFragment() {

    lateinit var mContentViewBinding: FragmentAddressListBinding
    private var mCheckoutAddressList: CheckoutAddressInfoResponseModel = CheckoutAddressInfoResponseModel()

    companion object {
        fun newInstance(checkoutAddressList: CheckoutAddressInfoResponseModel): AddressListFragment {
            val addressListFragment = AddressListFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_KEY_CHECKOUT_ADDRESS_LIST, checkoutAddressList)
            addressListFragment.arguments = bundle
            return addressListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContentViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_list, container, false)
        mCheckoutAddressList = requireArguments().getParcelable(BUNDLE_KEY_CHECKOUT_ADDRESS_LIST)
                ?: CheckoutAddressInfoResponseModel()
        return mContentViewBinding.root
    }


   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startInitialization()
    }

    private fun startInitialization() {
        mContentViewBinding.checkoutAddressRv.adapter = mCheckoutAddressList.addressData?.let { CheckoutShippingAddressRvAdapter(this, it) }
        mContentViewBinding.checkoutAddressRv.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
    }
}