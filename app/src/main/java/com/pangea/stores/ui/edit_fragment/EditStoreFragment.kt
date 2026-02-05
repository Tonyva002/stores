package com.pangea.stores.ui.edit_fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import com.pangea.stores.R
import com.pangea.stores.databinding.FragmentEditStoreBinding
import com.pangea.stores.domain.model.Store
import com.pangea.stores.ui.states.edit_fragment_state.EditStoreEvent
import com.pangea.stores.ui.states.edit_fragment_state.EditStoreUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditStoreFragment : Fragment() {


    private lateinit var binding: FragmentEditStoreBinding
    private val viewModel: EditStoreViewModel by viewModels()

    private var isEditMode = false
    private lateinit var store: Store

    private lateinit var menuProvider: MenuProvider


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0L) ?: 0L
        isEditMode = id != 0L

        setupActionBar()
        setupTextFields()
        setupMenu()


        if (isEditMode) {
            viewModel.loadStore(id)
        } else {
            store = Store()
        }

        observeUiState()
        observeEvents()
    }

    // -------------------------
    // Observers
    // -------------------------
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is EditStoreUiState.Success -> {
                            store = state.store
                            fillFields(store)
                        }

                        else -> Unit
                    }
                }

            }
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is EditStoreEvent.ShowMessage -> {
                            Toast.makeText(requireContext(), event.resId, Toast.LENGTH_SHORT).show()
                        }

                        EditStoreEvent.Created -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.store_created_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity()
                                .supportFragmentManager
                                .popBackStack()
                        }

                        EditStoreEvent.Updated -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.store_updated_success,
                                Toast.LENGTH_SHORT
                            ).show()

                            requireActivity()
                                .supportFragmentManager
                                .popBackStack()
                        }

                    }

                }

            }

        }
    }


    // -------------------------
    // Menu
    // -------------------------
    private fun setupMenu() {
        menuProvider = object : MenuProvider {

            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {

                    R.id.action_save -> {
                        val isValid = validateFields(
                            binding.tilName,
                            binding.tilPhone,
                            binding.tilPhotoUrl
                        )
                        if (isValid) saveStore()
                        true
                    }

                    android.R.id.home -> {
                        requireActivity()
                            .onBackPressedDispatcher
                            .onBackPressed()
                        true
                    }

                    else -> false
                }
            }
        }

        requireActivity().addMenuProvider(
            menuProvider,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }


    // -------------------------
    // Actions
    // -------------------------
    private fun saveStore() {
        val updatedStore = store.copy(
            name = binding.etName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim(),
            website = binding.etWebsite.text.toString().trim(),
            photoUrl = binding.etPhotoUrl.text.toString().trim()
        )

        viewModel.saveStore(original = store, updated =  updatedStore)

    }



    // -------------------------
    // UI
    // -------------------------
    private fun fillFields(store: Store) = with(binding) {
        etName.setText(store.name)
        etPhone.setText(store.phone)
        etWebsite.setText(store.website)
        etPhotoUrl.setText(store.photoUrl)
        loadImage(store.photoUrl)
    }

    fun loadImage(photoUrl: String) {
        Glide.with(requireContext())
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgPhoto)
    }

    private fun setupActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (isEditMode)
                getString(R.string.edit_store)
            else
                getString(R.string.create_store)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        textFields.forEach { field ->
            val value = field.editText?.text.toString().trim()

            if (value.isEmpty()) {
                field.error = getString(R.string.required)
                isValid = false
            } else {
                field.error = null
            }

        }

        if (!isValid) {
            Toast.makeText(
                requireContext(),
                R.string.store_message_valid,
                Toast.LENGTH_SHORT
            ).show()
        }

        return isValid

    }

    private fun setupTextFields() = with(binding) {
        etName.addTextChangedListener {
            validateFields(tilName)
        }

        etPhone.addTextChangedListener {
            validateFields(tilPhone)
        }

        etWebsite.addTextChangedListener {
            val text = it.toString().trim()
            if (text.isNotEmpty()) {
                validateUrlField(text, tilWebsite, etWebsite)
            } else {
                tilWebsite.error = null
            }
        }

    }

    private fun validateUrlField(
        url: String,
        textInputLayout: TextInputLayout,
        editText: EditText
    ): Boolean {
        return if (!Patterns.WEB_URL.matcher(url).matches()) {
            textInputLayout.error = getString(R.string.invalid_url)
            editText.requestFocus()
            false
        } else {
            textInputLayout.error = null
            true
        }
    }


    override fun onDestroyView() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = getString(R.string.app_name)
        }
        super.onDestroyView()
    }


}