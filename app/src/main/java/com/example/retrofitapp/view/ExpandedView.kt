package com.example.retrofitapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.viewmodel.APIViewModel
import com.example.retrofitapp.viewmodel.ApiState

@Composable
fun ExpandedView(
    viewModel: APIViewModel,
    onCharacterClick: (Result) -> Unit
) {
    val state by viewModel.apiState.observeAsState(ApiState.Loading)
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val selectedCharacter by viewModel.selectedCharacter.observeAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        // Lista de personajes (2/3 de la pantalla)
        Box(modifier = Modifier.weight(2f)) {
            Column {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                )

                when (state) {
                    is ApiState.Loading -> {
                        LoadingScreen()
                    }
                    is ApiState.Success -> {
                        val data = (state as ApiState.Success).data
                        val filteredResults = data.results.filter { character ->
                            character.name.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredResults.isEmpty() && searchQuery.isNotEmpty()) {
                            EmptyResultsScreen()
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(filteredResults) { _, character ->
                                    CharacterItem(
                                        character = character,
                                        onCharacterClick = onCharacterClick
                                    )
                                }
                            }
                        }
                    }
                    is ApiState.Error -> {
                        ErrorScreen(error = (state as ApiState.Error).error)
                    }
                }
            }
        }

        // Panel de detalles (1/3 de la pantalla)
        Box(modifier = Modifier.weight(1f)) {
            selectedCharacter?.let { character ->
                CharacterDetailPanel(
                    character = character,
                    onClose = { viewModel.setSelectedCharacter(null) }
                )
            }
        }
    }
} 