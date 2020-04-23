package com.example.mytaskmanager

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Parcelize
data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var dueDateTimestamp: Long = ZonedDateTime.now().toEpochSecond(),
    var taskStatus: TaskStatus = TaskStatus.NOT_DONE,
    var taskType: TaskType = TaskType.TASK
) : Parcelable {

    fun dueDate(): LocalDateTime = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(dueDateTimestamp),
        ZoneId.systemDefault()
    ).toLocalDateTime()

    fun dueDate(value: LocalDateTime) {
        dueDateTimestamp = ZonedDateTime.of(value, ZoneId.systemDefault()).toEpochSecond()
    }

    fun changeStatus() {
        taskStatus = when (taskStatus) {
            TaskStatus.NOT_DONE -> TaskStatus.DONE
            TaskStatus.DONE -> TaskStatus.NOT_DONE
        }
    }

}
