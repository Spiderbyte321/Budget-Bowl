package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.jacob.budgetbowl.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    //hmmmmmm yes the floor here is made of floor
    // ok now I just need to learn the content drawer stuff and link it to other screens we have

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDashboard.toolbar)

        //We do actually need the FAB so I'll set that up to add an expese
        //uuuuuuuggghhh
        //figure out how to add to content drawer then take a breather
        //ok so the snackbar is a fancy toast so I can just cut all that and have the FAB open up the activity
        // fab needs to be on fragment not globally

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        //ok so I think we add to the content drawer witht eh appbarconfiguration
        //then the rest will be handled by android
        //almost there I think I just need to add it to the XML
        //ok here's the order of operations
        //Step 1 create the fragment XML in the layout folder/create the activity
        //Step 2 add that fragment/activity to the mobile_navigation XML file in navigation
        //either through code or the GUI
        //Step 3 Set up the Item on the activity_main_drawer XML in the menu folder
        //Step 4 add that fragment to the above appBarConfiguration
        //N.B don't know if this works with activities or only fragments will test later
        //Ok all our stuff will need to be activities so if activities can't be added I will cry
        //I don't have to cry! yay
        //wait this method is considering these from a menu perspective but I wonder if it will open an activity
        //I might have to rework this a little to have it open our activities
        //Well that's what testing is for
        //ok no well yes but no
        //We can use the nav drawer with activities but it's not a good idea
        //better to use fragments rather than a new activity
        //thankfully fragments aren't that complicated


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}