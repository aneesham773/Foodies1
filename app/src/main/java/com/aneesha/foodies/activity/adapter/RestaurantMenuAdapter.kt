package com.aneesha.foodies.activity.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.CartActivity
import com.aneesha.foodies.activity.model.RestaurantMenu

class RestaurantMenuAdapter(val context:Context, val restaurantId:String?, val restaurantName:String?, val proceedToCart:RelativeLayout, val btnProceedToCart: Button, val restaurantMenu:ArrayList<RestaurantMenu> ):RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {
   var itemSelectedCount:Int=0
    lateinit var proceedToCart1: RelativeLayout
    var itemSelectedId=arrayListOf<String>()
    class ViewHolderRestaurantMenu(view: View):RecyclerView.ViewHolder(view){
        val txtSerialNo:TextView=view.findViewById(R.id.txtSerialNo)
        val txtItemName:TextView=view.findViewById(R.id.txtItemName)
        val txtItemPrice:TextView=view.findViewById(R.id.txtItemPrice)
        val btnAddToCart:TextView=view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.restaurant_menu_recycler_view_single_row,parent,false)
        return  ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
       return  restaurantMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenuItem=restaurantMenu[position]
        proceedToCart1=proceedToCart
        btnProceedToCart.setOnClickListener(View.OnClickListener {
            val intent= Intent(context, CartActivity::class.java)
            intent.putExtra("restaurant_id",restaurantId.toString())
            intent.putExtra("name",restaurantName)
            intent.putExtra("id",itemSelectedId)
            context.startActivity(intent)
        })
        holder.btnAddToCart.setOnClickListener(View.OnClickListener {
            if(holder.btnAddToCart.text.toString().equals("Remove")){
                itemSelectedCount--
                itemSelectedId.remove(holder.btnAddToCart.getTag().toString())
                holder.btnAddToCart.text="Add"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(244,67,54))
            }else{
                itemSelectedCount++
                itemSelectedId.add(holder.btnAddToCart.getTag().toString())
                holder.btnAddToCart.text="Remove"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(255,196,0))
            }
            if(itemSelectedCount>0){
                proceedToCart1.visibility=View.VISIBLE
            }else{
                proceedToCart1.visibility=View.INVISIBLE
            }
        })
        holder.btnAddToCart.setTag(restaurantMenuItem.id+"")
        holder.txtSerialNo.text=(position+1).toString()
        holder.txtItemName.text=restaurantMenuItem.name
        holder.txtItemPrice.text="Rs."+restaurantMenuItem.cost_for_one
    }
    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }


}