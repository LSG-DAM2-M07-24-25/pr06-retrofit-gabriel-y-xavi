package com.example.retrofitapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.retrofitapp.model.Result

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterDetailPanel(
    character: Result,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = character.image,
                contentDescription = "Character Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = character.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailInfoRow("Status", character.status)
            DetailInfoRow("Species", character.species)
            DetailInfoRow("Gender", character.gender)
            DetailInfoRow("Origin", character.origin.name)
            DetailInfoRow("Location", character.location.name)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
} 