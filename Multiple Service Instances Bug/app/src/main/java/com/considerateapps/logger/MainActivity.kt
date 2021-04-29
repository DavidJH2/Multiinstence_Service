package com.considerateapps.logger

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import com.considerateapps.logger.ui.main.*


class MainActivity : AppCompatActivity()
{
	public  val TAG = "Logger"
	private val locationPermissionCode = 2

	override fun onCreate(savedInstanceState: Bundle?)
	{
		Log.i(TAG, "MainActivity.onCreate(Buddle)")
		checkLocationPermission()
		startForegroundService()

		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		setSupportActionBar(findViewById(R.id.toolbar))

		setupObservers()
	}

	private fun setupObservers()
	{
		LocationService.location.observe(this as LifecycleOwner)
		{
			Log.i(TAG, "MainActivity:   ======= Location Changed  ======= ")
			val mediaPlayerButtonSound: MediaPlayer = MediaPlayer.create(this, R.raw.ding)
			mediaPlayerButtonSound.start()

		}
	}

	private fun checkLocationPermission()
	{
		if ((ContextCompat.checkSelfPermission(
						application,
						Manifest.permission.ACCESS_FINE_LOCATION
				) != PackageManager.PERMISSION_GRANTED)
		) {
			ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
					locationPermissionCode
			)
		}
	}

	private fun startForegroundService()
	{
		LocationService.startService(this, "Foreground Service is running...")
	}
	override fun onStart()
	{
		Log.i(TAG, "MainActivity.onStart")
		super.onStart()
	}

	override fun onResume()
	{
		Log.i(TAG, "MainActivity.onResume")
		super.onResume()
	}

	override fun onPause()
	{
		Log.i(TAG, "MainActivity.onPause")
		super.onPause()
	}

	override fun onStop()
	{
		Log.i(TAG, "MainActivity.onStop")
		super.onStop()
	}

	override fun onSaveInstanceState(outState: Bundle)
	{
		Log.i(TAG, "MainActivity.onSaveInstanceState")
		super.onSaveInstanceState(outState)
	}

	override fun onDestroy()
	{
		LocationService.stopService(this)
		super.onDestroy()
	}
}