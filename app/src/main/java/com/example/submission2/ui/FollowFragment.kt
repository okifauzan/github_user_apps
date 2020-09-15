package com.example.submission2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission2.R
import com.example.submission2.adapter.UserAdapter
import com.example.submission2.model.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_follow.*
import org.json.JSONArray

class FollowFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter

    companion object{
        private val ARG_SECTION_NUMBER = "section_number"
        var USERNAME_NAME = "username"

        fun newInstance(index: Int, username: String?): FollowFragment {
            val fragment = FollowFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putString(USERNAME_NAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()

        rv_followers.layoutManager = LinearLayoutManager(view.context)
        rv_followers.adapter = userAdapter

        var index = 1
        var username = "username"
        if (arguments != null){
            index = arguments?.getInt(ARG_SECTION_NUMBER, 0) as Int
            username = arguments?.getString(USERNAME_NAME) as String
        }

        getData(username, index)
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: User) {
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                val detailUser = activity as DetailActivity
                detailUser.getData(data.username)
                detailUser.progressBarDetail.visibility = View.VISIBLE
                getData(data.username, index)
                (activity as? AppCompatActivity)?.supportActionBar?.title = data.username
            }
        })
    }

    private fun getData(usernameFollow: String?, index: Int){
        val listItem = ArrayList<User>()
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 4234da956299006896e702b36841e723f74e5c59")
        client.addHeader("User-Agent","request")
        var url = "default"
        when(index){
            1 -> url = "https://api.github.com/users/$usernameFollow/following"
            2 -> url = "https://api.github.com/users/$usernameFollow/followers"
        }
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                val response = JSONArray(result)
                try {
                    for (i in 0 until response.length()){
                        val user = User()
                        user.username = response.getJSONObject(i).getString("login")
                        user.photo = response.getJSONObject(i).getString("avatar_url")
                        user.link = response.getJSONObject(i).getString("html_url")
                        listItem.add(user)
                    }
                    userAdapter.setData(listItem)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }
}