package com.anonlatte.florarium.ui.creation

import android.net.Uri
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.domain.CareTask
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantCreationData
import com.anonlatte.florarium.ui.creation.viewmodel.AddPlantUiState
import com.anonlatte.florarium.ui.creation.viewmodel.AddPlantViewModel
import com.anonlatte.florarium.ui.theme.PlantCareAppTheme
import kotlinx.coroutines.launch

data class CareTaskUi(val icon: Painter, val name: String, val intervalDays: Int)

@Composable
fun AddPlantScreen(
    plantId: Long? = null,
    viewModel: AddPlantViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTaskType by remember { mutableStateOf<CareTaskType?>(null) }
    var intervalText by remember { mutableStateOf("") }

    // Получаем текущее состояние растения и задач
    val plant = when (uiState) {
        is AddPlantUiState.EditPlant -> (uiState as AddPlantUiState.EditPlant).plant
        is AddPlantUiState.NewPlant -> Plant()
        else -> Plant()
    }
    val careTasks = viewModel.currentCareTasks

    // Валидация
    var validationError by remember { mutableStateOf<String?>(null) }
    fun validateAndSave() {
        when {
            plant.name.isBlank() -> {
                validationError = "Plant name cannot be empty"
                scope.launch { snackbarHostState.showSnackbar(validationError!!) }
            }

            careTasks.isEmpty() -> {
                validationError = "Add at least one care task"
                scope.launch { snackbarHostState.showSnackbar(validationError!!) }
            }

            else -> {
                validationError = null
                viewModel.savePlant()
                onBack()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AddPlantScreenContent(
            plantData = PlantCreationData(plant, careTasks),
            plantImageUriState = if (plant.imageUri.isNotEmpty()) Uri.parse(plant.imageUri) else Uri.EMPTY,
            onImagePicked = { uri -> viewModel.updatePlantImage(uri ?: "") },
            onTakePhoto = { uri -> viewModel.updatePlantImage(uri) },
            onRequestCameraPermission = {},
            onPlantNameChange = { name -> viewModel.updatePlantName(name) },
            onAddTask = { showAddTaskDialog = true },
            onRemoveTask = { index -> viewModel.removeCareTask(index) },
            onAddPlantToGarden = { validateAndSave() },
            onAddPhoto = {},
            onBack = onBack
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    if (showAddTaskDialog) {
        AddCareTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAdd = { type, interval ->
                viewModel.addCareTask(type.toCareTask(interval))
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun AddPlantScreenContent(
    plantData: PlantCreationData,
    plantImageUriState: Uri,
    onImagePicked: (String?) -> Unit,
    onTakePhoto: (String) -> Unit,
    onRequestCameraPermission: (String) -> Unit,
    onPlantNameChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onRemoveTask: (Int) -> Unit,
    onAddPlantToGarden: () -> Unit,
    onAddPhoto: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
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
            if (plantImageUriState != Uri.EMPTY) {
                AsyncImage(
                    model = plantImageUriState,
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

            IconButton(onClick = onAddPhoto) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_camera_24),
                    contentDescription = "Add photo",
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                )
            }
        }

        // 🌱 Plant name
        OutlinedTextField(
            value = plantData.plant.name,
            onValueChange = onPlantNameChange,
            label = { Text("Enter plant name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        // 📆 Care schedule
        Text("Care schedule", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            plantData.careTasks.forEachIndexed { index, task ->
                val taskUi = task.toUi()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CareTaskRow(
                        icon = taskUi.icon,
                        taskName = task.name,
                        intervalText = "Every ${task.intervalDays} days"
                    )
                    IconButton(onClick = { onRemoveTask(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task"
                        )
                    }
                }
            }
        }

        // ➕ Add task
        OutlinedButton(
            onClick = { onAddTask() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add care task")
        }

        Spacer(Modifier.weight(1f))

        // ✅ Add
        Button(
            onClick = onAddPlantToGarden,
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
    icon: Painter,
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
                painter = icon,
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
        AddPlantScreenContent(
            plantData = PlantCreationData(
                plant = Plant(
                    id = 0,
                    name = "Plant",
                    imageUri = "",
                    createdAt = System.currentTimeMillis()
                ),
                careTasks = listOf(
                    CareTask.Watering("Watering", 7),
                    CareTask.Spraying("Spraying", 14),
                    CareTask.Fertilizing("Fertilizing", 30),
                    CareTask.Rotating("Rotating", 365)
                )
            ),
            plantImageUriState = Uri.EMPTY,
            onImagePicked = {},
            onTakePhoto = {},
            onRequestCameraPermission = {},
            onPlantNameChange = {},
            onAddTask = {},
            onRemoveTask = {},
            onAddPlantToGarden = {},
            onAddPhoto = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CareTaskRowPreview() {
    PlantCareAppTheme {
        CareTaskRow(
            icon = painterResource(id = R.drawable.ic_outline_drop_24),
            taskName = "Watering",
            intervalText = "Every 7 days"
        )
    }
}

@Composable
fun CareTask.toUi(): CareTaskUi {
    return when (this) {
        is CareTask.Watering -> CareTaskUi(
            painterResource(id = R.drawable.ic_outline_drop_24),
            name,
            intervalDays
        )

        is CareTask.Spraying -> CareTaskUi(
            painterResource(id = R.drawable.ic_outline_spray_24),
            name,
            intervalDays
        )

        is CareTask.Fertilizing -> CareTaskUi(
            painterResource(id = R.drawable.ic_outline_fertilizing_24),
            name,
            intervalDays
        )

        is CareTask.Rotating -> CareTaskUi(
            painterResource(id = R.drawable.ic_outline_rotate_right_24),
            name,
            intervalDays
        )
    }
}

enum class CareTaskType(val displayName: String) {
    WATERING("Watering"),
    SPRAYING("Spraying"),
    FERTILIZING("Fertilizing"),
    ROTATING("Rotating");

    fun toCareTask(interval: Int): CareTask = when (this) {
        WATERING -> CareTask.Watering("Watering", interval)
        SPRAYING -> CareTask.Spraying("Spraying", interval)
        FERTILIZING -> CareTask.Fertilizing("Fertilizing", interval)
        ROTATING -> CareTask.Rotating("Rotating", interval)
    }
}

@Composable
fun AddCareTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (CareTaskType, Int) -> Unit
) {
    var selectedType by remember { mutableStateOf(CareTaskType.WATERING) }
    var intervalText by remember { mutableStateOf("") }
    var intervalError by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Care Task") },
        text = {
            Column {
                // Тип задачи
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedType.displayName)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    CareTaskType.values().forEach { type ->
                        DropdownMenuItem(text = { Text(type.displayName) }, onClick = {
                            selectedType = type
                            expanded = false
                        })
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Интервал
                OutlinedTextField(
                    value = intervalText,
                    onValueChange = {
                        intervalText = it
                        intervalError = false
                    },
                    label = { Text("Interval (days)") },
                    isError = intervalError
                )
                if (intervalError) {
                    Text("Enter a valid interval", color = Color.Red)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val interval = intervalText.toIntOrNull()
                if (interval == null || interval <= 0) {
                    intervalError = true
                } else {
                    onAdd(selectedType, interval)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}