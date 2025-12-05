package com.example.practica8_contactos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ContactoAdapter(
    private val context: Context,
    private val contactos: ArrayList<Contacto>
) : BaseAdapter() {

    override fun getCount(): Int = contactos.size

    override fun getItem(position: Int): Any = contactos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contacto = contactos[position]

        holder.tvNombre.text = contacto.nombre
        holder.tvTelefono.text = contacto.telefono
        holder.tvCorreo.text = if (contacto.correo.isEmpty()) "Sin correo" else contacto.correo
        holder.tvDireccion.text = if (contacto.direccion.isEmpty()) "Sin dirección" else contacto.direccion

        if (contacto.imagen.isNotEmpty()) {
            Picasso.get()
                .load(contacto.imagen)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(holder.imgContacto)
        } else {
            holder.imgContacto.setImageResource(R.drawable.ic_person)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val imgContacto: ImageView = view.findViewById(R.id.imgContactoItem)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreItem)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefonoItem)
        val tvCorreo: TextView = view.findViewById(R.id.tvCorreoItem)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccionItem)
    }
}