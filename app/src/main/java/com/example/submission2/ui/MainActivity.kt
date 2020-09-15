package com.example.submission2.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission2.R
import com.example.submission2.adapter.UserAdapter
import com.example.submission2.model.User
import com.example.submission2.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter
    private var defaultUsername = "dicoding"
    private val title: String = "Github Users"
    private lateinit var mainViewModel: MainViewModel

    companion object{
        var SET_QUERY_DATA = "query"
        var GET_QUERY_DATA = "get_query"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = title

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()
        rv_users.layoutManager = LinearLayoutManager(this)
        rv_users.adapter = userAdapter

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)

        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: User) {
                showOnClicked(data)
            }
        })

        mainViewModel.setUsername(defaultUsername)
        mainViewModel.getUsername().observe(this, Observer { usernameItem ->
            if (usernameItem != null){
                userAdapter.setData(usernameItem)
                showLoading(false)
            } else {
                showLoading(true)
            }
        })

    }

    private fun showLoading(state: Boolean){
        if (state){
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun showOnClicked(data: User){
        val detailIntentUser = Intent(this@MainActivity, DetailActivity::class.java)
        detailIntentUser.putExtra(DetailActivity.USERNAME, data.username)
        startActivity(detailIntentUser)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                GET_QUERY_DATA = query
                setDataQuery(GET_QUERY_DATA)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                GET_QUERY_DATA = query
                setDataQuery(GET_QUERY_DATA)
                showLoading(true)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings){
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDataQuery(query: String){
        when{
            query.isEmpty() -> mainViewModel.setUsername(defaultUsername)
            else -> mainViewModel.setUsername(query)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SET_QUERY_DATA, GET_QUERY_DATA)
        super.onSaveInstanceState(outState)
    }
}