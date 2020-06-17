package com.aneesha.foodies.activity.fragment

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment(val contextParam:Context): Fragment() {
    lateinit var txtSignUp:TextView
    lateinit var etMobileNo:EditText
    lateinit var etPassword:EditText
    lateinit var txtForgotPassword:TextView
    lateinit var btnLogin:Button
    lateinit var login_dialog:RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_login, container, false)
        etMobileNo=view.findViewById(R.id.etMobileNo)
        etPassword=view.findViewById(R.id.etPassword)
        txtForgotPassword=view.findViewById(R.id.txtForgotPassword)
        txtSignUp=view.findViewById(R.id.txtSignUp)
        btnLogin=view.findViewById(R.id.btnLogin)
        login_dialog=view.findViewById(R.id.login_dialog)
        login_dialog.visibility=View.INVISIBLE
        txtForgotPassword.paintFlags=Paint.UNDERLINE_TEXT_FLAG
        txtSignUp.paintFlags=Paint.UNDERLINE_TEXT_FLAG
        txtForgotPassword.setOnClickListener(View.OnClickListener {
            openForgotPaswordInputFragment()
        })
        txtSignUp.setOnClickListener(View.OnClickListener {
            openRegisterFragment()
        })
        btnLogin.setOnClickListener(View.OnClickListener {
            btnLogin.visibility=View.INVISIBLE
            if(etMobileNo.text.isBlank()){
                etMobileNo.error = "MOBILE NO IS NOT ENTERED"
                btnLogin.visibility=View.VISIBLE
            }else{
                if(etPassword.text.isBlank()){
                    btnLogin.visibility=View.VISIBLE
                    etPassword.error = "MISSING PASSWORD"
                }else{
                    loginUser()
                }
            }
        })
        return view
    }
    fun openForgotPaswordInputFragment() {
        val transaction=fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout,ForgotPasswordCredentialsFragment(contextParam))
        transaction?.commit()

    }
    fun openRegisterFragment(){
        val transaction=fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout,RegisterFragment(contextParam))
        transaction?.commit()
    }
    fun loginUser(){
        val sharedPreferences=contextParam.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
        if(ConnectionManager().checkConnectivity(activity as Context)){
            try{
                val loginUser=JSONObject()
                loginUser.put("mobile_number",etMobileNo.text)
                loginUser.put("password",etPassword.text)
                val queue= Volley.newRequestQueue(activity as Context)
                val url="http://13.235.250.119/v2/login/fetch_result"
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,url,loginUser,
                    Response.Listener {
                        val responseJsonObjData = it.getJSONObject("data")
                        val success = responseJsonObjData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjData.getJSONObject("data")
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
                                "Welcome " + data.getString("name"),
                                Toast.LENGTH_SHORT
                            ).show()
                            userSuccessfullyLogged()
                        } else {
                            btnLogin.visibility = View.VISIBLE
                            Toast.makeText(contextParam, "error message", Toast.LENGTH_SHORT).show()
                        }
                        login_dialog.visibility = View.INVISIBLE
                    },Response.ErrorListener {
                        btnLogin.visibility=View.VISIBLE
                        login_dialog.visibility=View.INVISIBLE
                        Toast.makeText(contextParam,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="d721c31063faf3"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                btnLogin.visibility=View.VISIBLE
                Toast.makeText(contextParam,"some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            btnLogin.visibility=View.VISIBLE
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
    fun userSuccessfullyLogged(){
        val intent=Intent(activity as Context,DashboardActivity::class.java)
        startActivity(intent)
       getActivity()?.finish()
    }

    override fun onResume() {
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

