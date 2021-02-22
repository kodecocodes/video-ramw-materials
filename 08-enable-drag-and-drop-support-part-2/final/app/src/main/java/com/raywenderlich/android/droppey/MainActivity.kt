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
package com.raywenderlich.android.droppey

import android.os.Bundle
import android.view.DragEvent.ACTION_DROP
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    enableDropListener()
  }

  private fun enableDropListener() {
    val notesDrop = findViewById<TextView>(R.id.notesDrop)

    notesDrop.setOnDragListener { _, event ->
      if (event.action == ACTION_DROP) {
        val notes = event.clipData.getItemAt(0).text

        notesDrop.text = notes
      }

      true
    }
  }
}
