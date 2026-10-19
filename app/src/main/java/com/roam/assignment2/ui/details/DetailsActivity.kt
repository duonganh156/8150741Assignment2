package com.roam.assignment2.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.roam.assignment2.R
import com.roam.assignment2.databinding.ActivityDetailsBinding
import com.roam.assignment2.databinding.ItemDetailRowBinding
import com.roam.assignment2.domain.model.Entity
import com.roam.assignment2.util.toDisplayLabel
import kotlin.collections.component1
import kotlin.collections.component2

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entity = IntentCompat.getParcelableExtra(intent, EXTRA_ENTITY, Entity::class.java)
        if (entity == null) {
            finish()
            return
        }

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.title = entity.title.ifBlank { getString(R.string.details_title) }

        entity.properties.forEach { (key, value) ->
            val row = ItemDetailRowBinding.inflate(layoutInflater, binding.propertiesContainer, false)
            row.labelText.text = key.toDisplayLabel()
            row.valueText.text = value
            binding.propertiesContainer.addView(row.root)
        }

        binding.descriptionText.text =
            entity.description.ifBlank { getString(R.string.no_description) }
    }

    companion object {
        private const val EXTRA_ENTITY = "entity"

        fun newIntent(context: Context, entity: Entity): Intent =
            Intent(context, DetailsActivity::class.java).putExtra(EXTRA_ENTITY, entity)
    }
}