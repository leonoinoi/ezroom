package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_option_edit.*

class OptionEdit : AppCompatActivity() {

    val list_type = arrayOf("Monthly", "One Time", "Deposit")
    var option = Option()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Fire Base -------------------------------------------------------------------------------
        if (FirebaseAuth.getInstance().currentUser == null) startActivity(Intent(this, Login::class.java))

        // Spinner ---------------------------------------------------------------------------------
        type.adapter = ArrayAdapter(applicationContext, R.layout.list_dropdown, list_type)

        // Intent ----------------------------------------------------------------------------------
        val command = intent.getStringExtra("command")
        when (command) {
            "create" -> {
            }
            "edit" -> {
                name.isEnabled = false
                name.setTextColor(ContextCompat.getColor(this, R.color.grey500))

                price.requestFocus()
                option = intent.getSerializableExtra("option") as Option
            }
        }

        // Button Save -----------------------------------------------------------------------------
        save.setOnClickListener {
            if (name.text.isNullOrEmpty()) {
                name.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            if (price.text.isNullOrEmpty()) {
                price.error = getString(R.string.error_empty_input)
                return@setOnClickListener
            }

            option.name = name.text.toString()
            option.price = price.text.toString().toInt()
            option.type = type.selectedItem.toString()

            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/OPTION"
            FirebaseDatabase.getInstance().getReference(ref).child(option.name).setValue(option)

            super.onBackPressed()
        }

        // Button Cancel ---------------------------------------------------------------------------
        cancel.setOnClickListener { super.onBackPressed() }

        // Init View -------------------------------------------------------------------------------
        initView()
    }

    private fun initView() {
        name.setText(option.name)
        price.setText(option.price.toString())
        type.setSelection(list_type.indexOf(option.type))
    }
}
