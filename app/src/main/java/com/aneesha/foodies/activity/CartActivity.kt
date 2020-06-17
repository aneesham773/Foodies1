package com.aneesha.foodies.activity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.adapter.CartAdapter
import com.aneesha.foodies.activity.model.CartItems
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var txtOrderFrom: TextView
    lateinit var toolbar: Toolbar
    lateinit var btnOrderPlace: Button
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: CartAdapter
    var restaurantId: String?="10"
     var restaurantName: String?="Pind Tadka"
    lateinit var llCart: LinearLayout
    lateinit var cart_dialog: RelativeLayout
    lateinit var selectedItemId: ArrayList<String>
    var totalAmt = 0
    var cartItems = arrayListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        btnOrderPlace = findViewById(R.id.btnOrderPlace)
        llCart = findViewById(R.id.llCart)
        txtOrderFrom = findViewById(R.id.txtOrderFrom)
        toolbar = findViewById(R.id.toolbar)
        cart_dialog = findViewById(R.id.cart_dialog)
        restaurantId = intent.getStringExtra("restaurant_id")
        restaurantName = intent.getStringExtra("name")
        selectedItemId = intent.getStringArrayListExtra("id")
        txtOrderFrom.text = restaurantName
        btnOrderPlace.setOnClickListener(View.OnClickListener {
            val sharedPreferences = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )
            if (ConnectionManager().checkConnectivity(this)) {
                cart_dialog.visibility = View.VISIBLE
                try {
                    val restJsonArray = JSONArray()
                    for (foodItem in selectedItemId) {
                        val itemObject = JSONObject()
                        itemObject.put("food_item_id", foodItem)
                        restJsonArray.put(itemObject)
                    }
                    val sendOrder = JSONObject()
                    sendOrder.put("user_id", sharedPreferences.getString("user_id", "0"))
                    sendOrder.put("restaurant_id", restaurantId.toString())
                    sendOrder.put("total_cost", totalAmt)
                    sendOrder.put("food", restJsonArray)
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/place_order/fetch_result"
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, sendOrder,
                        Response.Listener {
                            val responseJsonObjectData = it.getJSONObject("data")
                            val success = responseJsonObjectData.getBoolean("success")
                            if (success) {
                                Toast.makeText(
                                    this@CartActivity,
                                    "ORDER PLACED",
                                    Toast.LENGTH_SHORT
                                ).show()
                                createNotification()
                                val intent = Intent(this, OrderPlacedActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            } else {
                                val responseMsg = responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this@CartActivity,
                                    responseMsg.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            cart_dialog.visibility = View.INVISIBLE
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@CartActivity,
                                "Some error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "d721c31063faf3"
                            return headers
                        }
                    }
                    jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(20000, 1, 1f))
                    queue.add(jsonObjectRequest)
                } catch (e: JSONException) {
                    Toast.makeText(
                        this@CartActivity,
                        "Some unexpected error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("No access to internet")
                alertDialog.setMessage("Internet connection cannot establish")
                alertDialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }
                alertDialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                alertDialog.setCancelable(false)
                alertDialog.create()
                alertDialog.show()
            }
        })
        setToolBar()
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewCart)
    }

    fun fetchData() {
        if (ConnectionManager().checkConnectivity(this)) {
            cart_dialog.visibility = View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"+restaurantId
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        val responseJsonObjData = it.getJSONObject("data")
                        val success = responseJsonObjData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjData.getJSONArray("data")
                            totalAmt = 0
                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)
                                if (selectedItemId.contains(cartItemJsonObject.getString("id"))) {
                                    val menuObject = CartItems(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )
                                    totalAmt += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()
                                    cartItems.add(menuObject)
                                }
                                menuAdapter = CartAdapter(this, cartItems)
                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager
                            }
                            btnOrderPlace.text = "Place order(Total :Rs." + totalAmt + ")"
                        }
                        cart_dialog.visibility = View.INVISIBLE
                    }, Response.ErrorListener {
                        Toast.makeText(this@CartActivity, "Some error occurred", Toast.LENGTH_SHORT)
                            .show()
                        cart_dialog.visibility = View.INVISIBLE
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
                Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
            }
        } else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("No access to internet")
            alertDialog.setMessage("Internet connection cannot establish")
            alertDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            alertDialog.setNegativeButton("Exit") { text, listener ->
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        if(ConnectionManager().checkConnectivity(this)){
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
    fun createNotification(){
        val notifiId=1
        val channelId="personal_notification"
        val notificationBuilder= NotificationCompat.Builder(this@CartActivity,channelId)
        notificationBuilder.setSmallIcon(R.drawable.ic_default_image_restaurant)
        notificationBuilder.setContentTitle("Order Placed")
        notificationBuilder.setContentText("Your order has been placed successfully placed!")
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText("Ordered from"+restaurantName+"and amounting to Rs."+totalAmt))
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManagerCompat=NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notifiId,notificationBuilder.build())
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name="Order Placed"
            val description="Your order has been successfully placed"
            val imp=NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel=NotificationChannel(channelId,name,imp)
            notificationChannel.description=description
            val notificationManager=(getSystemService(Context.NOTIFICATION_SERVICE))as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}