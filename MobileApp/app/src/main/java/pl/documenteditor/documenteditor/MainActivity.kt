package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.beust.klaxon.Klaxon
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.utils.Constants
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        API_button.setOnClickListener {
            val url = Constants.REST_SERVERS_ADDRESS + "online-docs/documents/"
            //getFromRestAPI(url)
            fetchJson()

//            getDocument(1)
        }


    }

    fun getFromRestAPI(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val message = response.body()?.string()
                println(message)
               // textFromAPI.text = message
            }
        })
    }

    fun getDocument(id: Int) {
        val request = Request.Builder()
            .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + id)
            .build()

        var body: String? = ""

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                body = response.body()?.string()
                println("FROM SERVER" + body)
                val obj = Klaxon().parse<Document>(body ?: "")

                println("Document " + obj)

            }
        })
        println("BODYYYY : " + body)
    }
    fun fetchJson (){
        val url=Constants.REST_SERVERS_ADDRESS + "online-docs/documents/"
        val request=Request.Builder().url(url).build()

        val client= OkHttpClient()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call, response: Response) {
                val body =response.body()?.string()
                println("Body: " + body)
                val gson = GsonBuilder().create()
                val lDok= gson.fromJson(body, Array<Document>::class.java).toList()
               // textFromAPI.text=lDok.toString()
            }

        })
    }

}
class LDokument(val documents: List <Document>){

}