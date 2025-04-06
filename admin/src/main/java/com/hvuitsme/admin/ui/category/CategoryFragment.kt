package com.hvuitsme.admin.ui.category

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.CategoryAdapter
import com.hvuitsme.admin.data.model.Category
import com.hvuitsme.admin.data.remote.CategoryDataSource
import com.hvuitsme.admin.data.repository.CategoryRepoImpl
import com.hvuitsme.admin.databinding.FragmentCategoryBinding
import com.hvuitsme.admin.service.CloudinaryUploader
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    private var currentCategoryImageUrl: String? = null
    private var currentCategory: Category? = null
    private var isEditOperation = false

    private var dialogImageView: ImageView? = null
    private var dialogEditImageView: ImageView? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                lifecycleScope.launch {
                    try {
                        requireContext().contentResolver.openInputStream(it)?.use { inputStream ->
                            val (url, _) = CloudinaryUploader.uploadImageCategory(requireContext(), inputStream)
                            currentCategoryImageUrl = url
                            dialogImageView?.let { iv ->
                                Glide.with(requireContext()).load(url).into(iv)
                            }
                            dialogImageView?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    companion object {
        fun newInstance() = CategoryFragment()
    }

    private lateinit var viewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CategoryRepoImpl(CategoryDataSource())
        val factory = CategoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_category, container, false)
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        categoryAdapter = CategoryAdapter(
            categoryItem = emptyList(),
            onAddClick = { showAddEditDialog(isEdit = false, category = null) },
            onEditClick = { category -> showAddEditDialog(isEdit = true, category = category) },
            onDeleteClick = { category -> showDeleteDialog(category) }
        )

        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        viewModel.loadCategories()
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateDataCategory(categories)
        }
    }

    private fun showAddEditDialog(isEdit: Boolean, category: Category?) {
        isEditOperation = isEdit
        currentCategory = category
        currentCategoryImageUrl = if (isEdit) category?.url else null

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_edit_category)

        val ivPreview = dialog.findViewById<ImageView>(R.id.ivReview)
        val ivEdit = dialog.findViewById<ImageView>(R.id.ivEdit)
        val etBrand = dialog.findViewById<EditText>(R.id.etBrand)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        dialogImageView = ivPreview
        dialogEditImageView = ivEdit

        if (isEdit && category != null) {
            etBrand.setText(category.cateId)
        } else {
            etBrand.setText("")
        }

        ivPreview.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        ivEdit.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        ivEdit.visibility = if (currentCategoryImageUrl.isNullOrEmpty()) View.GONE else View.VISIBLE

        currentCategoryImageUrl?.let { url ->
            Glide.with(requireContext()).load(url).into(ivPreview)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val brandInput = etBrand.text.toString().trim()
            if (brandInput.isEmpty()) {
                Toast.makeText(requireContext(), "Please input brand", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentCategoryImageUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isEditOperation && currentCategory != null) {
                viewModel.updateCategory(
                    oldUrl = currentCategory!!.url,
                    brand = brandInput,
                    newUrl = currentCategoryImageUrl!!,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = { error ->
                        Toast.makeText(requireContext(), "Update Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                viewModel.addCategory(
                    brand = brandInput,
                    url = currentCategoryImageUrl!!,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Add successfully", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = { error ->
                        Toast.makeText(requireContext(), "Add error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun showDeleteDialog(category: Category) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete)
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        btnNo.setOnClickListener { dialog.dismiss() }
        btnYes.setOnClickListener {
            lifecycleScope.launch {
                val publicId = extractPublicIdFromUrl(category.url)
                if (publicId != null) {
                    val deletionSuccess = CloudinaryUploader.deleteImageCategory(publicId)
                    if (deletionSuccess) {
                        viewModel.deleteCategory(category,
                            onSuccess = {
                                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                Toast.makeText(requireContext(), "Error deleting on Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(requireContext(), "Error deleting image on Cloudinary", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid image URL", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }
        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun extractPublicIdFromUrl(url: String): String? {
        return try {
            val uploadMarker = "/upload/"
            val uploadIndex = url.indexOf(uploadMarker)
            if (uploadIndex == -1) return null
            val substringAfterUpload = url.substring(uploadIndex + uploadMarker.length)
            val versionRegex = Regex("""^v\d+/""")
            val withoutVersion = versionRegex.replace(substringAfterUpload, "")
            val dotIndex = withoutVersion.lastIndexOf('.')
            if (dotIndex != -1) withoutVersion.substring(0, dotIndex) else withoutVersion
        } catch (e: Exception) {
            println("Error extracting public id from URL $url: ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogImageView = null
        dialogEditImageView = null
        currentCategory = null
        currentCategoryImageUrl = null
    }
}