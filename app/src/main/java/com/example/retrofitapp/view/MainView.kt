package com.example.retrofitapp.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.viewmodel.APIViewModel
import com.example.retrofitapp.viewmodel.ApiState
import androidx.compose.material3.windowsizeclass.*

@Composable
fun MyRecyclerView(
    myViewModel: APIViewModel,
    windowSizeClass: WindowSizeClass
) {
    val state by myViewModel.apiState.observeAsState(ApiState.Loading)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCharacter by remember { mutableStateOf<Result?>(null) }
    val focusManager = LocalFocusManager.current
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Determine the number of columns based on window size
    val columns = when {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded -> 3
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium -> 2
        else -> 1
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A11CB),
                        Color(0xFF2575FC)
                    )
                )
            )
    ) {
        Column {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .background(
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    ),
                placeholder = {
                    Text(
                        "Buscar personaje...",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true
            )

            when (state) {
                is ApiState.Loading -> {
                    LoadingScreen(scale)
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
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(filteredResults) { index, character ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInVertically(
                                        initialOffsetY = { it * (index + 1) },
                                        animationSpec = tween(600)
                                    ) + fadeIn(animationSpec = tween(600))
                                ) {
                                    CharacterItem(
                                        character = character,
                                        onCharacterClick = { selectedCharacter = it }
                                    )
                                }
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
    
    // Show character dialog when a character is selected
    selectedCharacter?.let { character ->
        CharacterDialog(
            character = character,
            onDismiss = { selectedCharacter = null }
        )
    }
}

@Composable
private fun LoadingScreen(scale: Float) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF6B6B),
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .padding(16.dp)
            )
            Text(
                text = "Cargando...",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun EmptyResultsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No se encontraron resultados",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorScreen(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF4B2B),
                            Color(0xFFFF416C)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "¡Ups! Algo salió mal",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
