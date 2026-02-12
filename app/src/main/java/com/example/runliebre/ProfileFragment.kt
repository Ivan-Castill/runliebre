package com.example.runliebre

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflar el diseño del perfil
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val tvName = view.findViewById<TextView>(R.id.tvAdminName)
        val tvEmail = view.findViewById<TextView>(R.id.tvAdminEmail)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Cargar datos del Admin actual
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener {
                tvName.text = it.getString("nombre") ?: "Admin"
                tvEmail.text = it.getString("email") ?: auth.currentUser?.email
            }
        }

        // Botón de Cerrar Sesión
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            // Esto borra el historial para que no puedan volver atrás con el botón "Atrás"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}