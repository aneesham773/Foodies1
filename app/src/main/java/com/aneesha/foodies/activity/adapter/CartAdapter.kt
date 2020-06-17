package com.aneesha.foodies.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.model.CartItems

class CartAdapter(val context: Context,val cartItems:ArrayList<CartItems>):RecyclerView.Adapter<CartAdapter.ViewHolderCart>(){
    class ViewHolderCart(view: View):RecyclerView.ViewHolder(view){
        val txtOrderItem: TextView =view.findViewById(R.id.txtOrderItem)
        val txtItemPrice:TextView=view.findViewById(R.id.txtItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.cart_recycler_view_single_row,parent,false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject=cartItems[position]
        holder.txtOrderItem.text=cartItemObject.itemName
        holder.txtItemPrice.text="Rs."+cartItemObject.itemPrice
    }
}