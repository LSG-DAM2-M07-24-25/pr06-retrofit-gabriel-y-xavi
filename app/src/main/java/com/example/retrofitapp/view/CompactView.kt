package com.example.retrofitapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun CompactView(
    viewModel: APIViewModel,
    onCharacterClick: (Result) -> Unit
) {
    val state by viewModel.apiState.observeAsState(ApiState.Loading)
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val favorites by viewModel.favorites.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            when (state) {
                is ApiState.Loading -> {
                    LoadingScreen()
                }
                is ApiState.Success -> {
                    val data = (state as ApiState.Success).data
                    val filteredResults = if (searchQuery.isEmpty()) {
                        data.results
                    } else {
                        data.results.filter { character ->
                            character.name.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredResults.isEmpty()) {
                        EmptyResultsScreen(searchQuery)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(filteredResults) { _, character ->
                                CharacterItem(
                                    character = character,
                                    onCharacterClick = onCharacterClick,
                                    isFavorite = favorites.any { it.id == character.id },
                                    onFavoriteClick = { viewModel.toggleFavorite(character) }
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
} 