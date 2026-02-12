package com.example.runliebre

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Modelo de datos
data class User(
    val id: String,
    val nombre: String,
    val apellido: String = "",
    val email: String,
    val telefono: String = "",
    val rol: String = "runner", // Ahora guardamos el rol
    val fechaNacimiento: String = "",
    val isActive: Boolean = true
)
// CORRECCIÓN AQUÍ: Usar paréntesis () para el constructor, no llaves {}
class UserAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onBlockClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivUserIcon)
        val tvName: TextView = view.findViewById(R.id.tvUserName)
        val tvEmail: TextView = view.findViewById(R.id.tvUserEmail)
        val tvRole: TextView = view.findViewById(R.id.tvUserRole)
        val tvStatus: TextView = view.findViewById(R.id.tvUserStatus)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnBlock: ImageButton = view.findViewById(R.id.btnBlock)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvName.text = "${user.nombre} ${user.apellido}"
        holder.tvEmail.text = user.email
        holder.tvRole.text = user.rol.uppercase()

        // Diferenciar visualmente Admin de Runner
        if (user.rol == "admin") {
            holder.ivIcon.setImageResource(android.R.drawable.ic_lock_idle_lock) // Icono de candado/admin
            holder.tvRole.setBackgroundColor(Color.parseColor("#FFD700")) // Dorado
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_people) // Icono normal (asegúrate de tenerlo)
            holder.tvRole.setBackgroundColor(Color.parseColor("#E0E0E0")) // Gris
        }

        if (user.isActive) {
            holder.tvStatus.text = "Activo"
            holder.tvStatus.setTextColor(Color.GREEN)
            holder.btnBlock.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            holder.tvStatus.text = "Bloqueado"
            holder.tvStatus.setTextColor(Color.RED)
            holder.btnBlock.setImageResource(android.R.drawable.ic_media_play)
        }

        holder.btnEdit.setOnClickListener { onEditClick(user) }
        holder.btnBlock.setOnClickListener { onBlockClick(user) }
        holder.btnDelete.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}