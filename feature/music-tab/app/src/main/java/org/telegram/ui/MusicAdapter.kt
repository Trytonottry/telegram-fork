package org.telegram.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.telegram.R

class MusicAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var items = listOf<AudioItem>()

    fun submitList(list: List<AudioItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.itemView.setOnClickListener { onClick(item.url) }
    }

    class MusicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.audioTitle)
    }
}
