package com.example.runliebre

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private val db = FirebaseFirestore.getInstance()
    private val markersMap = HashMap<String, Marker>()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.mapOSM)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // --- CORRECCIÓN 1: INICIO EN QUITO (NO EN EL MAR) ---
        val mapController = map.controller
        mapController.setZoom(14.0) // Un zoom nivel ciudad
        val quitoPoint = GeoPoint(-0.1807, -78.4678) // Coordenadas de Quito
        mapController.setCenter(quitoPoint)

        Toast.makeText(context, "Buscando corredores...", Toast.LENGTH_SHORT).show()

        escucharCorredores()
    }

    private fun escucharCorredores() {
        firestoreListener = db.collection("users")
            //.whereEqualTo("rol", "runner") // Si quieres ver TODOS (incluido admins) quita esta línea
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    val puntosActivos = ArrayList<GeoPoint>() // Lista para guardar ubicaciones y hacer zoom

                    for (doc in snapshots.documents) {
                        // Solo mostramos si tiene "isActive" true (opcional) o si es runner
                        val rol = doc.getString("rol") ?: ""

                        // Coordenadas
                        val lat = doc.getDouble("latitud") ?: 0.0
                        val lng = doc.getDouble("longitud") ?: 0.0
                        val nombre = doc.getString("nombre") ?: "Usuario"
                        val uid = doc.id

                        // Filtramos para no mostrar gente en el mar (0,0)
                        if (lat != 0.0 && lng != 0.0) {
                            val nuevaPosicion = GeoPoint(lat, lng)
                            puntosActivos.add(nuevaPosicion)

                            if (markersMap.containsKey(uid)) {
                                // Mover marcador existente
                                val marcador = markersMap[uid]
                                marcador?.position = nuevaPosicion
                                marcador?.snippet = rol // Poner el rol en la descripción
                                marcador?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            } else {
                                // Crear nuevo marcador
                                val nuevoMarcador = Marker(map)
                                nuevoMarcador.position = nuevaPosicion
                                nuevoMarcador.title = nombre
                                nuevoMarcador.snippet = rol

                                // Icono personalizado
                                val icon = androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.ic_runner_pin)
                                nuevoMarcador.icon = icon
                                nuevoMarcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                map.overlays.add(nuevoMarcador)
                                markersMap[uid] = nuevoMarcador
                            }
                        }
                    }

                    map.invalidate() // Redibujar mapa

                    // --- CORRECCIÓN 2: ZOOM AUTOMÁTICO A LOS CORREDORES ---
                    if (puntosActivos.isNotEmpty()) {
                        // Si es la primera vez que cargamos o hay nuevos, ajustamos la cámara
                        // (Puedes quitar este if si quieres que SIEMPRE los siga)
                        if (puntosActivos.size == 1) {
                            map.controller.animateTo(puntosActivos[0])
                        } else {
                            // Si hay varios, hacemos zoom para que quepan todos en la pantalla
                            val box = BoundingBox.fromGeoPoints(puntosActivos)
                            // Agregamos un margen para que no queden pegados al borde
                            map.zoomToBoundingBox(box, true, 100)
                        }
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
    }
}