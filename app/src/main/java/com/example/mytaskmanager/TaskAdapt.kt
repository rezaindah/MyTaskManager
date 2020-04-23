package com.example.mytaskmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class TaskAdapt(
    private val onClickCallback: (Task) -> Unit,
    recyclerOptions: FirebaseRecyclerOptions<Task>
) : FirebaseRecyclerAdapter<Task, TaskVHold>(recyclerOptions) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVHold {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return TaskVHold(view)
    }

    override fun onBindViewHolder(viewHolder: TaskVHold, position: Int, tasked: Task) {
        viewHolder.bind(tasked, onClickCallback)
    }

    fun getTodoAt(position: Int): Task = getItem(position)

}
