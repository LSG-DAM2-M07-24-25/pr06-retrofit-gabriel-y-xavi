package com.example.retrofitapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitapp.api.Repository
import com.example.retrofitapp.model.Data
import kotlinx.coroutines.launch

sealed class ApiState {
    object Loading : ApiState()
    data class Success(val data: Data) : ApiState()
    data class Error(val message: String) : ApiState()
}

class APIViewModel : ViewModel() {
    private val repository = Repository()
    
    private val _apiState = MutableLiveData<ApiState>(ApiState.Loading)
    val apiState: LiveData<ApiState> = _apiState

    init {
        getCharacters()
    }

    fun getCharacters() {
        _apiState.value = ApiState.Loading
        
        viewModelScope.launch {
            try {
                val response = repository.getAllCharacters()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("APIViewModel", "Data received: $data")
                        _apiState.value = ApiState.Success(data)
                    } ?: run {
                        _apiState.value = ApiState.Error("No se recibieron datos")
                        Log.e("APIViewModel", "Response body is null")
                    }
                } else {
                    _apiState.value = ApiState.Error("Error: ${response.code()} - ${response.message()}")
                    Log.e("APIViewModel", "Error response: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _apiState.value = ApiState.Error("Error de red: ${e.localizedMessage ?: "Error desconocido"}")
                Log.e("APIViewModel", "Exception while fetching characters", e)
            }
        }
    }
}