package com.aneesha.foodies.activity.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.aneesha.foodies.R
import com.aneesha.foodies.activity.adapter.DashboardFragmentAdapter
import com.aneesha.foodies.activity.model.Restaurant
import com.aneesha.foodies.activity.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment(val contextParam:Context) : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var dashboardAdapter: DashboardFragmentAdapter
    lateinit var etTextSearch:EditText
    lateinit var radioButton:View
    lateinit var fragment_dashboard_dialog:RelativeLayout
    lateinit var dashboard_fragment_no_restaurant:RelativeLayout
    var restaurantInfoList=arrayListOf<Restaurant>()
    var ratingComparator=Comparator<Restaurant>{rest1,rest2->
        if(rest1.restaurantRating.compareTo(rest2.restaurantRating,true)==0){
            rest1.restaurantName.compareTo(rest2.restaurantName,true)
        }else{
            rest1.restaurantRating.compareTo(rest2.restaurantRating,true)
        }
    }
    var costComparator=Comparator<Restaurant>{rest1,rest2->
        rest1.cost_for_one.compareTo(rest2.cost_for_one,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view= inflater.inflate(R.layout.fragment_dashboard, container, false)
        layoutManager=LinearLayoutManager(activity)
        recyclerView=view.findViewById(R.id.recyclerViewDashboard)
        etTextSearch=view.findViewById(R.id.etTextSearch)
        fragment_dashboard_dialog=view.findViewById(R.id.fragment_dashboard_dialog)
        dashboard_fragment_no_restaurant=view.findViewById(R.id.dashboard_fragment_no_restaurant)
        fun filterFun(strTyped:String) {
            val filteredList = arrayListOf<Restaurant>()
            for (item in restaurantInfoList) {
                if (item.restaurantName.toLowerCase(Locale.ROOT)
                        .contains(strTyped.toLowerCase(Locale.ROOT))
                ) {
                    filteredList.add(item)
                }
            }
            if (filteredList.size == 0) {
                dashboard_fragment_no_restaurant.visibility = View.VISIBLE
            } else {
                dashboard_fragment_no_restaurant.visibility = View.INVISIBLE
            }
             dashboardAdapter.filterList(filteredList)
        }
        etTextSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        return view
    }
    fun fetchData(){
        if(ConnectionManager().checkConnectivity(activity as Context))
        {
           fragment_dashboard_dialog.visibility=View.VISIBLE
            try{
                val queue= Volley.newRequestQueue(activity as Context)
                val url="http://13.235.250.119/v2/restaurants/fetch_result"
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,url,null,
                    Response.Listener {
                        val responseJsonObjData = it.getJSONObject("data")
                        val success = responseJsonObjData.getBoolean("success")
                        if(success){
                            val data = responseJsonObjData.getJSONArray("data")
                            for(i in 0 until data.length())
                            {
                                val restaurantJsonObject=data.getJSONObject(i)
                                val restaurantObject= Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)
                                dashboardAdapter=DashboardFragmentAdapter(activity as Context,restaurantInfoList)
                                recyclerView.adapter=dashboardAdapter
                                recyclerView.layoutManager=layoutManager
                            }
                        }
                        fragment_dashboard_dialog.visibility=View.INVISIBLE
                    }
            ,Response.ErrorListener {
                        fragment_dashboard_dialog.visibility=View.INVISIBLE
                        Toast.makeText(activity as Context,"Some error occurred!!", Toast.LENGTH_SHORT).show()

                    })
                {
                    override fun getHeaders():MutableMap<String,String>{
                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="d721c31063faf3"
                        return  headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch(e:JSONException){
                Toast.makeText(activity as Context,"Some Unexpected error occurred!!", Toast.LENGTH_SHORT).show()
            }
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
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alertDialog.create()
            alertDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id){
            R.id.sort->{
                radioButton=View.inflate(contextParam,R.layout.sort_radio_button,null)
              val alertDialog=AlertDialog.Builder(activity as Context)
                alertDialog.setTitle("Sort By?")
                alertDialog.setView(radioButton)
                alertDialog.setPositiveButton("OK"){
                    text,listener->
                    if(radioButton.radio_high_to_low.isChecked){
                        Collections.sort(restaurantInfoList,costComparator)
                        restaurantInfoList.reverse()
                        dashboardAdapter.notifyDataSetChanged()
                    }
                    if(radioButton.radio_low_to_high.isChecked){
                        Collections.sort(restaurantInfoList,costComparator)
                        dashboardAdapter.notifyDataSetChanged()
                    }
                    if(radioButton.radio_rating.isChecked){
                        Collections.sort(restaurantInfoList,ratingComparator)
                        restaurantInfoList.reverse()
                        dashboardAdapter.notifyDataSetChanged()
                    }
                }
                alertDialog.setNegativeButton("CANCEL"){
                    text,listener->

                }
                alertDialog.create()
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            if (restaurantInfoList.isEmpty())
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
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alertDialog.setCancelable(false)
            alertDialog.create()
            alertDialog.show()
        }
        super.onResume()
    }

}
