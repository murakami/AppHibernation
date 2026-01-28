package com.example.apphibernation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.content.UnusedAppRestrictionsConstants
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        appHibernation()
    }

    lateinit var future: ListenableFuture<Int>

    fun appHibernation() {
        Log.d("Hibernation", "appHibernation")
        future = PackageManagerCompat.getUnusedAppRestrictionsStatus(this)
        future.addListener({ onResult(future.get()) }, ContextCompat.getMainExecutor(this))
    }

    private val REQUEST_CODE: Int = 999

    //val future: ListenableFuture<Int> =
    //    PackageManagerCompat.getUnusedAppRestrictionsStatus(this)
    //future.addListener({ onResult(future.get()) }, ContextCompat.getMainExecutor(context))

    fun onResult(appRestrictionsStatus: Int) {
        when (appRestrictionsStatus) {
            // Couldn't fetch status. Check logs for details.
            UnusedAppRestrictionsConstants.ERROR -> { Log.d("Hibernation", "ERROR") }

            // Restrictions don't apply to your app on this device.
            UnusedAppRestrictionsConstants.FEATURE_NOT_AVAILABLE -> { Log.d("Hibernation", "FEATURE_NOT_AVAILABLE") }

            // The user has disabled restrictions for your app.
            UnusedAppRestrictionsConstants.DISABLED -> { Log.d("Hibernation", "DISABLED") }

            // If the user doesn't start your app for a few months, the system will
            // place restrictions on it. See the API_* constants for details.
            UnusedAppRestrictionsConstants.API_30_BACKPORT, UnusedAppRestrictionsConstants.API_30, UnusedAppRestrictionsConstants.API_31 -> handleRestrictions(appRestrictionsStatus)
        }
    }

    fun handleRestrictions(appRestrictionsStatus: Int) {
        Log.d("Hibernation", "handleRestrictions: BEGIN")
        try {
            Log.d("Hibernation", "handleRestrictions: call createManageUnusedAppRestrictionsIntent")
            // If your app works primarily in the background, you can ask the user
            // to disable these restrictions. Check if you have already asked the
            // user to disable these restrictions. If not, you can show a message to
            // the user explaining why permission auto-reset or app hibernation should be
            // disabled. Then, redirect the user to the page in system settings where they
            // can disable the feature.
            val intent = IntentCompat.createManageUnusedAppRestrictionsIntent(this, packageName)

            Log.d("Hibernation", "handleRestrictions: call startActivityForResult")
            // You must use startActivityForResult(), not startActivity(), even if
            // you don't use the result code returned in onActivityResult().
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
            Log.e("Hibernation", e.toString())
        }
        Log.d("Hibernation", "handleRestrictions: END")
    }
}