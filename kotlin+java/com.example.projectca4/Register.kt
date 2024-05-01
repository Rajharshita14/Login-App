package com.example.projectca4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emailRegister : EditText
    private lateinit var reg : Button
    private lateinit var passwordRegister : EditText
    private lateinit var name: EditText
    private lateinit var phone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailRegister = findViewById(R.id.emailRegister)
        reg = findViewById(R.id.Register)
        passwordRegister = findViewById(R.id.passwordRegister)
        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)

        reg.setOnClickListener {
            if (checking()) {
                val email = emailRegister.text.toString()
                val password = passwordRegister.text.toString()
                val name = name.text.toString()
                val phone = phone.text.toString()

                val user = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "email" to email
                )
                val users = db.collection("user")
                users.whereEqualTo("email", email).get().addOnSuccessListener { tasks ->
                    if (tasks.isEmpty) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Use Firebase user ID as document ID
                                    val userId = auth.currentUser?.uid
                                    userId?.let {
                                        users.document(it).set(user)
                                        val intent = Intent(this, Login::class.java)
                                        intent.putExtra("email", email)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "User Already Registered", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Enter the Details", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun checking(): Boolean {
        return (name.text.toString().isNotBlank() &&
                phone.text.toString().isNotBlank() &&
                emailRegister.text.toString().isNotBlank() &&
                passwordRegister.text.toString().isNotBlank())
    }
}
