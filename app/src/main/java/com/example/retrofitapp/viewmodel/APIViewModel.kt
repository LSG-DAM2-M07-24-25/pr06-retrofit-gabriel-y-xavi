package com.example.retrofitapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.retrofitapp.api.APIInterface
import com.example.retrofitapp.model.Data
import com.example.retrofitapp.model.Info
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.model.database.AppDatabase
import com.example.retrofitapp.model.toEntity
import com.example.retrofitapp.model.toResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed class ApiState {
    object Loading : ApiState()
    data class Success(
        val data: Data,
        val isFromCache: Boolean = false,
        val hasMoreData: Boolean = false
    ) : ApiState()
    data class Error(
        val error: String,
        val data: Data? = null
    ) : ApiState()
}

class APIViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val characterDao = database.characterDao()
    private val api = APIInterface.create()
    
    private val _apiState = MutableLiveData<ApiState>()
    val apiState: LiveData<ApiState> = _apiState
    
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _selectedCharacter = MutableLiveData<Result?>(null)
    val selectedCharacter: LiveData<Result?> = _selectedCharacter

    private val _favorites = MutableLiveData<List<Result>>(emptyList())
    val favorites: LiveData<List<Result>> = _favorites

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    private var searchJob: Job? = null

    init {
        getCharacters()
        loadFavorites()
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value == query) return
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce para evitar muchas llamadas
            _searchQuery.value = query
            currentPage = 1
            isLastPage = false
            if (query.isNotEmpty()) {
                searchCharacters(query)
            } else {
                getCharacters()
            }
        }
    }

    fun setSelectedCharacter(character: Result?) {
        _selectedCharacter.value = character
    }

    fun toggleFavorite(character: Result) {
        viewModelScope.launch {
            try {
                val currentFavorites = _favorites.value.orEmpty().toMutableList()
                if (currentFavorites.any { it.id == character.id }) {
                    currentFavorites.removeAll { it.id == character.id }
                    characterDao.deleteCharacter(character.toEntity())
                } else {
                    currentFavorites.add(character)
                    characterDao.insertCharacter(character.toEntity())
                }
                _favorites.value = currentFavorites
            } catch (e: Exception) {
                handleError("Error al actualizar favoritos: ${e.message}")
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            characterDao.getFavoriteCharacters()
                .map { entities -> entities.map { it.toResult() } }
                .catch { e -> 
                    emit(emptyList())
                    handleError("Error al cargar favoritos: ${e.message}")
                }
                .collect { favorites ->
                    _favorites.value = favorites
                }
        }
    }

    fun getCharacters(loadMore: Boolean = false) {
        if (isLoading || (loadMore && isLastPage)) return
        
        viewModelScope.launch {
            try {
                isLoading = true
                if (!loadMore) {
                    _apiState.value = ApiState.Loading
                }

                // Cargar datos locales si es la primera página
                if (currentPage == 1) {
                    loadLocalData()
                }

                // Actualizar desde la API
                try {
                    val response = api.getCharacters(page = currentPage)
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        
                        if (currentPage == 1) {
                            characterDao.deleteAllCharacters()
                        }
                        characterDao.insertCharacters(data.results.map { it.toEntity() })

                        val currentData = if (loadMore && _apiState.value is ApiState.Success) {
                            val previousData = (_apiState.value as ApiState.Success).data
                            data.copy(results = previousData.results + data.results)
                        } else {
                            data
                        }

                        isLastPage = data.info.next == null
                        if (!isLastPage) currentPage++

                        _apiState.value = ApiState.Success(
                            data = currentData,
                            isFromCache = false,
                            hasMoreData = !isLastPage
                        )
                    } else {
                        handleApiError("Error en la respuesta: ${response.code()}")
                    }
                } catch (e: IOException) {
                    handleApiError("Error de conexión: ${e.message}")
                } catch (e: Exception) {
                    handleApiError(e.message ?: "Error desconocido")
                }
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun loadLocalData() {
        characterDao.getAllCharacters()
            .map { entities -> 
                Data(
                    info = Info(count = entities.size, pages = 1, null, null),
                    results = entities.map { it.toResult() }
                )
            }
            .catch { e ->
                emit(Data(Info(0, 0, null, null), emptyList()))
                handleError("Error al cargar datos locales: ${e.message}")
            }
            .collect { localData ->
                if (localData.results.isNotEmpty()) {
                    _apiState.value = ApiState.Success(
                        data = localData,
                        isFromCache = true,
                        hasMoreData = false
                    )
                }
            }
    }

    fun searchCharacters(query: String) {
        if (isLoading) return
        
        viewModelScope.launch {
            try {
                isLoading = true
                _apiState.value = ApiState.Loading

                // Búsqueda local
                val localResults = searchLocal(query)

                // Intentar búsqueda en la API
                try {
                    val response = api.getCharacters(name = query)
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        _apiState.value = ApiState.Success(
                            data = data,
                            isFromCache = false,
                            hasMoreData = false
                        )
                    } else {
                        if (localResults.results.isNotEmpty()) {
                            _apiState.value = ApiState.Success(
                                data = localResults,
                                isFromCache = true,
                                hasMoreData = false
                            )
                        } else {
                            handleApiError("No se encontraron resultados")
                        }
                    }
                } catch (e: Exception) {
                    if (localResults.results.isNotEmpty()) {
                        _apiState.value = ApiState.Success(
                            data = localResults,
                            isFromCache = true,
                            hasMoreData = false
                        )
                    } else {
                        handleApiError("Error en la búsqueda: ${e.message}")
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun searchLocal(query: String): Data {
        return characterDao.searchCharacters(query)
            .map { entities -> 
                Data(
                    info = Info(count = entities.size, pages = 1, null, null),
                    results = entities.map { it.toResult() }
                )
            }
            .catch { e ->
                emit(Data(Info(0, 0, null, null), emptyList()))
                handleError("Error en la búsqueda local: ${e.message}")
            }
            .first()
    }

    private fun handleApiError(message: String) {
        val currentState = _apiState.value
        if (currentState is ApiState.Success) {
            _apiState.value = ApiState.Error(
                error = message,
                data = currentState.data
            )
        } else {
            _apiState.value = ApiState.Error(message)
        }
    }

    private fun handleError(message: String) {
        val currentState = _apiState.value
        if (currentState !is ApiState.Success) {
            _apiState.value = ApiState.Error(message)
        }
    }
}