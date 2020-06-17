package com.aneesha.foodies.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
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
import com.aneesha.foodies.activity.adapter.OrderHistoryAdapter
import com.aneesha.foodies.activity.model.OrderHistoryRestaurant
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException

class OrderHistoryActivity : AppCompatActivity() {
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: OrderHistoryAdapter
    lateinit var recyclerViewOrderHist: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var order_history_dialog: RelativeLayout
    lateinit var order_history_no_order: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        recyclerViewOrderHist = findViewById(R.id.recyclerViewOrderHist)
        toolbar = findViewById(R.id.toolbar)
        order_history_dialog = findViewById(R.id.order_history_dialog)
        order_history_no_order = findViewById(R.id.order_history_no_order)
        setToolBar()
    }

    fun ItemsForResto() {
        layoutManager = LinearLayoutManager(this)
        val restaurantOrderList = ArrayList<OrderHistoryRestaurant>()
        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val user_id = sharedPreferences.getString("user_id", "000")
        if (ConnectionManager().checkConnectivity(this)) {
            order_history_dialog.visibility = View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/orders/fetch_result/" + user_id
                val jsonObjectRequest = object :
                    JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")
                            if (data.length() == 0) {
                                Toast.makeText(this, "No orders placed!!", Toast.LENGTH_SHORT)
                                    .show()
                                order_history_dialog.visibility = View.VISIBLE
                            } else {
                                order_history_dialog.visibility = View.INVISIBLE
                                for (i in 0 until data.length()) {
                                    val restaurantItemJsonObject = data.getJSONObject(i)
                                    val restaurantObject = OrderHistoryRestaurant(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at")
                                            .substring(0, 10)
                                    )
                                    restaurantOrderList.add(restaurantObject)
                                    menuAdapter= OrderHistoryAdapter(this, restaurantOrderList)
                                    recyclerViewOrderHist.adapter = menuAdapter
                                    recyclerViewOrderHist.layoutManager = layoutManager
                                }
                            }
                        }
                        order_history_dialog.visibility = View.INVISIBLE
                    }, Response.ErrorListener {
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
            } catch (e: JSONException) {
                Toast.makeText(this, "Some unexpected  error occurred!!", Toast.LENGTH_SHORT).show()
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

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Previous Orders"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
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
        ItemsForResto()
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