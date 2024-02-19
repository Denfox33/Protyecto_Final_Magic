package com.example.myapplication.Registro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.StatusCreatingUser
import com.example.myapplication.User_utilities
import com.example.myapplication.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.example.myapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import kotlin.coroutines.CoroutineContext

class RegisterActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var btn_back: AppCompatButton
    private lateinit var btn_register: AppCompatButton
    private lateinit var name: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var password_check: TextInputEditText
    private lateinit var email: TextInputEditText


    private var url_logo: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var user_list: MutableList<Usuario>


    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val this_activity = this
        job = Job()

        //Activity buttons
        btn_back = binding.btnBack
        btn_register = binding.btnAccept

        //Back button
        btn_back.setOnClickListener {
            onBackPressed()
        }


        //binding user attributes
        name = binding.editTextName
        password = binding.editTextPassword
        password_check = binding.editTextPasswordCheck
        email = binding.editTextEmail





        btn_register.setOnClickListener {
            setup()
        }
    }

    private fun setup() {

        val this_activity = this
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()
        user_list = User_utilities.getUserList(db_ref)


        if (name.text.toString().trim().isEmpty() ||
            password.text.toString().trim().isEmpty() ||
            password_check.text.toString().trim().isEmpty() ||
            email.text.toString().trim().isEmpty()
        ) {
            Toast.makeText(
                applicationContext, "Missing data in the form", Toast.LENGTH_SHORT
            ).show()
        } else if (password.text.toString() != password_check.text.toString()) {
            Toast.makeText(
                applicationContext, "Passwords do not match", Toast.LENGTH_SHORT
            ).show()
        } else {
            var id_gen: String? = db_ref.child("Shop").child("Users").push().key

            launch{


                val androidId =
                    Settings.Secure.getString(
                        applicationContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )


                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registro exitoso
                            val user = task.result?.user
                            if (user != null) {
                                // Aquí puedes realizar cualquier acción adicional, como agregar el usuario a la base de datos
                                User_utilities.user_add(
                                    db_ref,
                                    id_gen!!,
                                    "user",
                                    name.text.toString(),
                                    email.text.toString(),
                                    password.text.toString(),
                                    dateFormat.toString(),
                                    "",
                                    StatusCreatingUser.CREATED,
                                    androidId
                                )
                                Log.e("RegisterActivity", "User created: ${user.email}")

                                showLogin(user.email ?: "", ProviderType.BASIC)
                                Toast.makeText(applicationContext, "User created", Toast.LENGTH_SHORT).show()
                                onBackPressed()

                                User_utilities.courrutine_thing(
                                    this_activity,
                                    applicationContext,
                                    "User successfully created"
                                )

                                val activity = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(activity)


                            } else {
                                // El usuario es nulo, manejar el caso de manera adecuada
                                Toast.makeText(applicationContext, "User already exist", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Error al registrar al usuario
                            val exception = task.exception
                            if (exception != null) {
                                Log.e("RegisterActivity", "Error al registrar usuario: ${exception.message}")
                                Toast.makeText(applicationContext, "Error creating user: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }





            }





        }


    }

    fun showLogin(email: String, provider: ProviderType) {
        val activity = Intent(applicationContext, LoginActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(activity)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()


    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}



