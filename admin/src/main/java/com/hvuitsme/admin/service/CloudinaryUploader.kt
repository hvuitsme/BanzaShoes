package com.hvuitsme.admin.service

import android.content.Context
import com.cloudinary.Cloudinary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

object CloudinaryUploader {

    private const val CLOUD_NAME = "doio9ctca"
    private const val API_KEY = "559475578227815"
    private const val API_SECRET = "B4nRAllzsNMmeOzR5hn0nDwtnjE"

    private val config: HashMap<String, String> = hashMapOf(
        "cloud_name" to CLOUD_NAME,
        "api_key" to API_KEY,
        "api_secret" to API_SECRET,
        "secure" to "true"
    )

    private val cloudinary = Cloudinary(config)

    //Banner
    suspend fun uploadImageBanner(context: Context, inputStream: InputStream, defaultFileName: String? = null): Pair<String, String> {
        val newFileName = defaultFileName ?: "banner_${System.currentTimeMillis()}"
        val options = hashMapOf(
            "folder" to "Banner",
            "public_id" to newFileName
        )
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().upload(inputStream, options)
            val url = result["secure_url"] as String
            val publicId = result["public_id"] as String
            Pair(url, publicId)
        }
    }

    suspend fun deleteImageBanner(publicId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().destroy(publicId, emptyMap<String, String>())
            println("Cloudinary delete result: $result")
            (result["result"] as? String)?.equals("ok", ignoreCase = true) ?: false
        }
    }

    //Product
    suspend fun uploadProductImage(
        context: Context,
        inputStream: InputStream,
        folderPath: String,
        defaultFileName: String? = null
    ): Pair<String, String> {
        val newFileName = defaultFileName ?: "product_${System.currentTimeMillis()}"
        val options = hashMapOf(
            "folder" to folderPath,
            "public_id" to newFileName
        )
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().upload(inputStream, options)
            val url = result["secure_url"] as String
            val publicId = result["public_id"] as String
            Pair(url, publicId)
        }
    }

    suspend fun deleteProductImage(publicId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().destroy(publicId, emptyMap<String, String>())
            (result["result"] as? String)?.equals("ok", ignoreCase = true) ?: false
        }
    }

    //Category
    suspend fun uploadImageCategory(context: Context, inputStream: InputStream, defaultFileName: String? = null): Pair<String, String> {
        val newFileName = defaultFileName ?: "banner_${System.currentTimeMillis()}"
        val options = hashMapOf(
            "folder" to "Categories",
            "public_id" to newFileName
        )
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().upload(inputStream, options)
            val url = result["secure_url"] as String
            val publicId = result["public_id"] as String
            Pair(url, publicId)
        }
    }

    suspend fun deleteImageCategory(publicId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result = cloudinary.uploader().destroy(publicId, emptyMap<String, String>())
            println("Cloudinary delete result: $result")
            (result["result"] as? String)?.equals("ok", ignoreCase = true) ?: false
        }
    }
}