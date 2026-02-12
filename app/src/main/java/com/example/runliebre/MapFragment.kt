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


        val mapController = map.controller
        mapController.setZoom(14.0)
        val quitoPoint = GeoPoint(-0.1807, -78.4678)
        mapController.setCenter(quitoPoint)

        Toast.makeText(context, "Buscando corredores...", Toast.LENGTH_SHORT).show()

        escucharCorredores()
    }

    private fun escucharCorredores() {
        firestoreListener = db.collection("users")

            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    val puntosActivos = ArrayList<GeoPoint>()

                    for (doc in snapshots.documents) {

                        val rol = doc.getString("rol") ?: ""


                        val lat = doc.getDouble("latitud") ?: 0.0
                        val lng = doc.getDouble("longitud") ?: 0.0
                        val nombre = doc.getString("nombre") ?: "Usuario"
                        val uid = doc.id


                        if (lat != 0.0 && lng != 0.0) {
                            val nuevaPosicion = GeoPoint(lat, lng)
                            puntosActivos.add(nuevaPosicion)

                            if (markersMap.containsKey(uid)) {

                                val marcador = markersMap[uid]
                                marcador?.position = nuevaPosicion
                                marcador?.snippet = rol
                                marcador?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            } else {

                                val nuevoMarcador = Marker(map)
                                nuevoMarcador.position = nuevaPosicion
                                nuevoMarcador.title = nombre
                                nuevoMarcador.snippet = rol


                                val icon = androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.ic_runner_pin)
                                nuevoMarcador.icon = icon
                                nuevoMarcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                map.overlays.add(nuevoMarcador)
                                markersMap[uid] = nuevoMarcador
                            }
                        }
                    }

                    map.invalidate()


                    if (puntosActivos.isNotEmpty()) {

                        if (puntosActivos.size == 1) {
                            map.controller.animateTo(puntosActivos[0])
                        } else {

                            val box = BoundingBox.fromGeoPoints(puntosActivos)
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