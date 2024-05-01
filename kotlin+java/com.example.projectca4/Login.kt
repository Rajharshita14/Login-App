package com.example.projectca4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var reg: Button
    private lateinit var login: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        reg = findViewById(R.id.Register)
        login = findViewById(R.id.Login)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)


        reg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            if (checking()) {
                val email = email.text.toString()
                val password = password.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Authentication Failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter the Details", Toast.LENGTH_LONG).show()
            }
        }
    }

        private fun checking(): Boolean {
            return (email.text.toString().isNotBlank() && password.text.toString().isNotBlank())

        }
}
