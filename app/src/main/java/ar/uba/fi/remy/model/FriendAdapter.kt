package ar.uba.fi.remy.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R

class  FriendAdapter(var friendList:ArrayList<Friend>):RecyclerView.Adapter<FriendAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_add_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder:FriendAdapter.ViewHolder, position: Int) {
        holder.bindItems(friendList[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data:Friend){
            val name:TextView = itemView.findViewById(R.id.friend_name)
            val username:TextView = itemView.findViewById(R.id.friend_username)

            name.text = data.first_name + " " + data.last_name
            username.text = data.username

        }

    }

}