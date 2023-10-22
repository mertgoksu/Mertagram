package com.mertg.kotlininstagram.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mertg.kotlininstagram.R
import com.mertg.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signInButton = binding.signInButton
        registerButton = binding.registerButton

        makeButtonsEnabled()

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.darkThemeColor)))
        binding.signInButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkThemeColor))
        binding.registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkThemeColor))

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun makeButtonsEnabled(){
        signInButton.isEnabled = true
        registerButton.isEnabled = true
    }

    private fun makeButtonsDisabled(){
        registerButton.isEnabled = false
        signInButton.isEnabled = false
    }

    fun signInClicked(view: View){
        makeButtonsDisabled()
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Enter E-mail and Password", Toast.LENGTH_SHORT).show()
            makeButtonsEnabled()
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
                
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                makeButtonsEnabled()
            }
        }
    }

    fun registerClicked(view: View){
        makeButtonsDisabled()
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Enter E-mail and Password", Toast.LENGTH_SHORT).show()
            makeButtonsEnabled()
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                //success
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                //failed
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                makeButtonsEnabled()
            }
        }
    }
}