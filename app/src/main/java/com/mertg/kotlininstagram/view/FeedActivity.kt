package com.mertg.kotlininstagram.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mertg.kotlininstagram.R
import com.mertg.kotlininstagram.adapter.FeedRecyclerAdapter
import com.mertg.kotlininstagram.databinding.ActivityFeedBinding
import com.mertg.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.darkThemeColor)))

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        val intent = intent
        val menuSelecterData = intent.getStringExtra("optionsMenu")
        if (menuSelecterData != null){
            getMyData()
        }else{
            getData()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            getData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        db.collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING) //"date" alanına göre azalan sıralama
            .addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents

                        postArrayList.clear() //mevcut verileri temizle

                        for (document in documents) {
                            //casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(userEmail, comment, downloadUrl)
                            postArrayList.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMyData(){
        val currentEmail = auth.currentUser!!.email!!
        db.collection("Posts").whereEqualTo("userEmail", currentEmail)
            .orderBy("date", Query.Direction.DESCENDING) // "date" alanına göre azalan sıralama
            .addSnapshotListener { value, error ->
                if (error != null) {
//                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents

                            postArrayList.clear() //mevcut verileri temizle

                            for (document in documents) {
                                //casting
                                val comment = document.get("comment") as String
                                val userEmail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String

                                println(comment)
                                val post = Post(userEmail, comment, downloadUrl)
                                postArrayList.add(post)
                            }

                            feedAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_post -> {
                val intent = Intent(this, UploadActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.sign_out -> {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.my_posts -> {
                getMyData() // Sadece kendi postlarınızı almak için bu fonksiyonu çağırın
                return true
            }
            R.id.all_posts -> {
                getData()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}