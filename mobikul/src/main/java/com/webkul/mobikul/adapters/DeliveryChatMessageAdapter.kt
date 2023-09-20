package com.webkul.mobikul.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.webkul.mobikul.R
import com.webkul.mobikul.databinding.DeliveryChatMessageBinding
import com.webkul.mobikul.helpers.AppSharedPref
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER
import com.webkul.mobikul.models.chat.ChatMessage


/**
 * Created by vedesh.kumar on 30/7/17.
 */

class DeliveryChatMessageAdapter(private val mContext: Context, private val messageList: List<ChatMessage>) : RecyclerView.Adapter<DeliveryChatMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val messageView = inflater.inflate(R.layout.delivery_chat_message, parent, false)
        return ViewHolder(messageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = messageList[position]
        if (chatMessage.id!!.equals(BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + AppSharedPref.getCustomerId(mContext), ignoreCase = true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                holder.mBinding!!.mainContainer.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            holder.mBinding!!.message.setBackgroundResource(R.drawable.bubble_right)
            holder.mBinding.chatMessageIv.visibility = View.GONE
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                holder.mBinding!!.mainContainer.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            holder.mBinding!!.message.setBackgroundResource(R.drawable.bubble_left)
            holder.mBinding.chatMessageIv.visibility = View.VISIBLE
        }
        holder.mBinding.data = chatMessage
        holder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding: DeliveryChatMessageBinding? = DataBindingUtil.bind(itemView)
    }
}
