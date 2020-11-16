package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import ar.uba.fi.remy.model.ContactAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_contacts.*
import org.json.JSONObject
import androidx.core.os.HandlerCompat.postDelayed
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import ar.uba.fi.remy.model.ContactRequestAdapter
import com.android.volley.toolbox.JsonObjectRequest


class ContactsActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()
    var pendingInvites = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: ContactAdapter
    lateinit var adapterInvites: ContactRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        //Configuro adapter
        adapter = ContactAdapter(this, dataList)
        contact_list.adapter = adapter

        //Configuro adapter invites
        adapterInvites = ContactRequestAdapter(this, pendingInvites)
        contact_pending_invites.adapter = adapterInvites

        //Configuro filtro de contactos
        setFilter()

        cargarContactos()

        cargarInvites()

        configHideInvites()
    }

    private fun configHideInvites() {
        contact_title_invites.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action === MotionEvent.ACTION_DOWN) {
                if(contact_pending_invites.visibility === View.VISIBLE) {
                    contact_pending_invites.visibility = View.GONE
                } else {
                    contact_pending_invites.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    private fun cargarInvites() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/friendship/"

        pendingInvites.clear()
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val solicitud = results.getJSONObject(i)
                    if(solicitud.getString("status") == "REQUESTED") {
                        Log.i("API", "ENTRAAA")
                        agregarSolicitud(solicitud)
                    }

                }
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun agregarSolicitud(solicitud: JSONObject) {
        val map = HashMap<String, String>()

        /*map["name"] = contacto.getString("first_name") + " " + contacto.getString("last_name")
        map["username"] = contacto.getString("username")
        map["email"] = contacto.getString("email")*/

        map["idRequest"] = solicitud.getInt("id").toString()
        map["name"] = "Nombre hardcodeado"
        map["username"] = "Username"
        map["email"] = "Mail@mail.com"


        adapterInvites.addData(map)
    }

    private fun setFilter() {
        contact_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.getFilter().filter(newText)
                }
                return true
            }

        })
    }

    private fun cargarContactos() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/friends/"

        dataList.clear()
        val jsonArrayRequest = object: JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                /*contactos = response.toString()*/
                for (i in 0 until response.length()) {
                    val contacto = response.getJSONObject(i)
                    agregarContacto(contacto)
                }
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonArrayRequest)
    }

    private fun agregarContacto(contacto: JSONObject?) {
        val map = HashMap<String, String>()
        if (contacto != null) {
            map["name"] = contacto.getString("first_name") + " " + contacto.getString("last_name")
            map["username"] = contacto.getString("username")
            map["email"] = contacto.getString("email")
        }

        adapter.addData(map)

    }
}
