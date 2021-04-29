package com.considerateapps.logger.ui.main

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.considerateapps.logger.MainActivity
import com.considerateapps.logger.R


class LocationService : Service(),
		LocationListener
{
	val TAG = "Logger"
	val NOTIF_ID = 1

	private lateinit var locationManager: LocationManager
	private var locationUpdateCount = 0

	private val CHANNEL_ID = "ForegroundService Kotlin"
	companion object
	{
		val location:MutableLiveData<Location> = MutableLiveData<Location>()

		fun startService(context: Context, message: String)
		{
			val startIntent = Intent(context, LocationService::class.java)
			startIntent.putExtra("inputExtra", message)
			ContextCompat.startForegroundService(context, startIntent)
		}

		fun stopService(context: Context)
		{

			val stopIntent = Intent(context, LocationService::class.java)
			context.stopService(stopIntent)
		}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
	{
		Log.i(TAG, ">>>>>>>>>>>>>>>>>>>  LocationService: onStartCommand")
		locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

		//do heavy work on a background thread
		val input = intent?.getStringExtra("inputExtra")
		createNotificationChannel()
		val notification = getNotification(input!!)

		startForeground(NOTIF_ID, notification)
		requestLocationUpdates()
		super.onStartCommand(intent, flags, startId)
		return START_STICKY
	}

	private fun getNotification(contentText: String):Notification
	{
		val notificationIntent = Intent(this, MainActivity::class.java)
		notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
		val pendingIntent = PendingIntent.getActivity(
				this,
				0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT
		)

		val notification = NotificationCompat.Builder(this, CHANNEL_ID)
				.setOngoing(false)
				.setAutoCancel(false)
				.setContentTitle("Location Service")
				.setContentText(contentText)
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.setContentIntent(pendingIntent)
				.setOnlyAlertOnce(true)
				.build()
		return notification
	}

	private fun updateNotification(contentText: String)
	{
		val notification: Notification = getNotification(contentText)
		val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		mNotificationManager.notify(NOTIF_ID, notification)
	}

	override fun onBind(intent: Intent): IBinder?
	{
		return null
	}

	private fun createNotificationChannel() {
		val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
				NotificationManager.IMPORTANCE_DEFAULT)
		val manager = getSystemService(NotificationManager::class.java)
		manager!!.createNotificationChannel(serviceChannel)
	}

	private fun requestLocationUpdates()
	{
		if((ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION)) ==PackageManager.PERMISSION_GRANTED)
		{
			location.value = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0f, this)
		}
	}

	override fun onLocationChanged(i_location: Location) {
		locationUpdateCount++
		Log.i(TAG, "LocationService:  ******* onLocation Change *******   -  Count: $locationUpdateCount     Service Hash Code: ${hashCode()} <<<<<<<<<<<<<<")
		// Toast.makeText(this, "Count:$locationUpdateCount", Toast.LENGTH_SHORT).show();

		updateNotification("Count: $locationUpdateCount")
		location.value = i_location
	}

	override fun onDestroy()
	{
		Log.i(TAG, "LocationService: onDestroy  ========================================================")
		super.onDestroy()
	}
}