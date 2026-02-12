package com.example.runliebre

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsersFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        // Agregar botón flotante para "Añadir Admin/Usuario" en el XML de fragment_users si quieres
        // Opcional: Podrías añadir un FAB en el XML y configurarlo aquí.

        val recycler = view.findViewById<RecyclerView>(R.id.rvUsers)
        recycler.layoutManager = LinearLayoutManager(context)

        adapter = UserAdapter(emptyList(),
            onEditClick = { user -> showEditUserDialog(user) },
            onBlockClick = { user -> confirmAction("¿Cambiar estado?",
                "¿Seguro que quieres ${if(user.isActive) "bloquear" else "activar"} a ${user.nombre}?") { toggleUserStatus(user) }
            },
            onDeleteClick = { user -> confirmAction("¡CUIDADO! Eliminar usuario",
                "Esta acción no se puede deshacer. ¿Eliminar a ${user.nombre} permanentemente?") { deleteUser(user) }
            }
        )
        recycler.adapter = adapter

        loadUsers()
        return view
    }

    private fun loadUsers() {
        // QUITAMOS EL FILTRO .whereEqualTo("rol", "runner") para ver a TODOS (incluido Admins)
        db.collection("users")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener

                val userList = snapshots.documents.map { doc ->
                    User(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        email = doc.getString("email") ?: "",
                        telefono = doc.getString("telefono") ?: "",
                        fechaNacimiento = doc.getString("fechaNacimiento") ?: "",
                        rol = doc.getString("rol") ?: "runner",
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                }
                adapter.updateData(userList)
            }
    }

    // --- DIÁLOGOS DE SEGURIDAD ---
    private fun confirmAction(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Sí, continuar") { _, _ -> onConfirm() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toggleUserStatus(user: User) {
        db.collection("users").document(user.id).update("isActive", !user.isActive)
        Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
    }

    private fun deleteUser(user: User) {
        // Nota: Esto borra de Firestore. Borrar de Auth requiere Cloud Functions o re-autenticación.
        // Para la tarea, borrar de Firestore suele ser suficiente para que "deje de existir" en la app.
        db.collection("users").document(user.id).delete()
            .addOnSuccessListener { Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show() }
    }

    // --- DIÁLOGO DE EDICIÓN / AGREGAR ---
    private fun showEditUserDialog(user: User) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)

        // Referencias del layout del diálogo (Tendremos que crearlo ahora)
        val etNombre = dialogView.findViewById<EditText>(R.id.etEditNombre)
        val etApellido = dialogView.findViewById<EditText>(R.id.etEditApellido)
        val etTelefono = dialogView.findViewById<EditText>(R.id.etEditTelefono)
        val spinnerRol = dialogView.findViewById<Spinner>(R.id.spinnerRol)

        // Llenar datos
        etNombre.setText(user.nombre)
        etApellido.setText(user.apellido)
        etTelefono.setText(user.telefono)

        // Configurar Spinner de Roles
        val roles = arrayOf("runner", "admin")
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        spinnerRol.adapter = adapterSpinner
        if (user.rol == "admin") spinnerRol.setSelection(1) else spinnerRol.setSelection(0)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoRol = spinnerRol.selectedItem.toString()

                val updates = hashMapOf<String, Any>(
                    "nombre" to etNombre.text.toString(),
                    "apellido" to etApellido.text.toString(),
                    "telefono" to etTelefono.text.toString(),
                    "rol" to nuevoRol
                )

                db.collection("users").document(user.id).update(updates)
                    .addOnSuccessListener { Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}