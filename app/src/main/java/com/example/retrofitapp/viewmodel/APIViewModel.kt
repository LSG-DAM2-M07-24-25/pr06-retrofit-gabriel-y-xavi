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
        Log.d("APIViewModel", "Starting to fetch characters")
        
        viewModelScope.launch {
            try {
                val response = repository.getAllCharacters()
                Log.d("APIViewModel", "Response received: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        if (data.characters.isNotEmpty()) {
                            Log.d("APIViewModel", "Characters received: ${data.characters.size}")
                            Log.d("APIViewModel", "First character image URL: ${data.characters.firstOrNull()?.image}")
                            _apiState.value = ApiState.Success(data)
                        } else {
                            _apiState.value = ApiState.Error("No se encontraron personajes")
                        }
                    } ?: run {
                        _apiState.value = ApiState.Error("No se recibieron datos")
                    }
                } else {
                    _apiState.value = ApiState.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("APIViewModel", "Error fetching characters", e)
                _apiState.value = ApiState.Error("Error de red: ${e.localizedMessage ?: "Error desconocido"}")
            }
        }
    }
}