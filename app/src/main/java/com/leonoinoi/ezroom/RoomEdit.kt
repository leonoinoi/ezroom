package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_edit.*


class RoomEdit : AppCompatActivity() {

    private var room = Room()
    private val list_type = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent ----------------------------------------------------------------------------------
        val command = intent.getStringExtra("command")
        when (command) {
            "create" -> {
                id.requestFocus()
                room.name_building = intent.getStringExtra("name_building")
            }
            "edit" -> {
                id.isEnabled = false
                id.setTextColor(ContextCompat.getColor(this, R.color.grey500))

                dot.visibility = View.GONE
                to_room_id.visibility = View.GONE

                comment.requestFocus()
                room = intent.getSerializableExtra("room") as Room
            }
        }

        // Fire Base -------------------------------------------------------------------------------
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(this, Login::class.java))
            } else {
                val ref = "/${firebase.currentUser!!.uid}/ROOM_TYPE"
                FirebaseDatabase
                        .getInstance().getReference(ref)
                        .orderByChild("name_building").equalTo(room.name_building)
                        .addListenerForSingleValueEvent(Listener())
            }
        }

        // Button Save -----------------------------------------------------------------------------
        save.setOnClickListener {
            if (id.text.isNullOrEmpty()) {
                id.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (name_building.text.isNullOrEmpty()) {
                name_building.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (id_end.text.isNullOrEmpty() || id_end.text.toString().toInt() < id.text.toString().toInt()) {
                room.id = id.text.toString().replace(".", "")
                room.type = type.selectedItem.toString()
                room.name_building = name_building.text.toString()
                room.comment = comment.text.toString()

                val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/ROOM"
                FirebaseDatabase.getInstance().getReference(ref).child(room.id).setValue(room)
            } else {
                for (i in id.text.toString().toInt()..id_end.text.toString().toInt()) {
                    room.id = i.toString().replace(".", "")
                    room.type = type.selectedItem.toString()
                    room.name_building = name_building.text.toString()
                    room.comment = comment.text.toString()

                    val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/ROOM"
                    FirebaseDatabase.getInstance().getReference(ref).child(room.id).setValue(room)
                }
            }
            super.onBackPressed()
        }

        // Button Cancel ---------------------------------------------------------------------------
        cancel.setOnClickListener {
            super.onBackPressed()
        }
    }

    inner class Listener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(data: DataSnapshot?) {
            if (data == null) {
                list_type.clear()
                list_type.add("")
            } else {
                list_type.clear()
                data.children.mapTo(list_type) { it.getValue(RoomType::class.java)!!.type }
            }
            type.adapter = ArrayAdapter(applicationContext, R.layout.list_dropdown, list_type)

            initView()
        }
    }

    private fun initView() {
        id.setText(room.id)
        name_building.setText(room.name_building)
        type.setSelection(list_type.indexOf(room.type))
        comment.setText(room.comment)
    }
}
