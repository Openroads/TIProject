package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        API_button.setOnClickListener {
            val url="http://10.0.2.2:8000/hello-api/by-name/"+ nameText.text
            getFromRestAPI(url)
        }

    }

    fun getFromRestAPI(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()}
            override fun onResponse(call: Call, response: Response) {
                val message = response.body()?.string()
                println(message)
                textFromAPI.text=message
            }
        })
    }
}
