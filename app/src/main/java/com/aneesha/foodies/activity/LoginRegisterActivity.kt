package com.aneesha.foodies.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.fragment.LoginFragment

class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        val sharedPreferences=getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("logged in",false)){
            val intent= Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            openLoginFragment()
        }
    }
    fun openLoginFragment(){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,LoginFragment(this))
        transaction.commit()
        supportActionBar?.title="Dashboard"
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.FrameLayout)){
            !is LoginFragment -> openLoginFragment()
            else ->super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           android.R.id.home->{
                openLoginFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
