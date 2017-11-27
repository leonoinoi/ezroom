package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_owner_dash_board.*

class OwnerDashBoard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dash_board)
        setSupportActionBar(toolbar)

        building.setOnClickListener {
            startActivity(Intent(this, BuildingDashBoard::class.java))
        }

        room_type.setOnClickListener {
            startActivity(Intent(this, RoomTypeDashBoard::class.java))
        }

        room.setOnClickListener {
            startActivity(Intent(this, RoomDashBoard::class.java))
        }

        renter.setOnClickListener {
            startActivity(Intent(this, RenterDashBoard::class.java))
        }

        option.setOnClickListener {
            startActivity(Intent(this, OptionDashBoard::class.java))
        }
    }

    override fun onBackPressed() {}
}
