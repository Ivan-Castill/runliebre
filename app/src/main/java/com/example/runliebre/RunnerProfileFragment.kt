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

class RunnerProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_runner_profile, container, false)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val tvName = view.findViewById<TextView>(R.id.tvFullName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvDate = view.findViewById<TextView>(R.id.tvBirthDate)
        val tvRole = view.findViewById<TextView>(R.id.tvRole)
        val btnLogout = view.findViewById<Button>(R.id.btnLogoutRunner)

        //Cargar datos
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: ""
                val apellido = doc.getString("apellido") ?: ""
                tvName.text = "$nombre $apellido"
                tvEmail.text = doc.getString("email")
                tvPhone.text = doc.getString("telefono") ?: "No registrado"
                tvDate.text = doc.getString("fechaNacimiento") ?: "No registrada"
                tvRole.text = doc.getString("rol")?.uppercase() ?: "RUNNER"
            }
        }


        btnLogout.setOnClickListener {

            (activity as? RunnerActivity)?.stopLocationService()

            auth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}