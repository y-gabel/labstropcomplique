package com.example.tasklist2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.mytasklist.data.Task
import com.example.mytasklist.data.TaskList
import com.example.tasklist2.ui.theme.TaskList2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskList2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskListScreen()
                }
            }
        }
    }
}

@Composable
fun TaskListScreen(modifier: Modifier = Modifier) {
    val taskList = remember { TaskList() }
    var tasks by remember { mutableStateOf(taskList.getTasks().toMutableStateList()) }



    Column(modifier = modifier.padding(16.dp)) {
        var title by remember { mutableStateOf(TextFieldValue()) }
        var description by remember { mutableStateOf(TextFieldValue()) }
        var showError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it; showError = false },
            label = { Text("Title") },
            isError = showError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Button(
            onClick = {
                if (title.text.isBlank() || description.text.isBlank() || taskList.doesTaskExist(title.text)) {
                    showError = true
                } else {
                    taskList.addTask(Task(title.text, description.text))
                    tasks.clear()
                    tasks.addAll(taskList.getTasks())
                    title = TextFieldValue() // Reset title
                    description = TextFieldValue() // Reset description
                    showError = false
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Task")
        }

        if (showError) {
            Text(
                "Please enter a unique title and a description.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            FilterButton("Show Active") {
                tasks.clear()
                tasks.addAll(taskList.getTasks(filterDone = false))
            }
            FilterButton("Show Completed") {
                tasks.clear()
                tasks.addAll(taskList.getTasks(filterDone = true))
            }
            FilterButton("Show All") {
                tasks.clear()
                tasks.addAll(taskList.getTasks())
            }
        }

        tasks.forEach { task ->
            TaskItem(
                task = task,
                onDone = { updatedTask, isChecked ->
                    taskList.markTaskDone(updatedTask.title, isChecked)
                    // Mettre à jour la liste de manière à ce que Compose détecte le changement
                    tasks.clear()
                    tasks.addAll(taskList.getTasks())
                },

                onDelete = { taskToDelete ->
                    taskList.removeTask(taskToDelete.title)
                    // Mettre à jour la liste de manière à ce que Compose détecte le changement
                    tasks.clear()
                    tasks.addAll(taskList.getTasks())
                }
            )
        }
    }
}


@Composable
fun FilterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDone: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { isChecked ->
                    onDone(task, isChecked)
                }
            )
            Text("${task.title} - ${task.description}", style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = { onDelete(task) }) {
            Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
        }
    }
}

