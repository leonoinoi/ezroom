package com.leonoinoi.ezroom

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_renter_dash_board.*

class RenterDashBoard : AppCompatActivity() {

    private val list = ArrayList<Renter>()
    private val list_status = arrayOf( "Renting", "Booking", "Old Renter")
    private lateinit var myAdapter: MyAdapter
    private var selected = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renter_dash_board)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setIcon(R.drawable.ic_account_box_white)
        toast_message_loading(this)

        // Spinner ---------------------------------------------------------------------------------
        spinner.adapter = ArrayAdapter(toolbar.context, R.layout.list_dropdown, list_status)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (FirebaseAuth.getInstance().currentUser == null) startActivity(Intent(applicationContext, Login::class.java))
                else {
                    val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/RENTER"
                    FirebaseDatabase.getInstance().getReference(ref)
                            .orderByChild("status").equalTo(list_status[position])
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {}
                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0 == null) return

                                    list.clear()
                                    p0.children.mapTo(list) { it.getValue(Renter::class.java)!! }
                                    myAdapter.notifyDataSetChanged()
                                }
                            })
                }
            }
        }

        // Recycle View ----------------------------------------------------------------------------
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        myAdapter = MyAdapter()
        recycler_view.adapter = myAdapter

        // Button ----------------------------------------------------------------------------------
        edit.setOnClickListener {
            if (selected == -1) {
                startActivity(Intent(this, RenterEdit::class.java)
                        .putExtra("command", "create"))
            } else {
                startActivity(Intent(this, RenterEdit::class.java)
                        .putExtra("command", "edit")
                        .putExtra("renter", list[selected]))
            }
        }

        delete.setOnClickListener {
            if (selected == -1) {
                super.onBackPressed()
            } else {
                AlertDialog.Builder(this)
                        .setMessage(getString(R.string.message_delete))
                        .setPositiveButton(getString(R.string.button_delete), { _, _ ->
                            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/RENTER/${list[selected].name}"
                            FirebaseDatabase.getInstance().getReference(ref).removeValue()
                            selected = -1
                            edit.text = getString(R.string.button_create)
                            edit.setBackgroundResource(R.color.blue500)
                            delete.text = getString(R.string.button_close)
                            delete.setBackgroundResource(R.color.grey700)
                        })
                        .setNegativeButton(getString(R.string.button_cancel), { _, _ -> toast_delete_cancel(this) })
                        .create().show()
            }
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<ViewHolder>() {

        lateinit var parent: ViewGroup

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
            this.parent = parent
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_renter, parent, false))
        }

        private fun clearBackground() {
            for (i in 0 until parent.childCount) {
                parent.getChildAt(i).setBackgroundResource(R.drawable.background_round_normal)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == selected) {
                holder.card.setBackgroundResource(R.drawable.background_round_selected)
            } else {
                holder.card.setBackgroundResource(R.drawable.background_round_normal)
            }

            holder.name.text = list[position].name

            holder.card.setOnClickListener { view ->
                clearBackground()
                if (selected == position) {
                    selected = -1
                    edit.text = getString(R.string.button_create)
                    edit.setBackgroundResource(R.color.blue500)
                    delete.text = getString(R.string.button_close)
                    delete.setBackgroundResource(R.color.grey700)
                } else {
                    selected = position
                    view.setBackgroundResource(R.drawable.background_round_selected)
                    edit.text = getString(R.string.button_edit)
                    edit.setBackgroundResource(R.color.teal700a)
                    delete.text = getString(R.string.button_delete)
                    delete.setBackgroundResource(R.color.pink500)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: View = itemView.findViewById(R.id.card)
        var name: TextView = itemView.findViewById(R.id.name)
    }
}
