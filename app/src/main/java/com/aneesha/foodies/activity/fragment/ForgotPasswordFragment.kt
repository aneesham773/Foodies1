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
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
lateinit var etOTP:EditText
lateinit var etNewPassword:EditText
lateinit var txtForgotPassword:TextView
lateinit var etNewConfirmPassword:EditText
lateinit var btnSubmit:Button
lateinit var forgot_password_dialog:RelativeLayout
class ForgotPasswordFragment( val contextParam:Context,val mobile_number:String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_forgot_password, container, false)
        etOTP=view.findViewById(R.id.etOTP)
        etNewPassword=view.findViewById(R.id.etNewPassword)
        etNewConfirmPassword=view.findViewById(R.id.etNewConfirmPassword)
        btnSubmit=view.findViewById(R.id.btnSubmit)
        forgot_password_dialog=view.findViewById(R.id.forgot_password_dialog)
        btnSubmit.setOnClickListener(View.OnClickListener {
            if(etOTP.text.isBlank()){
                etOTP.error = "OTP missing!!"
            }else{
                if(etNewPassword.text.isBlank()){
                    etNewPassword.error = "Password missing!!"
                }else{
                    if(etNewConfirmPassword.text.isBlank()){
                        etNewConfirmPassword.error = "Confirm Password missing!"
                    }else{
                        if(etNewPassword.text.toString().toInt()== etNewConfirmPassword.text.toString().toInt()){
                            if(ConnectionManager().checkConnectivity(activity as Context)){
                                forgot_password_dialog.visibility=View.VISIBLE
                                try{
                                    val loginUser= JSONObject()
                                    loginUser.put("mobile_number",mobile_number)
                                    loginUser.put("otp", etOTP.text.toString())
                                    loginUser.put("password", etNewPassword.text.toString())
                                    val queue= Volley.newRequestQueue(activity as Context)
                                    val url="http://13.235.250.119/v2/reset_password/fetch_result"
                                    val jsonObjectRequest=object : JsonObjectRequest(
                                        Request.Method.POST,url,loginUser,
                                        Response.Listener{
                                            val responseJsonObjectData=it.getJSONObject("data")
                                            val success=responseJsonObjectData.getBoolean("success")
                                            if(success) {
                                                val successMessage =
                                                    responseJsonObjectData.getString("successMessage")
                                                if (successMessage=="Password has successfully changed.") {
                                                    Toast.makeText(
                                                        contextParam,
                                                        "Changed Password",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    newPassword()
                                                } else {
                                                    Toast.makeText(
                                                        contextParam,
                                                        "unsuccessful",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                            else{
                                                val responseMsgServer=responseJsonObjectData.getString("errorMessage")
                                                Toast.makeText(contextParam,responseMsgServer.toString(),Toast.LENGTH_SHORT).show()
                                            }
                                            forgot_password_dialog.visibility=View.INVISIBLE
                                        }
                                ,Response.ErrorListener {
                                    forgot_password_dialog.visibility=View.INVISIBLE
                                            Toast.makeText(contextParam,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                                }) {
                                    override fun getHeaders():   MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Content-type"]="application/json"
                                        headers["token"]="d721c31063faf3"
                                        return headers
                                    }
                                }
                                    queue.add(jsonObjectRequest)
                            }catch (e:JSONException){
                                Toast.makeText(contextParam,"Some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
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
                        }else{
                            etNewConfirmPassword.error = "Passwords didn't match"
                        }
                    }
                }
            }
        })
        return  view
    }
   fun newPassword(){
       val transaction=fragmentManager?.beginTransaction()
       transaction?.replace(R.id.frameLayout,LoginFragment(contextParam))
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
