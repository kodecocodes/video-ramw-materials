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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.raywenderlich.android.walloftext.App
import com.raywenderlich.android.walloftext.data.model.Note
import com.raywenderlich.android.walloftext.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

private const val KEY_NOTE = "notes"

class AddNoteActivity : AppCompatActivity() {

  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

  private var currentNote: Note? = null

  companion object {
    fun getIntent(context: Context, note: Note? = null) =
      Intent(context, AddNoteActivity::class.java).apply {
        putExtra(KEY_NOTE, note)
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    initUi()
    loadData()
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
        }
      }
    }
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
}
