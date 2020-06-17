package com.aneesha.foodies.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.aneesha.foodies.R

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var btnOk:Button
    lateinit var rlOrderPlaced:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        rlOrderPlaced=findViewById(R.id.rlOrderPlaced)
        btnOk=findViewById(R.id.btnOk)
        btnOk.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        })
    }

    override fun onBackPressed() {

    }
}
