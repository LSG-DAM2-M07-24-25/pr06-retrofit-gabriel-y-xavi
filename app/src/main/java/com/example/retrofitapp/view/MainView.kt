package com.example.retrofitapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.retrofitapp.model.Data
import com.example.retrofitapp.viewmodel.APIViewModel

@Composable
fun MyRecyclerView(myViewModel: APIViewModel) {
    val showLoading: Boolean by myViewModel.loading.observeAsState(true)
    val characters: Data by myViewModel.characters.observeAsState(Data(emptyList(), 0, 0, 0))
    myViewModel.getCharacters()
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
        LazyColumn {
            items(characters.characters) {
                CharacterItem(character = it)
            }

        }
    }
}
