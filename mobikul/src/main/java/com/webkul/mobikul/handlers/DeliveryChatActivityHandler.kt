package com.webkul.mobikul.handlers


import com.google.firebase.database.ServerValue
import com.webkul.mobikul.activities.DeliveryChatActivity
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper
import java.util.*


/**
 * Created by vedesh.kumar on 30/7/17. @Webkul Software Private limited
 */

class DeliveryChatActivityHandler(private val mContext: DeliveryChatActivity) {

    fun onClickSendButton(currentMsg: String?) {
        if (!currentMsg.isNullOrBlank()) {
            val messageRoot = mContext.mDatabaseReference.push().key?.let { mContext.mDatabaseReference.child(it) }
            val map2 = HashMap<String, Any>()
            map2["msg"] = currentMsg.trim ()
            map2["id"] = BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + AppSharedPref.getCustomerId(mContext)
            map2["name"] = AppSharedPref.getCustomerName(mContext)
            map2["timestamp"] = ServerValue.TIMESTAMP
            messageRoot?.updateChildren(map2)
            mContext.mBinding.textMsgEt.setText("")
        }
    }
}
