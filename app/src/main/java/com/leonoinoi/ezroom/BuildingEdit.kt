package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_building_edit.*

class BuildingEdit : AppCompatActivity() {

    private var building = Building()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent ----------------------------------------------------------------------------------
        val command = intent.getStringExtra("command")
        if (command == "edit") {
            name.isEnabled = false
            name.setTextColor(ContextCompat.getColor(this, R.color.grey500))
            phone.requestFocus()
            building = intent.getSerializableExtra("building") as Building
        }
        initView()

        // Button ----------------------------------------------------------------------------------
        save.setOnClickListener {
            if (name.text.isNullOrEmpty()) {
                name.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (power_rate.text.isNullOrEmpty()) {
                power_rate.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (power_bill_min.text.isNullOrEmpty()) {
                power_bill_min.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (water_rate.text.isNullOrEmpty()) {
                water_rate.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (water_bill_min.text.isNullOrEmpty()) {
                water_bill_min.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            building.name = name.text.toString().replace(".", "")
            building.phone = phone.text.toString()
            building.email = email.text.toString()
            building.address = address.text.toString()
            building.gps = gps.text.toString()
            building.comment = comment.text.toString()
            building.power_rate = power_rate.text.toString().toInt()
            building.power_bill_min = power_bill_min.text.toString().toInt()
            building.water_rate = water_rate.text.toString().toInt()
            building.water_bill_min = water_bill_min.text.toString().toInt()

            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/BUILDING"
            FirebaseDatabase.getInstance().getReference(ref).child(building.name).setValue(building)

            super.onBackPressed()
        }

        cancel.setOnClickListener {
            super.onBackPressed()
        }

        // Firebase --------------------------------------------------------------------------------
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(this, Login::class.java))
            }
        }
    }

    private fun initView() {
        name.setText(building.name)
        phone.setText(building.phone)
        email.setText(building.email)
        address.setText(building.address)
        gps.setText(building.gps)
        comment.setText(building.comment)
        power_rate.setText(building.power_rate.toString())
        power_bill_min.setText(building.power_bill_min.toString())
        water_rate.setText(building.water_rate.toString())
        water_bill_min.setText(building.water_bill_min.toString())
    }
}
