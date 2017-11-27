package com.leonoinoi.ezroom

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_dash_board.*

class RoomDashBoard : AppCompatActivity() {

    var list_building = ArrayList<String>()
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_dash_board)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setIcon(R.drawable.ic_domain_white)
        sp = getSharedPreferences("config", Context.MODE_PRIVATE)

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val bundle = Bundle()
                bundle.putString("name_building", list_building[position])
                val room = RoomFragment()
                room.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, room)
                        .commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //Toast Loading Message
        toast_message_loading(this)

        //Firebase
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(this, Login::class.java))
            } else {
                val ref = "/${firebase.currentUser!!.uid}/BUILDING"
                FirebaseDatabase.getInstance().getReference(ref).addListenerForSingleValueEvent(ListenerBuilding())
            }
        }
    }

    inner class ListenerBuilding : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(data: DataSnapshot?) {
            if (data == null) list_building.add("")
            else data.children.mapTo(list_building) { it.getValue(Building::class.java)!!.name }
            spinner.adapter = ArrayAdapter(toolbar.context, R.layout.list_dropdown, list_building)

            val building_selected = sp.getString("name_building", "")
            if (building_selected != "") spinner.setSelection(list_building.indexOf(building_selected))
        }
    }

    override fun onDestroy() {
        sp.edit().putString("name_building", spinner.selectedItem.toString()).apply()
        super.onDestroy()
    }
}
