package com.hvuitsme.banzashoes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.banzashoes.data.model.Carousel

class HomeViewModel : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _carousel = MutableLiveData<List<Carousel>>()
    val carousel: MutableLiveData<List<Carousel>> = _carousel

    fun loadCarousel(){
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<Carousel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(Carousel::class.java)
                    if (list != null){
                        lists.add(list)
                    }
                }
                _carousel.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}