package com.aneesha.foodies.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.adapter.RestaurantMenuAdapter
import com.aneesha.foodies.activity.database.RestaurantEntity
import com.aneesha.foodies.activity.fragment.DashboardFragment
import com.aneesha.foodies.activity.fragment.forgot_password_dialog
import com.aneesha.foodies.activity.model.RestaurantMenu
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
     var restaurantId:String?="10"
     var restaurantName:String?="Pind Tadka"
    lateinit var proceedToCart:RelativeLayout
    lateinit var btnProceedToCart:Button
    lateinit var restaurant_menu_dialog:RelativeLayout
    var restaurantMenuList=arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        proceedToCart=findViewById(R.id.proceedToCart)
        btnProceedToCart=findViewById(R.id. btnProceedToCart)
        restaurant_menu_dialog=findViewById(R.id. restaurant_menu_dialog)
        toolbar=findViewById(R.id.toolbar)
        restaurantId=intent.getStringExtra("restaurant_id")
        restaurantName=intent.getStringExtra("name")
        toolbar.title=restaurantName
        layoutManager=LinearLayoutManager(this)
        recyclerView=findViewById(R.id.recyclerViewRestaurantMenu)

    }
    fun fetchData(){
        if(ConnectionManager().checkConnectivity(this)){
            restaurant_menu_dialog.visibility= View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(this)
                restaurantId=intent.getStringExtra("restaurant_id")
                restaurantName=intent.getStringExtra("name")
                setToolBar()
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"+restaurantId
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            restaurantMenuList.clear()
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restJsonObject = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    restJsonObject.getString("id"),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("cost_for_one")
                                )
                                restaurantMenuList.add(menuObject)
                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCart,
                                    btnProceedToCart,
                                    restaurantMenuList
                                )
                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager
                            }
                        }
                        restaurant_menu_dialog.visibility = View.INVISIBLE

                    }, Response.ErrorListener {

                        restaurant_menu_dialog.visibility = View.INVISIBLE
                        Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "d721c31063faf3"
                            return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                Toast.makeText(this, "Some unexpected  error occurred!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            val alertDialog= AlertDialog.Builder(this)
            alertDialog.setTitle("No access to internet")
            alertDialog.setMessage("Internet connection cannot establish")
            alertDialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            alertDialog.setNegativeButton("Exit"){
                    text,listener ->
                ActivityCompat.finishAffinity(this)
            }
            alertDialog.setCancelable(false)
            alertDialog.create()
            alertDialog.show()
        }
    }
    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onBackPressed() {
        if(menuAdapter.getSelectedItemCount()>0){
            val alertDialog= AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!")
            alertDialog.setMessage("Not saved")
            alertDialog.setPositiveButton("Okay!!"){
                    text,listener-> super.onBackPressed()
        }
            alertDialog.setNegativeButton("No"){text,listener->

            }
            alertDialog.show()
        }else {
            super.onBackPressed()
        }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id){
            R.id.home->{
                if(menuAdapter.getSelectedItemCount()>0){
                    val alertDialog= AlertDialog.Builder(this)
                    alertDialog.setTitle("Alert!")
                    alertDialog.setMessage("Not saved")
                    alertDialog.setPositiveButton("Okay!!"){
                            text,listener-> super.onBackPressed()
                    }
                    alertDialog.setNegativeButton("No"){text,listener->

                    }
                    alertDialog.show()
                }else {
                    super.onBackPressed()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        if(ConnectionManager().checkConnectivity(this)){
            if(restaurantMenuList.isEmpty())
                fetchData()
        }else{
            val alertDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alertDialog.setTitle("No access to internet")
            alertDialog.setMessage("Internet connection cannot establish")
            alertDialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            alertDialog.setNegativeButton("Exit"){
                    text,listener ->
                ActivityCompat.finishAffinity(this)
            }
            alertDialog.setCancelable(false)
            alertDialog.create()
            alertDialog.show()
        }
        super.onResume()
    }
}
