package com.mathmaster.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mathmaster.app.data.db.ProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    viewModel: ProfileSelectionViewModel,
    onProfileSelected: (ProfileEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileSelectionContent(
        uiState = uiState,
        onProfileClick = onProfileSelected,
        onDeleteProfile = viewModel::deleteProfile,
        onCreateProfileClick = viewModel::showCreateDialog,
        onDismissDialog = viewModel::hideCreateDialog,
        onCreateProfile = viewModel::createProfile
    )
}

@Composable
private fun ProfileSelectionContent(
    uiState: ProfileSelectionUiState,
    onProfileClick: (ProfileEntity) -> Unit,
    onDeleteProfile: (ProfileEntity) -> Unit,
    onCreateProfileClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onCreateProfile: (String, (Long) -> Unit) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Select Profile") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateProfileClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Profile")
            }
        }
    ) { padding ->
        if (uiState.profiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No profiles yet. Create one to get started!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.profiles,
                    key = { it.id }
                ) { profile ->
                    ProfileItem(
                        profile = profile,
                        onClick = { onProfileClick(profile) },
                        onDelete = { onDeleteProfile(profile) }
                    )
                }
            }
        }

        if (uiState.showCreateDialog) {
            CreateProfileDialog(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onDismiss = onDismissDialog,
                onCreate = onCreateProfile
            )
        }
    }
}

@Composable
private fun ProfileItem(
    profile: ProfileEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initials avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.initials,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete ${profile.name}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CreateProfileDialog(
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onCreate: (String, (Long) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Create New Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    enabled = !isLoading,
                    singleLine = true,
                    isError = error != null
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(name) {
                        name = ""
                    }
                },
                enabled = !isLoading && name.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}
