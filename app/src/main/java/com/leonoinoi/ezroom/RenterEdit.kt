package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_renter_edit.*

class RenterEdit : AppCompatActivity() {

    private var renter = Renter()
    private val list_status = arrayOf("Renting", "Booking", "Old Renter")
    private val list_room = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renter_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toast_message_loading(this)

        status.adapter = ArrayAdapter(this, R.layout.list_dropdown, list_status)

        // Intent ----------------------------------------------------------------------------------
        val command = intent.getStringExtra("command")
        if (command == "edit") {
            name.isEnabled = false
            name.setTextColor(ContextCompat.getColor(this, R.color.grey500))
            phone.requestFocus()
            renter = intent.getSerializableExtra("renter") as Renter
        }

        // Button ----------------------------------------------------------------------------------
        save.setOnClickListener {
            if (name.text.isNullOrEmpty()) {
                name.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            renter.name = name.text.toString().replace(".", "")
            renter.phone = phone.text.toString()
            renter.email = email.text.toString()
            renter.id = id.text.toString()
            renter.comment = comment.text.toString()

            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/RENTER"
            FirebaseDatabase.getInstance().getReference(ref).child(renter.name).setValue(renter)

            super.onBackPressed()
        }

        cancel.setOnClickListener {
            super.onBackPressed()
        }

        // Firebase --------------------------------------------------------------------------------
        if (FirebaseAuth.getInstance().currentUser == null) startActivity(Intent(this, Login::class.java))
        val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/ROOM"
        FirebaseDatabase
                .getInstance().getReference(ref)
                .orderByChild("id")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(data: DataSnapshot?) {
                        if (data == null) return

                        list_room.clear()
                        data.children.mapTo(list_room) { it.getValue(Room::class.java)!!.id }
                        id_room.adapter = ArrayAdapter(applicationContext, R.layout.list_dropdown, list_room)

                        initView()
                    }
                })
    }

    private fun initView() {
        name.setText(renter.name)
        id.setText(renter.id)
        phone.setText(renter.phone)
        email.setText(renter.email)
        comment.setText(renter.comment)

        status.setSelection(list_status.indexOf(renter.status))
        id_room.setSelection(list_room.indexOf(renter.id_room))
    }
}
