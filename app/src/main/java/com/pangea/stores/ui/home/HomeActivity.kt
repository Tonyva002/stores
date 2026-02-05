package com.pangea.stores.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pangea.stores.R
import com.pangea.stores.databinding.ActivityHomeBinding
import com.pangea.stores.domain.model.Store
import com.pangea.stores.ui.edit_fragment.EditStoreFragment
import com.pangea.stores.ui.home.adapter.StoreAdapter
import com.pangea.stores.ui.states.home_state.HomeEvent
import com.pangea.stores.ui.states.home_state.HomeUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: StoreAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val hasFragments =
                supportFragmentManager.backStackEntryCount > 0

            if (hasFragments) {
                binding.fab.hide()
            } else {
                binding.fab.show()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = getString(R.string.app_name)
            }
        }

        setupRecyclerView()
        observeUiState()
        observeEvents()
        newStore()

    }


    private fun newStore() {
        binding.fab.setOnClickListener {
            launchEditFragment()
        }
    }

    private fun setupRecyclerView() {
        adapter = StoreAdapter(
            onClick = { store ->
                openDetail(store)
            },
            onFavorite = { store ->
                viewModel.toggleFavorite(store)
            },
            onDelete = { store ->
                showOptionsDialog(store)
            }
        )

        gridLayoutManager = GridLayoutManager(
            this, resources.getInteger(R.integer.main_columns)
        )

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = this@HomeActivity.adapter
        }
    }


    // -------------------------
    // Navigation
    // -------------------------
    private fun openDetail(store: Store) {
        val args = Bundle().apply {
            putLong(getString(R.string.arg_id), store.id)
        }
        launchEditFragment(args)

    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment().apply {
            arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()

    }

    // -------------------------
    // Dialogs
    // -------------------------
    private fun showOptionsDialog(store: Store) {
        val options = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(options) { _, index ->
                when (index) {
                    0 -> confirmDelete(store)
                    1 -> dial(store.phone)
                    2 -> goToWebsite(store.website)
                }
            }
            .show()
    }

    private fun confirmDelete(store: Store) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                viewModel.delete(store)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    // -------------------------
    // Intents
    // -------------------------
    private fun dial(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
        startIntent(intent)
    }

    private fun goToWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_website, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, website.toUri())
        startIntent(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else Toast.makeText(this, R.string.no_compatible_app_found, Toast.LENGTH_SHORT).show()
    }


    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            // optional loading
                        }

                        is HomeUiState.Success -> {
                            adapter.submitList(state.stores)
                        }

                        is HomeUiState.Error -> {
                            Toast.makeText(this@HomeActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }

        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeEvent.ShowMessage -> {
                            Toast.makeText(this@HomeActivity, event.resId, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

        }
    }


}