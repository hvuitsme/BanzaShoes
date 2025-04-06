package com.hvuitsme.admin.ui.banner

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.CarouselAdapter
import com.hvuitsme.admin.data.model.Carousel
import com.hvuitsme.admin.data.remote.BannerDataResource
import com.hvuitsme.admin.data.repository.BannerRepoImpl
import com.hvuitsme.admin.databinding.FragmentBannerBinding
import com.hvuitsme.admin.service.CloudinaryUploader
import kotlinx.coroutines.launch

class BannerFragment : Fragment() {
    private var _binding: FragmentBannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    private var currentBannerToUpdate: Carousel? = null

    companion object {
        fun newInstance() = BannerFragment()
    }

    private lateinit var viewModel: BannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BannerRepoImpl(BannerDataResource())
        val factory = BannerViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BannerViewModel::class.java]

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    selectedImageUri = it
                    showReviewDialog(it,
                        onImageConfirmed = { confirmedUri ->
                            if (currentBannerToUpdate != null) {
                                uploadBannerImage(confirmedUri, isUpdate = true)
                            } else {
                                uploadBannerImage(confirmedUri, isUpdate = false)
                            }
                        },
                        onReSelectImage = {
                            it?.let {}
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        carouselAdapter = CarouselAdapter(
            emptyList(),
            onAddClick = {
                currentBannerToUpdate = null
                selectImage()
            },
            onEditClick = { banner ->
                currentBannerToUpdate = banner
                selectImage()
            },
            onDeleteClick = { banner ->
                showDeleteDialog(banner)
            }
        )
        binding.rvBanner.adapter = carouselAdapter
        binding.rvBanner.layoutManager = LinearLayoutManager(requireContext())

        viewModel.loadCarousel()
        viewModel.carousel.observe(viewLifecycleOwner) { carouselItems ->
            carouselAdapter.updateDataCarousel(carouselItems)
        }
    }

    private fun selectImage() {
        imagePickerLauncher.launch("image/*")
    }

    private fun showReviewDialog(
        selectedImageUri: Uri,
        onImageConfirmed: (Uri) -> Unit,
        onReSelectImage: () -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_review_banner, null)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivBannerPreview)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val ivEdit = dialogView.findViewById<ImageView>(R.id.ivEdit)

        ivPreview.setImageURI(selectedImageUri)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        ivEdit.setOnClickListener {
            dialog.dismiss()
            onReSelectImage()
        }

        btnOk.setOnClickListener {
            onImageConfirmed(selectedImageUri)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteDialog(banner: Carousel) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)
        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnYes.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                try {
                    if (banner.publicId.isEmpty()) {
                        println("PublicId empty, cannot delete banner.")
                        return@launch
                    }
                    val cloudResult = CloudinaryUploader.deleteImageBanner(banner.publicId)
                    println("Cloudinary result: $cloudResult")
                    if (cloudResult) {
                        viewModel.deleteBanner(banner,
                            onSuccess = { },
                            onError = { e -> println("Firebase error: ${e.message}") }
                        )
                    } else {
                        println("Cloudinary delete failed.")
                    }
                } catch (e: Exception) {
                    println("Exception deleting banner: ${e.message}")
                }
            }
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun uploadBannerImage(imageUri: Uri, isUpdate: Boolean) {
        val progressDialog = createProgressDialog()
        progressDialog.show()
        lifecycleScope.launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Error open InputStream")
                val (url, publicId) = CloudinaryUploader.uploadImageBanner(requireContext(), inputStream)
                if (isUpdate && currentBannerToUpdate != null) {
                    viewModel.updateBanner(
                        oldUrl = currentBannerToUpdate!!.url,
                        newUrl = url,
                        newPublicId = publicId,
                        onSuccess = {},
                        onError = { e ->}
                    )
                    currentBannerToUpdate = null
                } else {
                    viewModel.addBanner(
                        url = url,
                        publicId = publicId,
                        onSuccess = {},
                        onError = { e ->}
                    )
                }
            } catch (e: Exception) {
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun createProgressDialog(): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(null)
            .setMessage("Uploading...")
            .setCancelable(false)
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}