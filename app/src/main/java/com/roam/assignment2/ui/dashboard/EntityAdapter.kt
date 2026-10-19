package com.roam.assignment2.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.roam.assignment2.databinding.ItemEntityBinding
import com.roam.assignment2.domain.model.Entity

class EntityAdapter(
    private val onItemClick: (Entity) -> Unit
) : ListAdapter<Entity, EntityAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ItemEntityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: Entity) {
            binding.titleText.text = entity.title
            // Summary only - the description is intentionally left for the Details screen.
            binding.summaryText.text = entity.summary()
                .joinToString(separator = "\n") { (label, value) -> "$label: $value" }
            binding.root.setOnClickListener { onItemClick(entity) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Entity>() {
        override fun areItemsTheSame(oldItem: Entity, newItem: Entity) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Entity, newItem: Entity) = oldItem == newItem
    }
}