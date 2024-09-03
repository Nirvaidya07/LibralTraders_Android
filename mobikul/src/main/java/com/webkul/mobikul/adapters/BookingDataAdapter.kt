package com.webkul.mobikul.adapters

import android.view.LayoutInflater
import android.view.View 
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.R
import com.webkul.mobikul.models.BookingData

class BookingDataAdapter(private val mList: List<BookingData>) : RecyclerView.Adapter<BookingDataAdapter.ViewHolder>() {

	// create new views 
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { 
		// inflates the card_view_design view 
		// that is used to hold list item 
		val view = LayoutInflater.from(parent.context) 
			.inflate(R.layout.item_booking_data, parent, false)

		return ViewHolder(view) 
	} 

	// binds the list items to a view 
	override fun onBindViewHolder(holder: ViewHolder, position: Int) { 

		val ItemsViewModel = mList[position] 

		holder.txtKey.setText(ItemsViewModel.key)
		holder.txtValue.setText(ItemsViewModel.value)

	} 

	// return the number of the items in the list 
	override fun getItemCount(): Int { 
		return mList.size 
	} 

	// Holds the views for adding it to image and text 
	class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) { 
		val txtKey: TextView = itemView.findViewById(R.id.txt_key)
		val txtValue: TextView = itemView.findViewById(R.id.txt_value)
	} 
}