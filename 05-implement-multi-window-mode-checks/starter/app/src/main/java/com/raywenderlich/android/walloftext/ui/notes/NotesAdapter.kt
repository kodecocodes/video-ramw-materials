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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.walloftext.data.model.Note
import com.raywenderlich.android.walloftext.databinding.ItemNoteBinding

class NoteAdapter(
  private val onItemClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteViewHolder>() {

  private val items = mutableListOf<Note>()

  override fun getItemCount(): Int = items.size

  fun setData(data: List<Note>) {
    this.items.clear()
    this.items.addAll(data)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
    return NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClick)
  }

  override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
    holder.bindData(items[position])
  }
}
