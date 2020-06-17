package com.aneesha.foodies.activity.adapter

import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.model.CartItems
import com.aneesha.foodies.activity.model.OrderHistoryRestaurant
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val restaurantOrderList:ArrayList<OrderHistoryRestaurant>):RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>(){
    class ViewHolderOrderHistoryRestaurant(view: View):RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtDate:TextView=view.findViewById(R.id.txtDate)
        val recyclerViewItemsOrdered:RecyclerView=view.findViewById(R.id.recyclerViewItemsOrdered)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistoryRestaurant {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.order_history_recycler_view,parent,false)
        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return restaurantOrderList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {
    val restaurantObject=restaurantOrderList[position]
        holder.txtRestaurantName.text=restaurantObject.restaurantName
        var date=restaurantObject.orderPlacedAt
        date=date.replace("-","/")
        date=date.substring(0,6)+"20"+date.substring(6,8)
        holder.txtDate.text=date
        val layoutManager=LinearLayoutManager(context)
        var orderItemAdapter:CartAdapter
        if (ConnectionManager().checkConnectivity(context)){
            try{
                val ItemsPerRestaurant=ArrayList<CartItems>()
                val sharedPreferences=context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE)
                val user_id=sharedPreferences.getString("user_id","0")
                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/"+user_id
                orderItemAdapter=CartAdapter(context,ItemsPerRestaurant)
                holder.recyclerViewItemsOrdered.adapter=orderItemAdapter
                holder.recyclerViewItemsOrdered.layoutManager=layoutManager
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        val responseJsonObjData = it.getJSONObject("data")
                        val success = responseJsonObjData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjData.getJSONArray("data")
                            val fetchedObj = data.getJSONObject(position)
                            val foodOrderedArray = fetchedObj.getJSONArray("food_items")
                            for (j in 0 until foodOrderedArray.length()) {
                                val eachFoodItem = foodOrderedArray.getJSONObject(j)
                                val itemObject = CartItems(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"), "000"
                                )
                                ItemsPerRestaurant.add(itemObject)
                            }
                            orderItemAdapter = CartAdapter(context, ItemsPerRestaurant)
                            holder.recyclerViewItemsOrdered.adapter=orderItemAdapter
                            holder.recyclerViewItemsOrdered.layoutManager=layoutManager
                        } },Response.ErrorListener {
                           Toast.makeText(context,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d721c31063faf3"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

    }catch (e:JSONException){
                Toast.makeText(context, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
            }
            }
        }
}