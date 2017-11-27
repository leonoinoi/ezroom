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
import kotlinx.android.synthetic.main.activity_room_type_edit.*

class RoomTypeEdit : AppCompatActivity() {

    private val list = ArrayList<String>()
    private var roomType = RoomType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_type_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent ----------------------------------------------------------------------------------
        val command = intent.getStringExtra("command")
        if (command == "edit") {
            type.isEnabled = false
            type.setTextColor(ContextCompat.getColor(this, R.color.grey500))
            rental_month.requestFocus()
            roomType = intent.getSerializableExtra("roomType") as RoomType
        }

        // Fire Base -------------------------------------------------------------------------------
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(this, Login::class.java))
            } else {
                val ref = "/${firebase.currentUser!!.uid}/BUILDING"
                FirebaseDatabase.getInstance().getReference(ref).addListenerForSingleValueEvent(Listener())
            }
        }

        // Button Save -----------------------------------------------------------------------------
        save.setOnClickListener {
            if (type.text.isNullOrEmpty()) {
                type.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (rental_month.text.isNullOrEmpty()) {
                rental_month.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (deposit.text.isNullOrEmpty()) {
                deposit.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (deposit_period_month.text.isNullOrEmpty()) {
                deposit_period_month.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            roomType.type = type.text.toString().replace(".", "")
            roomType.name_building = name_building.selectedItem.toString()
            roomType.rental_month = rental_month.text.toString().toInt()
            roomType.deposit = deposit.text.toString().toInt()
            roomType.deposit_period_month = deposit_period_month.text.toString().toInt()

            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/ROOM_TYPE"
            FirebaseDatabase.getInstance().getReference(ref).child(roomType.type).setValue(roomType)

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
                list.clear()
                list.add("")
            } else {
                list.clear()
                data.children.mapTo(list) { it.getValue(Building::class.java)!!.name }

            }
            name_building.adapter = ArrayAdapter(applicationContext, R.layout.list_dropdown, list)
            initView()
        }
    }

    private fun initView() {
        type.setText(roomType.type)
        name_building.setSelection(list.indexOf(roomType.name_building))
        rental_month.setText(roomType.rental_month.toString())
        deposit.setText(roomType.deposit.toString())
        deposit_period_month.setText(roomType.deposit_period_month.toString())
    }
}
