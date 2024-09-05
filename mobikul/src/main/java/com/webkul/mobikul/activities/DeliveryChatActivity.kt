package com.webkul.mobikul.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import com.libraltraders.android.R
import com.webkul.mobikul.adapters.DeliveryChatMessageAdapter
import com.libraltraders.android.databinding.ActivityDeliveryChatBinding
import com.webkul.mobikul.handlers.DeliveryChatActivityHandler
import com.webkul.mobikul.helpers.*
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_CHAT_IDENTIFIER_OWNER
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_USER_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_USER_NAME
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_SELLER_ID
import com.webkul.mobikul.helpers.BundleKeysHelper.KEY_ACCOUNT_TYPE
import com.webkul.mobikul.models.BaseModel
import com.webkul.mobikul.models.chat.ChatMessage
import com.webkul.mobikul.network.ApiConnection
import com.webkul.mobikul.network.ApiCustomCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class DeliveryChatActivity : BaseActivity() {

    var mUserId: String = ""
    var mUserName: String = ""
    var mAccountType: String = ""
    lateinit var mBinding: ActivityDeliveryChatBinding

    lateinit var mDatabaseReference: DatabaseReference
    private val mMessageList = ArrayList<ChatMessage>()
    private var isFromNotification: Boolean = false

    //mUserId = customerId+sellerId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_chat)
        initSupportActionBar()
        mAccountType = intent.getStringExtra(KEY_ACCOUNT_TYPE)!!
        mUserId = BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + intent.getStringExtra(BUNDLE_KEY_USER_ID)!!
        if (intent.hasExtra(BUNDLE_KEY_USER_NAME)) {
            mUserName = intent.getStringExtra(BUNDLE_KEY_USER_NAME)!!
            mUserId = BUNDLE_KEY_CHAT_IDENTIFIER_CUSTOMER + AppSharedPref.getCustomerId(this) + "+" + BUNDLE_KEY_CHAT_IDENTIFIER_OWNER + mUserId.replace("[^0-9]".toRegex(), "")
        }
        if (intent.hasExtra(BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION)) {
            isFromNotification = intent.getBooleanExtra(BundleKeysHelper.BUNDLE_KEY_FROM_NOTIFICATION, false)
        }
        if (intent.hasExtra(BUNDLE_SELLER_ID)) {
            mUserId += "+" + BUNDLE_KEY_CHAT_IDENTIFIER_OWNER+intent.getStringExtra(BUNDLE_SELLER_ID)
        }
        startInitialization()
    }

    override fun initSupportActionBar() {
        if (intent.hasExtra(BUNDLE_KEY_USER_NAME)) {
            supportActionBar?.title = intent.getStringExtra(BUNDLE_KEY_USER_NAME)
        } else {
            supportActionBar?.title = getString(R.string.admin)
        }
        super.initSupportActionBar()
    }

    private fun startInitialization() {
        if (AppSharedPref.isLoggedIn(this)) {
            val sellerId= intent.getStringExtra(BUNDLE_SELLER_ID)
            ApiConnection.updateTokenOnCloud(this, mAccountType,sellerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : ApiCustomCallback<BaseModel>(this, false) {
                        override fun onNext(baseModel: BaseModel) {
                            super.onNext(baseModel)
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            ToastHelper.showToast(context, NetworkHelper.getErrorMessage(context, e)!!)
                        }
                    })

        }

        mBinding.handler = DeliveryChatActivityHandler(this)
        mBinding.msgRv.adapter = DeliveryChatMessageAdapter(this, mMessageList)
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child("DeliveryApp").child("messages").child(mAccountType).child(mUserId)

        mDatabaseReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (p0 != null)
                    loadChat(p0)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }

    private fun loadChat(dataSnapshot: DataSnapshot) {
        try {
            val i = dataSnapshot.children.iterator()
            while (i.hasNext()) {
                mBinding.isLoading = true

                val chat_user_id = (i.next() as DataSnapshot).value as String?
                val chat_msg = (i.next() as DataSnapshot).value as String?
                val chat_user_name = (i.next() as DataSnapshot).value as String?
                val timeStampValue = (i.next() as DataSnapshot).value as Long
                val displayDateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.getDefault())
                val displayDayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

                val calendar = Calendar.getInstance()
                var currentDate = displayDateFormat.format(calendar.time)
                currentDate = currentDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                calendar.timeInMillis = timeStampValue

                var displayDay = displayDayFormat.format(calendar.time)
                val dispalyDateTimeValue = displayDateFormat.format(calendar.time)

                val dispalyDateTimeValueArray = dispalyDateTimeValue.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val displayDate = dispalyDateTimeValueArray[0]
                val displayTime = dispalyDateTimeValueArray[1] + " " + dispalyDateTimeValueArray[2]

                if (currentDate.matches(displayDate.toRegex())) {
                    displayDay = ""
                }

                val chatMessage = ChatMessage(chat_user_id, chat_msg, chat_user_name, displayDay, displayTime, displayDate)

                mMessageList.add(chatMessage)
            }
            mBinding.isLoading = false
            mBinding.msgRv.adapter!!.notifyDataSetChanged()
            mBinding.msgRv.scrollToPosition(mBinding.msgRv.adapter!!.itemCount - 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (isFromNotification) {
            val intent = Intent(this, (application as MobikulApplication).getHomePageClass())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }
}