package com.sahilpvns.todoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sahilpvns.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")
    private lateinit var adapter: NoteAdapter

    private var noteTitleEditText: EditText? = null
    private var noteContentEditText: EditText? = null
    private var notesRecyclerView: RecyclerView? = null
    private var addNoteButton: Button? = null
    private var binding: ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        addNoteButton = findViewById(R.id.addNoteButton)
        noteTitleEditText = findViewById(R.id.noteTitleEditText)
        noteContentEditText = findViewById(R.id.noteContentEditText)

        val sms = findViewById<Button>(R.id.btnSMS)
        sms.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
        }



        adapter = NoteAdapter(emptyList(), { note -> updateNoteUI(note) }, { note -> deleteNoteFromFirestore(note) })
        notesRecyclerView?.layoutManager = LinearLayoutManager(this)
        notesRecyclerView?.adapter = adapter



        addNoteButton?.setOnClickListener {
            if (TextUtils.isEmpty(noteTitleEditText?.text)) {
                noteTitleEditText?.error = "Enter title"
                Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
            } else {
                addNoteToFirestore(noteTitleEditText?.text.toString(), noteContentEditText?.text.toString())
            }

        }

        loadNotesFromFirestore()
    }

    private fun addNoteToFirestore(title: String, content: String) {
        val note = hashMapOf(
            "title" to title,
            "content" to content
        )
        notesCollection.add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                noteTitleEditText?.text?.clear()
                noteContentEditText?.text?.clear()
                loadNotesFromFirestore()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, " $e Error adding note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNoteInFirestore(note: Note) {
        val noteMap = hashMapOf(
            "title" to note.title,
            "content" to note.content
        )

        notesCollection.document(note.id)
            .set(noteMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                loadNotesFromFirestore()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, " $e Error updating note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNoteFromFirestore(note: Note) {
        notesCollection.document(note.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                loadNotesFromFirestore()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "$e Error deleting note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotesFromFirestore() {
        notesCollection.get()
            .addOnSuccessListener {
                val notesList = mutableListOf<Note>()
                for (document in it) {
                    val id = document.id
                    val title = document.getString("title") ?: ""
                    val content = document.getString("content") ?: ""
                    notesList.add(Note(id, title, content))
                }
                adapter.updateData(notesList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, " $exception Error getting documents: ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNoteUI(note: Note) {
        noteTitleEditText?.setText(note.title)
        noteContentEditText?.setText(note.content)
        addNoteButton?.text = String.format("Update Note")
        addNoteButton?.setOnClickListener {
            note.title = noteTitleEditText?.text.toString()
            note.content = noteContentEditText?.text.toString()
            updateNoteInFirestore(note)
            addNoteButton?.text =String.format("Add Note")
            addNoteButton?.setOnClickListener {
                val title = noteTitleEditText?.text.toString()
                val content = noteContentEditText?.text.toString()
                addNoteToFirestore(title, content)
            }
        }
    }





}