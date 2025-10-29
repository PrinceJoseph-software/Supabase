package com.example.supabase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NoteList(controller: Controller, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var noteText by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<Entity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        // Main box containing everything
        Box(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Box for notes list - limited to 5 items height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(controller.note) { note1 ->
                            NoteItem(
                                entity = note1,
                                onEdit = {
                                    editingNote = note1
                                    showEditDialog = true
                                },
                                onDelete = {
                                    scope.launch {
                                        note1.id?.let { controller.deleteData(it) }
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Input section
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Enter a new note") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            controller.insertData(noteText)
                            noteText = ""
                        }
                    }
                ) {
                    Text("Add Note")
                }
            }
        }
    }

    // Edit Dialog
    if (showEditDialog && editingNote != null) {
        EditNoteDialog(
            note = editingNote!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedText ->
                scope.launch {
                    editingNote?.id?.let { controller.updateData(it, updatedText) }
                    showEditDialog = false
                }
            }
        )
    }
}

@Composable
fun NoteItem(
    entity: Entity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entity.note,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EditNoteDialog(
    note: Entity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editText by remember { mutableStateOf(note.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Note") },
        text = {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onSave(editText) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}