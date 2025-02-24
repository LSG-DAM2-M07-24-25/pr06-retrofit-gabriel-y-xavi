package com.example.retrofitapp.view

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retrofitapp.model.Character
import com.example.retrofitapp.model.Result
import com.example.retrofitapp.model.Data
import com.example.retrofitapp.viewmodel.APIViewModel

@Composable
fun MyRecyclerView(myViewModel: APIViewModel) {
    val showLoading: Boolean by myViewModel.loading.observeAsState(true)
    val characters: Data? by myViewModel.characters.observeAsState(null)
    val activity = LocalContext.current as Activity
    
    LaunchedEffect(Unit) {
        myViewModel.getCharacters()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Botón de cierre en la parte superior
        Button(
            onClick = { activity.finish() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Cerrar App")
        }

        if (showLoading) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            characters?.let { data ->
                data.results?.let { characterList ->
                    if (characterList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.padding(top = 64.dp)
                        ) {
                            items(characterList) { character ->
                                CharacterItem(character = character)
                            }
                        }
                    } else {
                        // Mostrar mensaje cuando la lista está vacía
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron personajes")
                        }
                    }
                }
            }
        }
    }
}
