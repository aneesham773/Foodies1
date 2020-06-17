package com.aneesha.foodies.activity.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.aneesha.foodies.R
import com.aneesha.foodies.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordCredentialsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
lateinit var etMobileNo:EditText
lateinit var txtForgotPasswordCredentials:TextView
lateinit var etEmail:EditText
lateinit var btnNext:Button
lateinit var imgFood_app:ImageView
lateinit var forgot_password_credentials_dialog:RelativeLayout
class ForgotPasswordCredentialsFragment(val contextParam:Context) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password_credentials, container, false)
        etMobileNo=view.findViewById(R.id.etMobileNo)
        etEmail=view.findViewById(R.id.etEmail)
        btnNext=view.findViewById(R.id.btnNext)
        forgot_password_credentials_dialog=view.findViewById(R.id.forgot_password_credentials_dialog)
        btnNext.setOnClickListener(View.OnClickListener {
            if(etMobileNo.text.isBlank()){
                etMobileNo.error = "Please enter the Mobile No"
            }else{
                if(etEmail.text.isBlank()){
                    etEmail.error = "Please enter the Email-id"
                }else{
                    if(ConnectionManager().checkConnectivity(activity as Context)){
                        try{
                            val loginUser=JSONObject()
                            loginUser.put("mobile_number", etMobileNo.text)
                            loginUser.put("email", etEmail.text)
                            val queue= Volley.newRequestQueue(activity as Context)
                            val url="http://13.235.250.119/v2/forgot_password/fetch_result"
                            forgot_password_credentials_dialog.visibility=View.VISIBLE
                            val jsonObjectRequest=object : JsonObjectRequest(
                                Request.Method.POST,url,loginUser,
                                Response.Listener{
                                    val responseJsonObjectData=it.getJSONObject("data")
                                    val success=responseJsonObjectData.getBoolean("success")
                                    if(success){
                                        val first_try=responseJsonObjectData.getBoolean("first_try")
                                        if(first_try)
                                        { Toast.makeText(contextParam,"OTP has been sent",Toast.LENGTH_SHORT).show()
                                            changePassword()
                                        }else{
                                            Toast.makeText(contextParam,"OTP has already been sent",Toast.LENGTH_SHORT).show()
                                            changePassword()
                                        }}
                                    else{
                                        val responseMsgServer=responseJsonObjectData.getString("errorMessage")
                                        Toast.makeText(contextParam,responseMsgServer.toString(),Toast.LENGTH_SHORT).show()
                                    }
                                },Response.ErrorListener {
                                    Toast.makeText(contextParam,"Some error has occurred",Toast.LENGTH_SHORT).show()
                        }){
                                override fun getHeaders():   MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"]="application/json"
                                    headers["token"]="d721c31063faf3"
                                    return headers
                                } }
                            queue.add(jsonObjectRequest)
                }catch (e:JSONException){
                            Toast.makeText(contextParam,"Some unexpected error has occurred",Toast.LENGTH_SHORT).show()
                        }
                        } else{
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
            }
        })
        return view
    }
      fun changePassword(){
          val transaction=fragmentManager?.beginTransaction()
          transaction?.replace(R.id.frameLayout,ForgotPasswordFragment(contextParam,etMobileNo.text.toString()))
          transaction?.commit()
      }
    override fun onResume(){
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
