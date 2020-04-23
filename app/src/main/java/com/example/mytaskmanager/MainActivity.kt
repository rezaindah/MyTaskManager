package com.example.mytaskmanager

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val DO_KEY = "DO_KEY"
    }
    private val dbHelper = DBhelp()
    private var activeTodo: Task? = null

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val recyclerOptions = dbHelper.getRecyclerOptions(this)
        val adapter = TaskAdapt(this::startAddEditActivity, recyclerOptions)

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            private val background: ColorDrawable = ColorDrawable()

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView

                when {
                    dX > 0 -> { // Swiping to the right
                        background.apply {
                            color = Color.GRAY
                            setBounds(
                                itemView.left,
                                itemView.top,
                                itemView.left + dX.toInt() + itemView.paddingLeft,
                                itemView.bottom
                            )
                        }
                    }
                    dX < 0 -> { // Swiping to the left
                        if (recyclerView.itemAnimator?.isRunning == false) {
                            val tasked = adapter.getTodoAt(viewHolder.adapterPosition)

                            val currentColor = when (tasked.taskStatus) {
                                TaskStatus.DONE -> Color.TRANSPARENT
                                TaskStatus.NOT_DONE -> Color.BLUE
                            }

                            background.apply {
                                color = currentColor
                                setBounds(
                                    itemView.right + dX.toInt() - itemView.paddingRight,
                                    itemView.top,
                                    itemView.right,
                                    itemView.bottom
                                )
                            }
                        }
                    }
                    else -> { // view is unSwiped
                        background.setBounds(0, 0, 0, 0)
                    }
                }

                if (dX != 0.0F) {
                    background.draw(canvas)
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val tasked = adapter.getTodoAt(viewHolder.adapterPosition)
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        tasked.changeStatus()
                        dbHelper.saveTodo(tasked)
                        adapter.notifyDataSetChanged()
                    }
                    ItemTouchHelper.RIGHT -> {
                        dbHelper.deleteTodo(tasked)
                        Snackbar.make(
                            mainlayout,
                            "Deleted",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(recyclerView)

        add_button.setOnClickListener { this.startAddEditActivity(Task()) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putParcelable(DO_KEY, activeTodo)
        })
    }

    private fun startAddEditActivity(tasked: Task) {
        val intent = Intent(this, TaskDetilActivity::class.java)
        intent.putExtra("TASK", tasked)
        startActivity(intent)
    }
}

