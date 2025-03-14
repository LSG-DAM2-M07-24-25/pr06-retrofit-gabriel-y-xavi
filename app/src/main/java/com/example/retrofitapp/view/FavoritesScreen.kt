package com.example.retrofitapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.viewmodel.APIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: APIViewModel,
    onCharacterClick: (Result) -> Unit
) {
    val favorites by viewModel.favorites.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (favorites.isEmpty()) {
                EmptyFavoritesMessage()
            } else {
                FavoritesList(
                    favorites = favorites,
                    onCharacterClick = onCharacterClick,
                    onFavoriteClick = { character -> viewModel.toggleFavorite(character) }
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No tienes personajes favoritos")
    }
}

@Composable
private fun FavoritesList(
    favorites: List<Result>,
    onCharacterClick: (Result) -> Unit,
    onFavoriteClick: (Result) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(favorites) { character ->
            CharacterItem(
                character = character,
                onCharacterClick = onCharacterClick,
                isFavorite = true, // Siempre true ya que estamos en la pantalla de favoritos
                onFavoriteClick = { onFavoriteClick(character) }
            )
        }
    }
} 