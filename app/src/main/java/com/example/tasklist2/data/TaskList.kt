package com.example.mytasklist.data

class TaskList {
    private val tasks = mutableListOf<Task>()

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun markTaskDone(taskTitle: String, isDone: Boolean) {
        tasks.find { it.title == taskTitle }?.let {
            it.isDone = isDone
        }
    }

    fun removeTask(taskTitle: String) {
        tasks.removeIf { it.title == taskTitle }
    }

    fun getTasks(filterDone: Boolean? = null): List<Task> {
        return when (filterDone) {
            true -> tasks.filter { it.isDone }
            false -> tasks.filter { !it.isDone }
            else -> tasks
        }
    }

    fun doesTaskExist(title: String): Boolean {
        return tasks.any { it.title == title }
    }

}