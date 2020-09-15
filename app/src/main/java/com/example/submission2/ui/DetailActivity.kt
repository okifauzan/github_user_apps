package com.example.submission2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.submission2.R
import com.example.submission2.adapter.SectionsPagerAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {

    companion object{
        var USERNAME = "username"
    }

    private var name: String? = null
    private var location: String? = null
    private var photo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val usernameDetail = intent.getStringExtra(USERNAME)
        supportActionBar?.title = usernameDetail
        supportActionBar?.elevation = 0f

        val sectionsPagerAdapter = SectionsPagerAdapter(this@DetailActivity, supportFragmentManager)
        sectionsPagerAdapter.username = usernameDetail
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        getData(usernameDetail)
    }

    fun getData(usernameDetail: String?){
        progressBarDetail.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$usernameDetail"
        client.addHeader("Authorization", "token 4234da956299006896e702b36841e723f74e5c59")
        client.addHeader("User-Agent","request")
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                progressBarDetail.visibility = View.INVISIBLE
                val result = String(responseBody)
                val response = JSONObject(result)
                try {
                    showResult(response)
                    inputdata(usernameDetail, name, location, photo)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    private fun showResult(response: JSONObject){
        name = response.getString("name")
        location = response.getString("location")
        photo = response.getString("avatar_url")
    }

    private fun inputdata(usernameDetail: String?, name: String?, location: String?, photo: String?){
        tv_username_detail.text = usernameDetail
        tv_name_detail.text = name
        tv_location_detail.text = location
        Glide.with(this@DetailActivity)
            .load(photo)
            .into(img_user_detail)
    }
}