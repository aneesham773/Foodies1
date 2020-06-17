package com.aneesha.foodies.activity.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.aneesha.foodies.R
import com.aneesha.foodies.activity.adapter.DashboardFragmentAdapter
import com.aneesha.foodies.activity.database.RestaurantDatabase
import com.aneesha.foodies.activity.database.RestaurantEntity
import com.aneesha.foodies.activity.model.Restaurant
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteRestaurantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteRestaurantFragment (val contextParam:Context): Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favouriteAdapter: DashboardFragmentAdapter
    lateinit var favourite_rest_dialog: RelativeLayout
    var restaurantInfoList = arrayListOf<Restaurant>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFavRest)
        layoutManager = LinearLayoutManager(activity)
        favourite_rest_dialog = view.findViewById(R.id.favourite_rest_dialog)
        return view
    }

    fun fetchData() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            favourite_rest_dialog.visibility = View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            restaurantInfoList.clear()
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantObjJson = data.getJSONObject(i)
                                val restaurantEntity = RestaurantEntity(
                                    restaurantObjJson.getString("id"),
                                    restaurantObjJson.getString("name"),
                                    restaurantObjJson.getString("cost_for_one"),
                                    restaurantObjJson.getString("rating"),
                                    restaurantObjJson.getString("image_url")
                                )
                                if (DBAsyncTask(
                                        contextParam,
                                        restaurantEntity,
                                        1
                                    ).execute().get()
                                ) {
                                    val restaurantObj = Restaurant(
                                        restaurantObjJson.getString("id"),
                                        restaurantObjJson.getString("name"),
                                        restaurantObjJson.getString("rating"),
                                        restaurantObjJson.getString("cost_for_one"),
                                        restaurantObjJson.getString("image_url")
                                    )
                                    restaurantInfoList.add(restaurantObj)
                                    favouriteAdapter = DashboardFragmentAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )
                                    recyclerView.adapter = favouriteAdapter
                                    recyclerView.layoutManager = layoutManager
                                }
                            }
                            if (restaurantInfoList.size == 0) {
                                Toast.makeText(
                                    activity as Context,
                                    "Nothing added to favourites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        favourite_rest_dialog.visibility = View.INVISIBLE
                    }, Response.ErrorListener {
                        Toast.makeText(
                            activity as Context,
                            "Some error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        favourite_rest_dialog.visibility = View.INVISIBLE
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d721c31063faf3"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                Toast.makeText(
                    activity as Context,
                    "Some unexpected  error occurred!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val alertDialog = AlertDialog.Builder(activity as Context)
            alertDialog.setTitle("No access to internet")
            alertDialog.setMessage("Internet connection cannot establish")
            alertDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            alertDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alertDialog.setCancelable(false)
            alertDialog.create()
            alertDialog.show()
        }
    }
    class DBAsyncTask(val context: Context,val restaurantEntity: RestaurantEntity,val mode:Int):
            AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurant-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
                    val restaurant:RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant!=null

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
    override fun onResume() {
        if(ConnectionManager().checkConnectivity(activity as Context)){
            fetchData()
        }else{
            val alertDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alertDialog.setTitle("No access to internet")
            alertDialog.setMessage("Internet connection cannot establish")
            alertDialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            alertDialog.setNegativeButton("Exit"){
                    text,listener ->
                ActivityCompat.finishAffinity(activity as Activity )
            }
            alertDialog.setCancelable(false)
            alertDialog.create()
            alertDialog.show()
        }
        super.onResume()
    }
}