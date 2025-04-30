package com.hvuitsme.admin.ui.product

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.ProductAdapter
import com.hvuitsme.admin.adapter.ProductImageAdapter
import com.hvuitsme.admin.data.model.Product
import com.hvuitsme.admin.data.model.Size
import com.hvuitsme.admin.data.remote.CategoryDataSource
import com.hvuitsme.admin.data.remote.ProductDataSource
import com.hvuitsme.admin.data.repository.CategoryRepoImpl
import com.hvuitsme.admin.data.repository.ProductRepoImpl
import com.hvuitsme.admin.databinding.FragmentBrandBinding
import com.hvuitsme.admin.service.CloudinaryUploader
import kotlinx.coroutines.launch

class BrandFragment : Fragment() {
    private var _binding: FragmentBrandBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    private val args: BrandFragmentArgs by navArgs()

    private var currentDialogEtTitle: EditText? = null
    private var currentDialogImageAdapter: ProductImageAdapter? = null
    private var currentDialogImageUrls: MutableList<String> = mutableListOf()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri ->
//            val titleText = currentDialogEtTitle?.text?.toString()?.trim().takeIf { it?.isNotEmpty() == true } ?: "Default Product"
            val autoBrand = args.brand.replaceFirstChar { it.uppercase() }
//            val folderPath = "Product/$autoBrand/$titleText"
            val folderPath = "Product/$autoBrand"

            lifecycleScope.launch {
                try {
                    requireContext().contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                        val (url, _) = CloudinaryUploader.uploadProductImage(requireContext(), inputStream, folderPath)
                        currentDialogImageUrls.add(url)
                        currentDialogImageAdapter?.updateData(currentDialogImageUrls)
                    }
                } catch (e: Exception) {
                    println("Error uploading image: ${e.message}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = ProductRepoImpl(ProductDataSource())
        val factory = ProductViewModelFactory(repository, CategoryRepoImpl(CategoryDataSource()))
        viewModel = ViewModelProvider(requireActivity(), factory)[ProductViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBrandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductAdapter(
            onAddClick = { showEditDialog(null) },
            onEditClick = { product -> showEditDialog(product) },
            onDeleteClick = { product -> showDeleteDialog(product) }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(context)
        binding.rvProducts.adapter = adapter

        val brand = args.brand
        viewModel.products.observe(viewLifecycleOwner) { productList ->
            val filteredList = productList.filter {
                it.brand.replace("\\s".toRegex(), "")
                    .equals(brand.replace("\\s".toRegex(), ""), ignoreCase = true)
            }
            adapter.updateDataProduct(filteredList)
        }
    }

    private fun showEditDialog(product: Product?) {
        val editDialog = Dialog(requireActivity())
        editDialog.setContentView(R.layout.dialog_edit_product)
        val etTitle = editDialog.findViewById<EditText>(R.id.etTitle)
        val etDescription = editDialog.findViewById<EditText>(R.id.etDescription)
        val etPrice = editDialog.findViewById<EditText>(R.id.etPrice)
        val rvImages = editDialog.findViewById<RecyclerView>(R.id.rvProductImages)
        val llSizes = editDialog.findViewById<LinearLayout>(R.id.llSizes)
        val btnAddSize = editDialog.findViewById<Button>(R.id.btnAddSize)
        val btnCancel = editDialog.findViewById<Button>(R.id.btnCancel)
        val btnSave = editDialog.findViewById<Button>(R.id.btnSave)

        currentDialogEtTitle = etTitle
        currentDialogImageUrls = if (product != null) product.imageUrls.toMutableList() else mutableListOf()
        currentDialogImageAdapter = ProductImageAdapter(
            imageUrls = currentDialogImageUrls,
            onAddClick = { imagePickerLauncher.launch("image/*") },
            onDelImageClick = { url ->
                currentDialogImageUrls.remove(url)
                currentDialogImageAdapter?.updateData(currentDialogImageUrls)
            }
        )

        rvImages.layoutManager = GridLayoutManager(requireContext(), 3)
        rvImages.isNestedScrollingEnabled = false
        rvImages.adapter = currentDialogImageAdapter

        if (product != null) {
            etTitle.setText(product.title)
            etDescription.setText(product.description)
            etPrice.setText(product.price.toString())
            product.sizes.forEach { sizeItem ->
                addSizeView(llSizes, sizeItem.size, sizeItem.qty.toString())
            }
        }

        btnAddSize.setOnClickListener { addSizeView(llSizes, "", "") }
        btnCancel.setOnClickListener { editDialog.dismiss() }
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val sizes = mutableListOf<Size>()
            for (i in 0 until llSizes.childCount) {
                val sizeItemView = llSizes.getChildAt(i)
                val etSize = sizeItemView.findViewById<EditText>(R.id.etSize)
                val etQty = sizeItemView.findViewById<EditText>(R.id.etQty)
                val sizeValue = etSize.text.toString().trim()
                val qtyValue = etQty.text.toString().toIntOrNull()
                if (sizeValue.isNotEmpty() && qtyValue != null && qtyValue >= 0) {
                    sizes.add(Size(size = sizeValue, qty = qtyValue))
                }
            }
            val autoBrand = args.brand.replaceFirstChar { it.uppercase() }
            val autoCateId = args.brand
            val newProduct = if (product != null) {
                product.copy(
                    title = title,
                    description = description,
                    price = price,
                    brand = autoBrand,
                    cateId = autoCateId,
                    sizes = sizes,
                    imageUrls = currentDialogImageUrls,
                    rating = product.rating
                )
            } else {
                Product(
                    id = "",
                    title = title,
                    description = description,
                    price = price,
                    brand = autoBrand,
                    cateId = autoCateId,
                    rating = 0.0,
                    imageUrls = currentDialogImageUrls,
                    sizes = sizes
                )
            }
            viewModel.saveProduct(newProduct)
            editDialog.dismiss()
        }

        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.7).toInt()
        editDialog.window?.setLayout(width, height)
        editDialog.show()
    }

    private fun addSizeView(linearLayout: LinearLayout, size: String, qty: String) {
        val sizeItemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_size_quantity, linearLayout, false)
        val etSize = sizeItemView.findViewById<EditText>(R.id.etSize)
        val etQty = sizeItemView.findViewById<EditText>(R.id.etQty)
        val btnRemove = sizeItemView.findViewById<ImageButton>(R.id.btnRemoveSize)
        etSize.setText(size)
        etQty.setText(qty)
        btnRemove.setOnClickListener { linearLayout.removeView(sizeItemView) }
        linearLayout.addView(sizeItemView)
    }

    private fun showDeleteDialog(product: Product) {
        val deleteDialog = Dialog(requireActivity())
        deleteDialog.setContentView(R.layout.dialog_delete)
        deleteDialog.findViewById<Button>(R.id.btnNo).setOnClickListener { deleteDialog.dismiss() }
        deleteDialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
            lifecycleScope.launch {
                if (product.imageUrls.isNotEmpty()) {
                    product.imageUrls.forEach { imageUrl ->
                        val publicId = extractPublicIdFromUrl(imageUrl)
                        if (publicId != null) {
                            val deletionSuccess = CloudinaryUploader.deleteProductImage(publicId)
                            if (deletionSuccess) {
                                println("Deleted image with public id: $publicId")
                            } else {
                                println("Failed to delete image with public id: $publicId")
                            }
                        } else {
                            println("Không thể trích xuất public id từ URL: $imageUrl")
                        }
                    }
                }
                viewModel.deleteProduct(product)
                deleteDialog.dismiss()
            }
        }
        deleteDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        deleteDialog.show()
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

    companion object {
        fun newInstance(brand: String) = BrandFragment().apply {
            arguments = bundleOf("brand" to brand)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentDialogEtTitle = null
        currentDialogImageAdapter = null
        currentDialogImageUrls.clear()
    }
}