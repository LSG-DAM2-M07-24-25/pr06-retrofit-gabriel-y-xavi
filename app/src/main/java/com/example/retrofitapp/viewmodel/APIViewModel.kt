package com.example.retrofitapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitapp.api.Repository
import com.example.retrofitapp.model.Data
import kotlinx.coroutines.launch

class APIViewModel : ViewModel() {

    private val repository = Repository()
    private val _loading = MutableLiveData(true)
    val loading = _loading
    private val _characters = MutableLiveData<Data>()
    val characters = _characters

    fun getCharacters() {
        viewModelScope.launch {
            try {
                _loading.value = true  // Aseguramos que loading está en true al iniciar
                val response = repository.getAllCharacters()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    // Verificamos que results no sea null
                    if (data.results != null) {
                        _characters.value = data
                    } else {
                        Log.e("Error", "Results is null")
                    }
                } else {
                    Log.e("Error", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

}