package com.example.primarydetailkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.primarydetailkotlin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity for the application.
 *
 * This activity hosts the [NavHostFragment] which manages the navigation graph
 * and swaps fragments in and out. It also sets up the Action Bar to work
 * with the Navigation Controller.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewBinding for the activity's layout, used to access views safely.
    private lateinit var mBinding: ActivityMainBinding

    // The Navigation Controller responsible for managing app navigation.
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)

        // Find the NavHostFragment within the layout. We cast it to NavHostFragment
        // to retrieve the NavController.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        mNavController = navHostFragment.navController

        // Set the toolbar from the layout as the Activity's Action Bar.
        setSupportActionBar(mBinding.toolbar)

        // Configure the Action Bar to work with the NavController.
        // This ensures the title updates based on the destination and the Up button is displayed when appropriate.
        val appBarConfiguration = AppBarConfiguration(mNavController.graph)
        setupActionBarWithNavController(mNavController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Delegate the Up button handling to the NavController.
        // If the NavController doesn't handle it, fall back to the super implementation.
        return mNavController.navigateUp()
                || super.onSupportNavigateUp()
    }
}
