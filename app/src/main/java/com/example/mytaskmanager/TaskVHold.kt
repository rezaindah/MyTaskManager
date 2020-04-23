package com.example.mytaskmanager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskVHold(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    fun bind(tasked: Task, onClickCallback: (Task) -> Unit) {
        containerView.tag = tasked.id
        containerView.setOnClickListener { onClickCallback(tasked) }

        title.text = tasked.title
        description.text = tasked.description
        dueDate.text = dateTimeFormatter.format(tasked.dueDate())
        imageView.setImageDrawable(containerView.context.getDrawable(getIconId(tasked.taskType)))
        isDone.visibility = getVisibility(tasked.taskStatus)
    }

    private fun getVisibility(taskStatus: TaskStatus): Int {
        return when (taskStatus) {
            TaskStatus.DONE -> View.VISIBLE
            TaskStatus.NOT_DONE -> View.INVISIBLE
        }
    }

    private fun getIconId(taskType: TaskType): Int {
        return when (taskType) {
            TaskType.TASK -> R.drawable.ic_todo
            TaskType.PHONECALL -> R.drawable.ic_phone
            TaskType.MEETING -> R.drawable.ic_meeting
            TaskType.EMAIL -> R.drawable.ic_email
        }
    }
}
