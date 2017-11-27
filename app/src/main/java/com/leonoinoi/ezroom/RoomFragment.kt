package com.leonoinoi.ezroom


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
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
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.fragment_room.*

class RoomFragment : Fragment() {

    private val list = ArrayList<Room>()
    private lateinit var myadapter: MyAdapter
    private var selected = -1
    private var view_old: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_room, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val name_building = arguments.getString("name_building")

        //Recycle View -----------------------------------------------------------------------------
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = GridLayoutManager(context, 3)
        myadapter = MyAdapter()
        recycler_view.adapter = myadapter

        // Firebase --------------------------------------------------------------------------------
        FirebaseAuth.getInstance().addAuthStateListener { firebase ->
            if (firebase.currentUser == null) {
                startActivity(Intent(context, Login::class.java))
            } else {
                val ref = "/${firebase.currentUser!!.uid}/ROOM"
                FirebaseDatabase
                        .getInstance().getReference(ref)
                        .orderByChild("name_building").equalTo(name_building)
                        .addValueEventListener(ListenerRoom())
            }
        }

        // Button ----------------------------------------------------------------------------------
        edit.setOnClickListener {
            if (selected == -1) {
                startActivity(Intent(context, RoomEdit::class.java)
                        .putExtra("command", "create")
                        .putExtra("name_building", name_building))
            } else {
                startActivity(Intent(context, RoomEdit::class.java)
                        .putExtra("command", "edit")
                        .putExtra("room", list[selected]))
            }
        }

        delete.setOnClickListener {
            if (selected == -1) {
                activity.finish()
            } else {
                AlertDialog.Builder(context)
                        .setMessage(getString(R.string.message_delete))
                        .setPositiveButton(getString(R.string.button_delete), { _, _ ->
                            val ref = "/${FirebaseAuth.getInstance().currentUser!!.uid}/ROOM/${list[selected].id}"
                            FirebaseDatabase.getInstance().getReference(ref).removeValue()
                            selected = -1
                            view_old = null
                            edit.text = getString(R.string.button_create)
                            edit.setBackgroundResource(R.color.blue500)
                            delete.text = getString(R.string.button_close)
                            delete.setBackgroundResource(R.color.grey700)
                        })
                        .setNegativeButton(getString(R.string.button_cancel), { _, _ ->
                            StyleableToast.Builder(context)
                                    .text(getString(R.string.message_delete_cancel))
                                    .textColor(Color.WHITE)
                                    .icon(R.drawable.ic_cancel)
                                    .backgroundColor(ContextCompat.getColor(context, R.color.pink500))
                                    .build().show()
                        })
                        .create().show()
            }
        }
    }

    inner class ListenerRoom : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(data: DataSnapshot?) {
            if (data == null) return

            list.clear()
            data.children.mapTo(list) { it.getValue(Room::class.java)!! }
            myadapter.notifyDataSetChanged()
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<ViewHolder>() {

        lateinit var parent: ViewGroup

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
            this.parent = parent
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_room, parent, false))
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

            holder.id.text = list[position].id

            holder.card.setOnClickListener { view ->
                clearBackground()
                if (selected == position) {
                    selected = -1
                    edit.text = getString(R.string.button_create)
                    edit.setBackgroundResource(R.color.blue500)
                    delete.text = getString(R.string.button_close)
                    delete.setBackgroundResource(R.color.grey700)
                } else {
                    view.setBackgroundResource(R.drawable.background_round_selected)
                    selected = position
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
        var id: TextView = itemView.findViewById(R.id.id)
    }
}
