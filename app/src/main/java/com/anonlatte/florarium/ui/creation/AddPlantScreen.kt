package com.anonlatte.florarium.ui.creation

import android.app.AlertDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.anonlatte.florarium.R
import com.anonlatte.florarium.ui.theme.PlantCareAppTheme

@Composable
fun AddPlantScreen(
    onAddPlant: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    val context = LocalContext.current

    val photoUri = remember {
        val directory = context.cacheDir
        val file = java.io.File.createTempFile("plant_photo_", ".jpg", directory).apply {
            deleteOnExit()
        }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    var plantName by remember { mutableStateOf("") }
    var plantImageUri by remember { mutableStateOf<Uri?>(null) }

    // Example of one task (will be replaced with a list later)
    val wateringInterval = 7

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        plantImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            plantImageUri = photoUri
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 🌵 Photo + camera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.TopEnd
        ) {
            if (plantImageUri != null) {
                AsyncImage(
                    model = plantImageUri,
                    contentDescription = "Plant photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.flower_example),
                    contentDescription = "Placeholder image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(onClick = {
                val options = listOf("Choose from gallery", "Take a photo")
                AlertDialog.Builder(context).apply {
                    setTitle("Add photo")
                    setItems(options.toTypedArray()) { _, which ->
                        when (which) {
                            0 -> imagePickerLauncher.launch("image/*")
                            1 -> {
                                val permission = android.Manifest.permission.CAMERA
                                cameraPermissionLauncher.launch(permission)
                            }
                        }
                    }
                    show()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Add photo",
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                )
            }
        }

        // 🌱 Plant name
        OutlinedTextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Enter plant name") },
            modifier = Modifier.fillMaxWidth()
        )

        // 📆 Care schedule
        Text("Care schedule", style = MaterialTheme.typography.titleMedium)

        // 💧 Watering
        CareTaskRow(
            icon = Icons.Default.WaterDrop,
            taskName = "Watering",
            intervalText = "Every $wateringInterval days"
        )

        // ➕ Add task
        OutlinedButton(
            onClick = onAddTaskClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add care task")
        }

        Spacer(Modifier.weight(1f))

        // ✅ Add
        Button(
            onClick = onAddPlant,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Add to my plants")
        }
    }
}

@Composable
fun CareTaskRow(
    icon: ImageVector,
    taskName: String,
    intervalText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(taskName)
        }
        Text(intervalText, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun AddPlantScreenPreview() {
    PlantCareAppTheme {
        AddPlantScreen(
            onAddPlant = {},
            onAddTaskClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CareTaskRowPreview() {
    PlantCareAppTheme {
        CareTaskRow(
            icon = Icons.Default.WaterDrop,
            taskName = "Watering",
            intervalText = "Every 7 days"
        )
    }
}