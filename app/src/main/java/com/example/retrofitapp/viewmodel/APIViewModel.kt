package com.example.retrofitapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.retrofitapp.api.RetrofitInstance
import com.example.retrofitapp.model.Data
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.model.database.AppDatabase
import com.example.retrofitapp.model.toEntity
import com.example.retrofitapp.model.toResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ApiState {
    object Loading : ApiState()
    data class Success(val data: Data) : ApiState()
    data class Error(val error: String) : ApiState()
}

class APIViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val characterDao = database.characterDao()
    
    private val _apiState = MutableLiveData<ApiState>()
    val apiState: LiveData<ApiState> = _apiState
    
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _selectedCharacter = MutableLiveData<Result?>(null)
    val selectedCharacter: LiveData<Result?> = _selectedCharacter

    private val _favorites = MutableLiveData<List<Result>>(emptyList())
    val favorites: LiveData<List<Result>> = _favorites

    init {
        getCharacters()
        loadFavorites()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            searchCharacters(query)
        } else {
            getCharacters()
        }
    }

    fun setSelectedCharacter(character: Result?) {
        _selectedCharacter.value = character
    }

    fun toggleFavorite(character: Result) {
        val currentFavorites = _favorites.value.orEmpty().toMutableList()
        if (currentFavorites.any { it.id == character.id }) {
            currentFavorites.removeAll { it.id == character.id }
        } else {
            currentFavorites.add(character)
        }
        _favorites.value = currentFavorites
        saveFavorites(currentFavorites)
    }

    private fun saveFavorites(favorites: List<Result>) {
        viewModelScope.launch {
            // Aquí podrías guardar los favoritos en Room o SharedPreferences
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            // Aquí podrías cargar los favoritos desde Room o SharedPreferences
        }
    }

    fun getCharacters() {
        viewModelScope.launch {
            try {
                _apiState.value = ApiState.Loading
                
                // Observar datos locales
                characterDao.getAllCharacters()
                    .map { entities -> Data(entities.map { it.toResult() }) }
                    .catch { emit(Data(emptyList())) }
                    .collect { localData ->
                        if (localData.results.isNotEmpty()) {
                            _apiState.value = ApiState.Success(localData)
                        }
                        
                        // Actualizar desde la API
                        try {
                            val response = RetrofitInstance.api.getCharacters()
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()!!
                                // Guardar en la base de datos local
                                characterDao.deleteAllCharacters()
                                characterDao.insertCharacters(data.results.map { it.toEntity() })
                                _apiState.value = ApiState.Success(data)
                            } else {
                                if (localData.results.isEmpty()) {
                                    _apiState.value = ApiState.Error("Error: ${response.code()}")
                                }
                            }
                        } catch (e: Exception) {
                            if (localData.results.isEmpty()) {
                                _apiState.value = ApiState.Error(e.message ?: "Error desconocido")
                            }
                        }
                    }
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun searchCharacters(query: String) {
        viewModelScope.launch {
            try {
                characterDao.searchCharacters(query)
                    .map { entities -> Data(entities.map { it.toResult() }) }
                    .catch { emit(Data(emptyList())) }
                    .collect { data ->
                        _apiState.value = ApiState.Success(data)
                    }
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Error en la búsqueda")
            }
        }
    }
}