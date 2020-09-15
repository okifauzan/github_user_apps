package com.example.submission2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submission2.model.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainViewModel: ViewModel() {

    private val listUsername = MutableLiveData<ArrayList<User>>()

    fun setUsername(defaultUsername: String?){
        val listItem = ArrayList<User>()
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$defaultUsername"
        client.addHeader("Authorization", "token 4234da956299006896e702b36841e723f74e5c59")
        client.addHeader("User-Agent","request")
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                val response = JSONObject(result)
                when(response.getInt("total_count")){
                    else -> gettingData(response, listItem)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    private fun gettingData(response: JSONObject, listItem: ArrayList<User>){
        try {
            val jsonArray = response.getJSONArray("items")
            for (i in 0 until jsonArray.length()){
                val data =jsonArray.getJSONObject(i)
                val user = User()
                user.username = data.getString("login")
                user.photo = data.getString("avatar_url")
                user.link = data.getString("html_url")
                listItem.add(user)
            }
            listUsername.postValue(listItem)
        } catch (e: Exception){
            Log.d("Exception", e.message.toString())
        }
    }

    fun getUsername(): LiveData<ArrayList<User>>{
        return listUsername
    }
}