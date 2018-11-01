package pl.documenteditor.documenteditor

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.*
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.documenteditor.documenteditor.adapters.DocumentListAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var user: User? = User(username = "John", password = "pass")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        user = intent.getSerializableExtra(LoginActivity.USER_DATA) as? User ?: user


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        API_button.setOnClickListener {
            val url = Constants.REST_SERVERS_ADDRESS + "online-docs/documents/"

            AsyncTaskHandleRestApi().execute(url)
        }
    }

    inner class AsyncTaskHandleRestApi : AsyncTask<String, String, List<Document>>() {

        override fun doInBackground(vararg url: String?): List<Document> {

            try {
                val request = Request.Builder().url(url[0]).build()
                val response = OkHttpClient().newCall(request).execute();
                val string = response.body()?.string()
                println(string)
                val lDok = GsonBuilder().create().fromJson(string, Array<Document>::class.java).toList()

                return lDok
            } catch (ex: Exception) {
                Log.e(TAG, "Cant get data from rest api server", ex)
            }

            return emptyList()
        }

        override fun onPostExecute(result: List<Document>?) {
            super.onPostExecute(result)

            val adapter = DocumentListAdapter(this@MainActivity, result!!)
            docListView.adapter = adapter

            docListView.setOnItemClickListener { parent, view, position, id ->
                val selectedDocument = result[position]

                val intentDocumentEditing = Intent(this@MainActivity, DocumentEditingActivity::class.java)

                intentDocumentEditing.putExtra(DOCUMENT_DATA, selectedDocument)

                startActivity(intentDocumentEditing)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        if (user != null) {
            username_textView.text = user!!.username
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    companion object {
        const val TAG: String = "ODE_MainActivity" // ODE - online document editor
        const val DOCUMENT_DATA = "document_data_object"
    }

}
