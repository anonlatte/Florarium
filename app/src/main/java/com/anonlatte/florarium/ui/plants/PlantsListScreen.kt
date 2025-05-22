package com.anonlatte.florarium.ui.plants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlantsListScreen(
    onAddPlant: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Список растений")
        Button(
            onClick = onAddPlant,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Добавить растение")
        }
    }
} 