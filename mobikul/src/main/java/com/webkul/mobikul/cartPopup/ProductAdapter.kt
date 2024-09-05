package com.webkul.mobikul.cartPopup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.libraltraders.android.databinding.ItemProductBinding

class ProductAdapter(private val products: List<ProductItem>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductItem) {
            binding.product = product
            binding.executePendingBindings()
        }
    }
}
