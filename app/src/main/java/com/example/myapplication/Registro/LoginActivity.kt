package com.example.myapplication.Registro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.databinding.ActivityLoginBinding


enum class ProviderType {
    BASIC,
    GOOGLE
}
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnLogin = binding.buttonLogin

        binding.apply {
            btnLogin.setOnClickListener {
                val email = editTextUser.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val isAdmin = email.endsWith("@admin.com")
                                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                    putExtra("isAdmin", isAdmin)
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@LoginActivity, "Authentication error", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this@LoginActivity, "Empty email or password", Toast.LENGTH_LONG).show()
                }
            }

            buttonRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }


        }
    }


    }

