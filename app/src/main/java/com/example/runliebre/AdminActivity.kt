package com.example.runliebre

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // 1. Cargar el Mapa por defecto al abrir la app
        if (savedInstanceState == null) {
            loadFragment(MapFragment())
        }

        // 2. Configurar los clics del menú
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> loadFragment(MapFragment())
                R.id.nav_users -> loadFragment(UsersFragment()) // Asegúrate de haber creado esta clase (del paso anterior)
                R.id.nav_profile -> loadFragment(ProfileFragment()) // Asegúrate de haber creado esta clase
            }
            true
        }
    }

    // Función mágica para cambiar pantallas
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // 'fragmentContainer' es el ID del FrameLayout en tu XML
            .commit()
    }
}