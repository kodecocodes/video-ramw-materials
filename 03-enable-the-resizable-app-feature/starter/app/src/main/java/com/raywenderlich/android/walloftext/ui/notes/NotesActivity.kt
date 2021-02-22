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
package com.raywenderlich.android.walloftext.ui.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.walloftext.App
import com.raywenderlich.android.walloftext.data.model.Note
import com.raywenderlich.android.walloftext.databinding.ActivityNotesBinding
import com.raywenderlich.android.walloftext.ui.addNote.AddNoteActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {

  private val binding by lazy { ActivityNotesBinding.inflate(layoutInflater) }
  private val adapter by lazy { NoteAdapter(::showNoteDetails) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    initUi()
    loadData()
  }

  private fun initUi() {
    binding.addNote.setOnClickListener { showNoteDetails() }
    binding.notes.layoutManager = LinearLayoutManager(this)
    binding.notes.adapter = adapter
  }

  private fun loadData() {
    lifecycleScope.launch {
      App.notesDao.getNotes()
        .catch { emit(emptyList()) }
        .collect { adapter.setData(it) }
    }
  }

  private fun showNoteDetails(note: Note? = null) {
    startActivity(AddNoteActivity.getIntent(this, note))
  }
}
