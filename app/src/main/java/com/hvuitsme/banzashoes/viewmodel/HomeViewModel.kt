package com.hvuitsme.banzashoes.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product

class HomeViewModel : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _carousel = MutableLiveData<List<Carousel>>()
    val carousel: MutableLiveData<List<Carousel>> = _carousel

    private val _category = MutableLiveData<List<Category>>()
    val category: MutableLiveData<List<Category>> = _category

    private val _product = MutableLiveData<List<Product>>()
    val product: MutableLiveData<List<Product>> = _product

    fun loadCarousel(){
        val carouselRef = firebaseDatabase.getReference("Banner")

        carouselRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val carouselList = mutableListOf<Carousel>()
                for (child in snapshot.children){
                    val carouselItem = child.getValue(Carousel::class.java)
                    if(carouselItem != null){
                        carouselList.add(carouselItem)
                    }
                    _carousel.value = carouselList
                    Log.d("HomeFragment", "Carousel List: $carouselList")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching carousel data: ${error.message}")
            }

        })
    }

    fun loadCategory(){
        val categoryRef = firebaseDatabase.getReference("Categories")

        categoryRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList = mutableListOf<Category>()
                for (child in snapshot.children){
                    val categoryItem = child.getValue(Category::class.java)
                    if(categoryItem != null){
                        categoryList.add(categoryItem)
                    }
                    _category.value = categoryList
                    Log.d("HomeFragment", "Category List: $categoryList")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching category data: ${error.message}")
            }

        })
    }

    fun loadProduct(selectedCateId: String = ""){
        val productRef = firebaseDatabase.getReference("Product")
        val query = productRef.orderByChild("cateId").equalTo(selectedCateId)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (child in snapshot.children){
                    val productItem = child.getValue(Product::class.java)
                    if(productItem != null){
                        productList.add(productItem)
                    }
                    _product.value = productList
                    Log.d("HomeFragment", "Product List: $productList")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching product data: ${error.message}")
            }

        })
    }
}