package com.aneesha.foodies.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aneesha.foodies.R
import com.aneesha.foodies.activity.fragment.DashboardFragment
import com.aneesha.foodies.activity.fragment.FaqsFragment
import com.aneesha.foodies.activity.fragment.FavouriteRestaurantFragment
import com.aneesha.foodies.activity.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var txtUser:TextView
    lateinit var txtMobileNo:TextView
    lateinit var sharedPreferences: SharedPreferences
    var previousItemSelected: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        sharedPreferences=getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        drawerLayout=findViewById(R.id.drawerLayout)
        val headerView=navigationView.getHeaderView(0)
        txtUser=headerView.findViewById(R.id.txtUser)
        txtMobileNo=headerView.findViewById(R.id.txtMobileNo)
        navigationView.menu.getItem(0).setCheckable(true)
        navigationView.menu.getItem(0).setChecked(true)
        setToolBar()
        txtUser.text=sharedPreferences.getString("name","John Doe")
        txtMobileNo.text=sharedPreferences.getString("mobile_number","+91-1115555555")
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@DashboardActivity,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if(previousItemSelected!=null){
                previousItemSelected?.setChecked(false)
            }
            previousItemSelected=it
            it.setCheckable(true)
            it.setChecked(true)
            when(it.itemId){
                R.id.home->{
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,
                        ProfileFragment(this)
                    ).commit()
                supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                    Toast.makeText(this@DashboardActivity,"My Profile",Toast.LENGTH_SHORT).show()
                }
                R.id.favRestaurants->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,
                        FavouriteRestaurantFragment(this)
                    ).commit()

                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                    Toast.makeText(this@DashboardActivity,"Favourite Restaurants",Toast.LENGTH_SHORT).show()
                }
                R.id.orderHistory->{
                    val intent= Intent(this@DashboardActivity,OrderHistoryActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                }
                R.id.faqs->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,
                        FaqsFragment(this)
                    ).commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                    Toast.makeText(this@DashboardActivity,"FAQ'S",Toast.LENGTH_SHORT).show()
                }
                R.id.logout->{
                    drawerLayout.closeDrawers()
                    val alertDialog=AlertDialog.Builder(this)
                    alertDialog.setTitle("Confirmation")
                    alertDialog.setMessage("Are you sure you want to logout?")
                    alertDialog.setPositiveButton("Yes"){
                        text,listener->
                        sharedPreferences.edit().putBoolean("logged in",false).apply()
                        ActivityCompat.finishAffinity(this)
                    }
                    alertDialog.setNegativeButton("No"){
                        text,listener->
                    }
                    alertDialog.create()
                    alertDialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
        openDashboard()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val currentFragment=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(currentFragment){
            !is DashboardFragment -> {
                navigationView.menu.getItem(0).isChecked = true
                openDashboard()
            }
            else-> super.onBackPressed()
    }}
    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    fun openDashboard(){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, DashboardFragment(this))
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onResume() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        super.onResume()
    }
}
