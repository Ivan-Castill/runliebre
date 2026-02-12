package com.example.runliebre

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etFecha = findViewById<EditText>(R.id.etFechaNacimiento)
        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPass = findViewById<EditText>(R.id.etPasswordReg)

        val etPassConfirm = findViewById<EditText>(R.id.etPasswordConfirm)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)


        etFecha.isFocusable = false
        etFecha.isClickable = true
        etFecha.setOnClickListener { mostrarSelectorFecha(etFecha) }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString()
            val passConfirm = etPassConfirm.text.toString()

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (pass != passConfirm) {
                etPassConfirm.error = "Las contraseñas no coinciden"
                Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (!esPasswordSegura(pass)) {
                return@setOnClickListener
            }

            crearUsuario(nombre, apellido, telefono, fecha, email, pass)
        }

        tvLoginLink.setOnClickListener { finish() }
    }


    private fun mostrarSelectorFecha(editText: EditText) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val mesAjustado = selectedMonth + 1
            val fechaSeleccionada = "$selectedDay/$mesAjustado/$selectedYear"
            editText.setText(fechaSeleccionada)
        }, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun esPasswordSegura(password: String): Boolean {
        if (password.length < 6) {
            Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!password.any { it.isDigit() }) {
            Toast.makeText(this, "Falta un número", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!password.any { it.isUpperCase() }) {
            Toast.makeText(this, "Falta una mayúscula", Toast.LENGTH_SHORT).show()
            return false
        }
        val pattern = Pattern.compile("[^a-zA-Z0-9]")
        if (!pattern.matcher(password).find()) {
            Toast.makeText(this, "Falta un símbolo especial", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun crearUsuario(nombre: String, apellido: String, tel: String, fecha: String, email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val userData = hashMapOf(
                    "uid" to uid,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "telefono" to tel,
                    "fechaNacimiento" to fecha,
                    "email" to email,
                    "rol" to "runner",
                    "isActive" to true,
                    "latitud" to 0.0,
                    "longitud" to 0.0
                )
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, RunnerActivity::class.java))
                        finishAffinity()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}