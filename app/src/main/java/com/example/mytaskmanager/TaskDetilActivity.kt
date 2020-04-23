package com.example.mytaskmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_task_detil.*
//import kotlinx.android.synthetic.main.content_add_edit_task.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskDetilActivity : AppCompatActivity() {

    private lateinit var tasked: Task
    private val dbHelper = DBhelp()
    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detil)
        setSupportActionBar(toolbar)

        add_button.setOnClickListener {
            updateTodo()
            dbHelper.saveTodo(tasked)
            finish()
        }

        et_deadline.setOnFocusChangeListener { view, hasFocus ->
            val dueDate = tasked.dueDate()
            val onDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val onTimeSetListener =
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            tasked.dueDate(
                                LocalDateTime.of(
                                    LocalDate.of(year, month + 1, dayOfMonth),
                                    LocalTime.of(hourOfDay, minute)
                                )
                            )

                            et_deadline.setText(dateTimeFormatter.format(dueDate))
                        }
                    TimePickerDialog(
                        this,
                        onTimeSetListener,
                        dueDate.hour,
                        dueDate.minute,
                        DateFormat.is24HourFormat(this)
                    ).show()
                }

            if (hasFocus) {
                hideKeyboard(view)
                DatePickerDialog(
                    this,
                    onDateSetListener,
                    dueDate.year,
                    dueDate.monthValue - 1,
                    dueDate.dayOfMonth
                ).show()
                view.clearFocus()
            }
        }

        spinner_category.adapter = ArrayAdapter.createFromResource(
            this, R.array.task_types, android.R.layout.simple_spinner_item
        )
            .also { arrayAdapter -> arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tasked.taskType = TaskTypeConverter.toTaskType(position)
            }

        }

        setData()
    }

    private fun setData() {
        tasked = intent.getParcelableExtra("TASK") ?: throw IllegalStateException()
        et_title.setText(tasked.title)
        et_descr.setText(tasked.description)
        et_deadline.setText(dateTimeFormatter.format(tasked.dueDate()))
        spinner_category.setSelection(TaskTypeConverter.fromTaskType(tasked.taskType), true)
    }

    private fun updateTodo() {
        tasked.apply {
            title = et_title.text.toString()
            description = et_descr.text.toString()
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    object TaskTypeConverter {
        fun fromTaskType(taskType: TaskType): Int = when (taskType) {
            TaskType.PHONECALL -> 0
            TaskType.MEETING -> 1
            TaskType.TASK -> 2
            TaskType.EMAIL -> 3
        }

        fun toTaskType(intValue: Int) = when (intValue) {
            0 -> TaskType.PHONECALL
            1 -> TaskType.MEETING
            2 -> TaskType.TASK
            3 -> TaskType.EMAIL
            else -> throw IllegalStateException()
        }
    }

}
