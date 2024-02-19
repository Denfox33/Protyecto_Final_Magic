package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot


import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class User_utilities {

    companion object{
        fun user_Exist(users: List<Usuario>, name: String): Boolean {
            return users.any { it.name!!.lowercase() == name.lowercase() }
        }

        fun getUserList(db_ref: DatabaseReference): MutableList<Usuario> {
            var userlist = mutableListOf<Usuario>()

            db_ref.child("Shop")
                .child("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot ->
                            val pojo_user = hijo.getValue(Usuario::class.java)
                            userlist.add(pojo_user!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })




            return userlist
        }

        fun user_add(
            db_ref: DatabaseReference,
            id: String,
            user_type: String,
            name: String,
            email: String,
            password: String,
            date: String,
            url_firebase: String?,
            noti_status: Int,
            user_notification: String
        ) =
            db_ref.child("Shop").child("Users").child(id).setValue(
                Usuario(
                    id,
                    user_type,
                    name,
                    email,
                    password,
                    date,
                    url_firebase,
                    noti_status,
                    user_notification
                )
            )

        suspend fun save_logo(sto_ref: StorageReference, id: String, logo: Uri): String {
            lateinit var url_logo_firebase: Uri

            url_logo_firebase = sto_ref.child("Shop").child("ProfilePic").child(id)
                .putFile(logo).await().storage.downloadUrl.await()

            return url_logo_firebase.toString()
        }

        fun courrutine_thing(activity: AppCompatActivity, context: Context, text: String) {
            activity.runOnUiThread {
                Toast.makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun loading_animation(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transitions = DrawableTransitionOptions.withCrossFade(500)

        fun glideOptions(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(loading_animation(context))
                .fallback(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_error_24)
            return options
        }

    }
}