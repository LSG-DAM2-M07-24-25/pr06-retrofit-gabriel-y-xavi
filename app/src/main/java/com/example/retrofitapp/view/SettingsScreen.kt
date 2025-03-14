package com.example.retrofitapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Aquí irían las opciones de configuración
            Text(
                text = "Configuración de la aplicación",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ejemplo de opciones
            SettingsOption(
                title = "Tema oscuro",
                description = "Activar/desactivar el tema oscuro"
            )
            
            SettingsOption(
                title = "Notificaciones",
                description = "Configurar las notificaciones"
            )
            
            SettingsOption(
                title = "Caché",
                description = "Limpiar datos almacenados"
            )
        }
    }
}

@Composable
private fun SettingsOption(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 