package com.example.runliebre

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationService : Service() {
    //Variables para la ubicación y Firebase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //Actualizar cada 10 segundos para no saturar
    private val UPDATE_INTERVAL: Long = 10000
    private val FASTEST_INTERVAL: Long = 5000

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    saveLocationToFirebase(location)
                }
            }
        }
    }

    // Este método se ejecuta cuando llamamos a "startService" desde la Activity
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Crear la notificación obligatoria
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_LOCATION")
            .setContentTitle("RunLiebre Activo")
            .setContentText("Rastreando tu ubicación en tiempo real...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Icono genérico
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        //Poner el servicio en "Primer Plano"
        startForeground(1, notification)

        //Empezar a pedir coordenadas
        requestLocationUpdates()

        return START_STICKY // Si el sistema mata el servicio, intenta revivirlo
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_INTERVAL)
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {

        }
    }

    private fun saveLocationToFirebase(location: Location) {
        val uid = auth.currentUser?.uid ?: return

        val updateData = hashMapOf<String, Any>(
            "latitud" to location.latitude,
            "longitud" to location.longitude,
            "lastUpdate" to System.currentTimeMillis() // Para saber si es reciente
        )


        db.collection("users").document(uid)
            .update(updateData)
            .addOnFailureListener { e ->
            }
    }

    private fun createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "CHANNEL_LOCATION",
                "Canal de Rastreo RunLiebre",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Detener rastreo al cerrar sesión o parar el servicio
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}