package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.documenteditor.documenteditor.adapters.DocumentListAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val user = intent.getSerializableExtra(LoginActivity.USER_DATA) as User

        API_button.setOnClickListener {
            val url = Constants.REST_SERVERS_ADDRESS + "online-docs/documents/"
            AsyncTaskHandleRestApi().execute(url)
        }
    }

    inner class AsyncTaskHandleRestApi : AsyncTask<String, String, List<Document>>() {

        override fun doInBackground(vararg url: String?): List<Document> {
            val request = Request.Builder().url(url[0]).build()
            val response = OkHttpClient().newCall(request).execute();
            val string = response.body()?.string()
            println(string)
            val lDok = GsonBuilder().create().fromJson(string, Array<Document>::class.java).toList()

            return lDok
        }

        override fun onPostExecute(result: List<Document>?) {
            super.onPostExecute(result)

            val adapter = DocumentListAdapter(this@MainActivity, result!!)
            docListView.adapter = adapter
        }
    }
}

