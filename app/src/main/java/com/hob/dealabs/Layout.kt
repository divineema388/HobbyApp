package com.hobby.dealabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobbyApp(viewModel: HobbyViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Hobbies") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Hobby")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Add Hobby") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.hobbies) { hobby ->
                HobbyCard(hobby = hobby, viewModel = viewModel)
            }
        }
    }
    
    if (showAddDialog) {
        AddHobbyDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category ->
                viewModel.addHobby(name, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HobbyCard(hobby: Hobby, viewModel: HobbyViewModel) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hobby.isActive) 
                MaterialTheme.colorScheme.surfaceVariant else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hobby.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = hobby.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Level: ${hobby.level}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${hobby.timeSpent / 60}h ${hobby.timeSpent % 60}m spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                IconButton(
                    onClick = { viewModel.toggleActive(hobby.id) }
                ) {
                    Icon(
                        if (hobby.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (hobby.isActive) "Pause" else "Resume"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        viewModel.updateTimeSpent(hobby.id, 30)
                        NotificationHelper.showProgressNotification(context, hobby.name, 30)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+30min")
                }
                
                Button(
                    onClick = { 
                        viewModel.updateTimeSpent(hobby.id, 60)
                        NotificationHelper.showProgressNotification(context, hobby.name, 60)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1hr")
                }
                
                IconButton(
                    onClick = { viewModel.deleteHobby(hobby.id) }
                ) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddHobbyDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val categories = listOf("Creative", "Sports", "Music", "Technology", "Outdoor", "Indoor", "Learning", "Crafts")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Hobby") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hobby Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank() && category.isNotBlank()) onAdd(name, category) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}