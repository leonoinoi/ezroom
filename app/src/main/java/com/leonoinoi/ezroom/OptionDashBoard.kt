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
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_option_dash_board.*

class OptionDashBoard : AppCompatActivity() {

    private val list = ArrayList<Option>()
    private lateinit var myAdapter: MyAdapter
    private var selected = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_dash_board)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toast_message_loading(this)

        // Recycle View ----------------------------------------------------------------------------
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        myAdapter = MyAdapter()
        recycler_view.adapter = myAdapter

        // Button ----------------------------------------------------------------------------------
        edit.setOnClickListener {
            if (selected == -1) {
                startActivity(Intent(this, OptionEdit::class.java)
                        .putExtra("command", "create"))
            } else {
                startActivity(Intent(this, OptionEdit::class.java)
                        .putExtra("command", "edit")
                        .putExtra("option", list[selected]))
            }
        }

        delete.setOnClickListener {
            if (selected == -1) {
                super.onBackPressed()
            } else {
                AlertDialog.Builder(this)
                        .setMessage(getString(R.string.message_delete))
                        .setPositiveButton(getString(R.string.button_delete), { _, _ ->
                            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/OPTION/${list[selected].name}"
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

        // Firebase --------------------------------------------------------------------------------
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(this, Login::class.java))
            } else {
                val ref = "/${firebase.currentUser!!.uid}/OPTION"
                FirebaseDatabase.getInstance().getReference(ref).addValueEventListener(Listener())
            }
        }
    }

    inner class Listener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(data: DataSnapshot?) {
            if (data == null) return

            list.clear()
            data.children.mapTo(list) { it.getValue(Option::class.java)!! }
            myAdapter.notifyDataSetChanged()
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<ViewHolder>() {

        lateinit var parent: ViewGroup

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
            this.parent = parent
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_option, parent, false))
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
            holder.type.text = list[position].type
            holder.price.text = list[position].price.toString()

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
        var type: TextView = itemView.findViewById(R.id.type)
        var price: TextView = itemView.findViewById(R.id.price)
    }
}
