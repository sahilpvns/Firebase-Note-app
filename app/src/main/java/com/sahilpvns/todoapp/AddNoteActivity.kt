package com.sahilpvns.todoapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddNoteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_note)



        db = FirebaseFirestore.getInstance()

        val btn_save = findViewById<Button>(R.id.btn_save)
        val et_title = findViewById<TextView>(R.id.et_title)
        val et_content = findViewById<EditText>(R.id.et_content)



        btn_save.setOnClickListener {
            val title = et_title.text.toString()
            val content = et_content.text.toString()

            val note = Note(title, content)

            db.collection("notes").add(note)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, documentReference.id, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "$e Error", Toast.LENGTH_LONG).show()
                }
        }


    }

}