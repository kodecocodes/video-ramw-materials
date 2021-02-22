/*
Copyright 2021 Razeware LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
package com.raywenderlich.android.walloftext.ui.addNote

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.raywenderlich.android.walloftext.App
import com.raywenderlich.android.walloftext.R
import com.raywenderlich.android.walloftext.data.model.Note
import com.raywenderlich.android.walloftext.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

private const val KEY_NOTE = "notes"

class AddNoteActivity : AppCompatActivity() {

  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
  private val dragListener by lazy { buildDragListener() }

  private var currentNote: Note? = null
  private var wasInEditMode: Boolean = false

  companion object {
    fun getIntent(context: Context, shouldLaunchInMultiWindow: Boolean, note: Note? = null) =
      Intent(context, AddNoteActivity::class.java).apply {
        putExtra(KEY_NOTE, note)

        if (shouldLaunchInMultiWindow) {
          flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK
        }
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    initUi()
    loadData()
    enableDragListener()
  }

  private fun initUi() {
    with(binding) {
      editSwitch.setOnCheckedChangeListener { _, isChecked ->
        notes.visibility = if (isChecked) View.GONE else View.VISIBLE
        notesInput.visibility = if (isChecked) View.VISIBLE else View.GONE

        if (!isChecked) {
          val currentNote = notesInput.text.toString()

          updateNote(currentNote)
          notes.text = currentNote

          hideKeyboard()
        } else {
          wasInEditMode = true
        }
      }
    }
  }

  private fun enableDragListener() {
    binding.notes.setOnDragListener(dragListener)
    binding.notes.setOnLongClickListener(buildLongClickListener())
  }

  private fun buildLongClickListener() = View.OnLongClickListener {
    val data = ClipData.Item(binding.notes.text.toString())
    val dragData = ClipData("Notes Data", arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), data)

    it.startDragAndDrop(
      dragData,
      View.DragShadowBuilder(binding.notes),
      null,
      View.DRAG_FLAG_GLOBAL
    )

    true
  }

  private fun buildDragListener(): View.OnDragListener = View.OnDragListener { _, event ->
    when (event.action) {
      DragEvent.ACTION_DRAG_STARTED -> {
        binding.notes.setTextColor(getColor(R.color.purple_200))
      }

      DragEvent.ACTION_DRAG_ENDED -> {
        binding.notes.setTextColor(getColor(R.color.teal_700))
      }
    }

    true
  }

  private fun updateNote(noteText: String) {
    lifecycleScope.launch {
      val noteData = currentNote

      val updatedNote = if (noteData != null && noteData.id.isNotEmpty()) {
        noteData.copy(text = noteText)
      } else {
        Note(text = noteText)
      }

      App.notesDao.saveNote(updatedNote)
      currentNote = updatedNote
    }
  }

  private fun loadData() {
    currentNote = intent.getSerializableExtra(KEY_NOTE) as? Note
    val note = currentNote

    if (note != null) {
      binding.notesInput.setText(note.text)
      binding.notes.text = note.text
    } else {
      binding.editSwitch.isChecked = true
    }
  }

  private fun hideKeyboard() {
    val inputMethodManager = getSystemService(InputMethodManager::class.java) ?: return

    inputMethodManager.hideSoftInputFromWindow(binding.notesInput.windowToken, 0)
  }

  override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
    super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)

    if (isInMultiWindowMode) {
      binding.editSwitch.isChecked = false
    } else if (!isInMultiWindowMode && wasInEditMode) {
      binding.editSwitch.isChecked = true

      wasInEditMode = false
    }
  }
}
