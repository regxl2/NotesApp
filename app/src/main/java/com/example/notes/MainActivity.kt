package com.example.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.notes.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


interface Notification {
    fun showSnackBar(msg: String)
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Notification {
    private lateinit var navController: NavController
    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var splashScreen: SplashScreen
    private var logoutMenuItem: MenuItem? = null
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        setSupportActionBar(mainActivityBinding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.listNoteFragment || destination.id == R.id.authFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            launch {
                viewModel.showSplashScreen.flowWithLifecycle(lifecycle = lifecycle)
                    .collect { value ->
                        splashScreen.apply {
                            setKeepOnScreenCondition {
                                value
                            }
                        }
                    }
            }
            launch {
                viewModel.isLogin.flowWithLifecycle(lifecycle = lifecycle)
                    .collect { isLoggedIn ->
                        if (isLoggedIn && navController.currentDestination?.id == R.id.authFragment) {
                            navController.navigate(resId = R.id.action_authFragment_to_listNoteFragment)
                            navController.graph.setStartDestination(R.id.listNoteFragment)
                        }
                    }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun showSnackBar(msg: String) {
        Snackbar.make(mainActivityBinding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

}