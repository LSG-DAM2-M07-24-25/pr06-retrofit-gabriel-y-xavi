package com.example.retrofitapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.retrofitapp.viewmodel.APIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: APIViewModel,
    onBackClick: () -> Unit
) {
    val selectedCharacter by viewModel.selectedCharacter.observeAsState()

    LaunchedEffect(characterId) {
        // Aquí podrías cargar los detalles del personaje si necesitas más información
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Personaje") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        selectedCharacter?.let { character ->
            CharacterDetailPanel(
                character = character,
                onClose = onBackClick
            )
        }
    }
} 