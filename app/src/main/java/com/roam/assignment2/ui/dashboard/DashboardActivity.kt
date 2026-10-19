package com.roam.assignment2.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.roam.assignment2.R
import com.roam.assignment2.databinding.ActivityDashboardBinding
import com.roam.assignment2.ui.details.DetailsActivity
import com.roam.assignment2.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    private val adapter = EntityAdapter { entity ->
        startActivity(DetailsActivity.newIntent(this, entity))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.inflateMenu(R.menu.dashboard_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                logout()
                true
            } else {
                false
            }
        }

        binding.entityList.layoutManager = LinearLayoutManager(this)
        binding.entityList.adapter = adapter
        binding.retryButton.setOnClickListener { viewModel.load() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: DashboardUiState) {
        binding.progress.isVisible = state is DashboardUiState.Loading
        binding.entityList.isVisible = state is DashboardUiState.Success
        binding.totalText.isVisible = state is DashboardUiState.Success
        binding.errorGroup.isVisible = state is DashboardUiState.Error

        when (state) {
            is DashboardUiState.Success -> {
                adapter.submitList(state.entities)
                binding.totalText.text = getString(R.string.items_total, state.total)
            }
            is DashboardUiState.Error -> binding.errorText.text = state.message
            DashboardUiState.Loading -> Unit
        }
    }

    private fun logout() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    companion object {
        fun newIntent(context: Context, keypass: String): Intent =
            Intent(context, DashboardActivity::class.java)
                .putExtra(DashboardViewModel.KEY_KEYPASS, keypass)
    }
}