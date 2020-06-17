package com.aneesha.foodies.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.aneesha.foodies.R

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment(val contextParam:Context) : Fragment() {
   lateinit var txtName:TextView
    lateinit var txtMobileNo:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView
    lateinit var imgUser:ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        txtName=view.findViewById(R.id.txtName)
        txtMobileNo=view.findViewById(R.id.txtMobileNo)
        txtAddress=view.findViewById(R.id.txtAddress)
        txtEmail=view.findViewById(R.id.txtEmail)
        val sharedPreferences=contextParam.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
        txtName.text=sharedPreferences.getString("name","")
        txtMobileNo.text=sharedPreferences.getString("mobile_number","")
        txtEmail.text=sharedPreferences.getString("email","")
        txtAddress.text=sharedPreferences.getString("address","")
        return view
    }


}
