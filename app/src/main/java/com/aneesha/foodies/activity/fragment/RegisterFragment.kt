package com.aneesha.foodies.activity.fragment

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.aneesha.foodies.R
import com.aneesha.foodies.activity.DashboardActivity
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment(val contextParam: Context) : Fragment() {

    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobileNo:EditText
    lateinit var etAddress:EditText
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnRegister:Button
    lateinit var toolbar: Toolbar
    lateinit var register_dialog:RelativeLayout
    var insertSuccess=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_register, container, false)
        etName=view.findViewById(R.id.etName)
        etEmail=view.findViewById(R.id.etEmail)
        etMobileNo=view.findViewById(R.id.etMobileNo)
        etAddress=view.findViewById(R.id.etAddress)
        etPassword=view.findViewById(R.id.etPassword)
        etConfirmPassword=view.findViewById(R.id.etConfirmPassword)
        btnRegister=view.findViewById(R.id.btnRegister)
        toolbar=view.findViewById(R.id.toolbar)
        register_dialog=view.findViewById(R.id.register_dialog)
        toolbar.title="Register Yourself"
        btnRegister.setOnClickListener(View.OnClickListener {
            registerUser()
        })

        return view
    }
    fun userSuccessfullyRegistered(){
        openDashboard()
    }
    fun openDashboard(){
        val intent= Intent(activity as Context,DashboardActivity::class.java)
        startActivity(intent)
        getActivity()?.finish()
    }
    fun registerUser(){
        val sharedPreferences=contextParam.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("logged in ",false).apply()
        if(ConnectionManager().checkConnectivity(activity as Context)){
            if(errors()){
                register_dialog.visibility=View.VISIBLE
                try{
                    val registerUser= JSONObject()
                    registerUser.put("name",etName.text)
                    registerUser.put("mobile_number",etMobileNo.text)
                    registerUser.put("password",etPassword.text)
                    registerUser.put("address",etAddress.text)
                    registerUser.put("email",etEmail.text)
                    val queue= Volley.newRequestQueue(activity as Context)
                    val url="http://13.235.250.119/v2/register/fetch_result"
                    val jsonObjectRequest=object : JsonObjectRequest(
                        Request.Method.POST,url,registerUser,
                        Response.Listener{
                        val responseJsonObjectData=it.getJSONObject("data")
                        val success=responseJsonObjectData.getBoolean("success")
                        if(success){
                            val data = responseJsonObjectData.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("logged in", true).apply()
                            sharedPreferences.edit().putString("user_id", data.getString("user_id"))
                                .apply()
                            sharedPreferences.edit().putString("name", data.getString("name"))
                                .apply()
                            sharedPreferences.edit().putString("email", data.getString("email"))
                                .apply()
                            sharedPreferences.edit()
                                .putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address", data.getString("address"))
                                .apply()
                            Toast.makeText(
                                contextParam,
                                "Register Success",
                                Toast.LENGTH_SHORT
                            ).show()
                            userSuccessfullyRegistered()
                        }else{
                            Toast.makeText(contextParam, "error message", Toast.LENGTH_SHORT).show()
                        }
                            register_dialog.visibility=View.INVISIBLE
                    },Response.ErrorListener{
                            register_dialog.visibility=View.INVISIBLE
                            Toast.makeText(contextParam,"Some error occurred!!",Toast.LENGTH_SHORT).show ()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="d721c31063faf3"
                            return headers
                    }
                }
                    queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                    Toast.makeText(contextParam,"some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
                }
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
    fun errors():Boolean{
        var count=0
        if(etName.text.isBlank()){
            etName.error = "Field Missing!"
        }else{
            count++
        }
        if(etEmail.text.isBlank()){
            etEmail.error = "Field Missing!"
        }else{
            count++
        }
        if(etMobileNo.text.isBlank()){
            etMobileNo.error = "Field Missing!"
        }else{
            count++
        }
        if(etAddress.text.isBlank()){
            etAddress.error = "Field Missing!"
        }else{
            count++
        }
        if(etPassword.text.isBlank()){
            etPassword.error = "Field Missing!"
        }else{
            count++
        }
        if(etConfirmPassword.text.isBlank()){
            etConfirmPassword.error = "Field Missing!"
        }else{
            count++
        }
        if(etPassword.text.isNotBlank() && etConfirmPassword.text.isNotBlank())
        {
            if(etPassword.text.toString().toInt()==etConfirmPassword.text.toString().toInt()){
                count++
            }else{
                etConfirmPassword.error = "Password didn't match try again!"
            }
        }
         if(count==7)
             return true
         else
             return false
    }

    override  fun onResume(){
        if(!ConnectionManager().checkConnectivity(activity as Context)){
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

