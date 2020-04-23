package com.example.mytaskmanager

import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


const val PATH = "doitem"
const val TITLE = "title"
const val DESCRIPTION = "description"
const val TASK_TYPE = "taskType"
const val TASK_STATUS = "taskStatus"
const val DUE_DATE_TIMESTAMP = "dueDateTimestamp"

class DBhelp {
    private val doitemRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(PATH)

    init {
        doitemRef.keepSynced(true)
    }

    fun saveTodo(tasked: Task) {
        if (tasked.id == "") createTodo(tasked)
        else updateTodo(tasked)
    }

    private fun createTodo(tasked: Task) {
        tasked.id = doitemRef.push().key!!
        doitemRef.child(tasked.id).setValue(tasked)
    }

    private fun updateTodo(tasked: Task) {
        doitemRef.child(tasked.id).apply {
            child(TITLE).setValue(tasked.title)
            child(DESCRIPTION).setValue(tasked.description)
            child(DUE_DATE_TIMESTAMP).setValue(tasked.dueDateTimestamp)
            child(TASK_TYPE).setValue(tasked.taskType)
            child(TASK_STATUS).setValue(tasked.taskStatus)
        }
    }

    fun deleteTodo(tasked: Task) {
        doitemRef.child(tasked.id).setValue(null)
    }

    fun getRecyclerOptions(owner: LifecycleOwner) = FirebaseRecyclerOptions.Builder<Task>()
        .setQuery(doitemRef.orderByKey(), Task::class.java)
        .setLifecycleOwner(owner)
        .build()

}