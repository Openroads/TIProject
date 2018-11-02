package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_document_editing.*
import kotlinx.android.synthetic.main.content_document_editing.*
import okhttp3.*
import okio.ByteString
import pl.documenteditor.documenteditor.adapters.MessageAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.Message
import pl.documenteditor.documenteditor.utils.Constants


class DocumentEditingActivity : AppCompatActivity() {
    val JSON = MediaType.parse("application/json; charset=utf-8")

    private var document: Document? = null

    private var client: OkHttpClient = OkHttpClient()

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_editing)
        setSupportActionBar(toolbar)

        // this is the document selected from list view
        document = intent.getSerializableExtra(MainActivity.DOCUMENT_DATA) as? Document

        Log.i(TAG, "Document object selected on user list: " + document.toString())
        val id: Int = document!!.id
        val url = Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + id
        GetDocumentDetailsTask().execute(url)

        adapter = MessageAdapter(this)
        messages_view.adapter = adapter
        // taki sam task jak w main ale po 1 dokument.
        // po pobraniu zaciagnac nowy
        // nadpisanie, to
        // tylko juz url z tym id jaki ktos kliknął.
        // wyslac put zserializowany do JSON, pod ten sam url z 1.
        //delete to tylko url
        //put, wyslac body czyli ten json.\

        val request = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "say-hello/").build()
        val listener = EchoWebSocketListener()
        val ws = client.newWebSocket(request, listener)

        send_button.setOnClickListener {
            start(ws)
        }
        buttonCancel.setOnClickListener {
            super.onBackPressed()

        }
        buttonDel.setOnClickListener {
            DeleteDocumentTask().execute()
            this.finish()
        }

        buttonSave.setOnClickListener {

        }

    }

    companion object {
        const val TAG: String = "ODE_DocumentEditingActivity" // ODE - online document editor
        const val NORMAL_CLOSURE_STATUS = 1000
    }


    private inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "websocket on open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Receiving : $text")
            val m = Message(text, false)
            runOnUiThread {
                adapter.add(m)
                messages_view.setSelection(messages_view.count - 1)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            //webSocket.close(NORMAL_CLOSURE_STATUS, null)
            println("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("Connection failed : $response")
            Log.e(TAG, "Web socket failure", t)
        }

    }

    private fun start(ws: WebSocket) {
        Log.d(TAG, "Sending to ws: " + ws.toString())
        ws.send("s")
        //client.dispatcher().executorService().shutdown()
    }

    inner class DeleteDocumentTask : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean {
            try {
                val request = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + '/')
                    .delete()
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                println("PROBA REQUEST: " + request)
                println("Response: " + response)

                if (response.isSuccessful) {
                    return true
                }

            } catch (ex: Exception) {
                Log.e(MainActivity.TAG, "Cant get data from rest api server", ex)
            }
            return false

        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            println("result: " + result)

            // proponuje zrobić if - zobaczyc ze result jest true czy false jak true to poinformować
            // np Toastem czy udało sie usunąc pomyślnie czy nie
        }
    }

    inner class UpdateDocumentTask : AsyncTask<String, String, Document>() {

        override fun doInBackground(vararg url: String?): Document {

            try {
                val request = Request.Builder()
                    .url(url[0])
                    .build()
                val response = OkHttpClient().newCall(request).execute();
                val string = response.body()?.string()
                println(string)
                val lDok = GsonBuilder().create().fromJson(string, Document::class.java)

                return lDok
            } catch (ex: Exception) {
                Log.e(MainActivity.TAG, "Cant get data from rest api server", ex)
            }
            return null!!

        }

        override fun onPostExecute(result: Document?) {
            super.onPostExecute(result)

            //val adapter = DocumentContextAdapter(this@DocumentEditingActivity, result!!)
            //documentTitle.text=result!!.title
            documentContext.setText(result!!.content)
            this@DocumentEditingActivity.title = result!!.title


        }
    }

    inner class GetDocumentDetailsTask : AsyncTask<String, String, Document>() {

        override fun doInBackground(vararg url: String?): Document? {

            try {
                val request = Request.Builder().url(url[0]).build()
                val response = OkHttpClient().newCall(request).execute()
                val string = response.body()?.string()
                println(string)

                return GsonBuilder().create().fromJson(string, Document::class.java)
            } catch (ex: Exception) {
                Log.e(MainActivity.TAG, "Cant get data from rest api server", ex)
            }

            return null

        }

        override fun onPostExecute(result: Document?) {
            super.onPostExecute(result)

            document = result ?: document

            documentContext.setText(document!!.content)
            this@DocumentEditingActivity.title = document!!.title

        }
    }

}
