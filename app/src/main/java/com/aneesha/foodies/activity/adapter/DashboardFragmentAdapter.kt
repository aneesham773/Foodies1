package com.aneesha.foodies.activity.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.RestaurantMenuActivity
import com.aneesha.foodies.activity.database.RestaurantDatabase
import com.aneesha.foodies.activity.database.RestaurantEntity
import com.aneesha.foodies.activity.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardFragmentAdapter (val context: Context,var itemList:ArrayList<Restaurant>):RecyclerView.Adapter<DashboardFragmentAdapter.ViewHolderDashboard>() {

    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCostPerPerson: TextView = view.findViewById(R.id.txtCostPerPerson)
        val txtFavourite: TextView = view.findViewById(R.id.txtFavourite)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_recycler_view_single_row, parent, false)
        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {
        val restaurant = itemList[position]
        val restaurantEntity = RestaurantEntity(restaurant.restaurantId, restaurant.restaurantName,restaurant.cost_for_one,restaurant.restaurantRating,restaurant.restaurantImage)
        holder.txtFavourite.setOnClickListener(View.OnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()

                if (result) {

                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()

                    holder.txtFavourite.setTag("liked")//new value
                    holder.txtFavourite.background=context.resources.getDrawable(R.drawable.ic_favourite)
                } else {

                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()

                }

            } else {

                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()

                if (result) {

                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()

                    holder.txtFavourite.tag = "Not liked"
                    holder.txtFavourite.background=context.resources.getDrawable(R.drawable.ic_favourite_outline)
                } else {

                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()

                }

            }
        })
          holder.llContent.setOnClickListener(View.OnClickListener {
              val intent=Intent(context,RestaurantMenuActivity::class.java)
              intent.putExtra("restaurant_id",holder.txtRestaurantName.getTag().toString())
              intent.putExtra("name",holder.txtRestaurantName.text.toString())
              context.startActivity(intent)
          })
        holder.txtRestaurantName.tag = restaurant.restaurantId + ""
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtCostPerPerson.text= restaurant.cost_for_one + "/person"
        holder.txtRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_default_image_restaurant)
            .into(holder.imgRestaurant)

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.txtFavourite.tag = "liked"
            holder.txtFavourite.background=context.resources.getDrawable(R.drawable.ic_favourite)

        } else {
            holder.txtFavourite.tag = "Not liked"
            holder.txtFavourite.background=context.resources.getDrawable(R.drawable.ic_favourite_outline)
        }
    }
    fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }
    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false

            }
        }
    }
}