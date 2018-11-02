package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_document_editing.*
import kotlinx.android.synthetic.main.content_document_editing.*
import okhttp3.*
import okio.ByteString
import pl.documenteditor.documenteditor.adapters.MessageAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.Message
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants


class DocumentEditingActivity : AppCompatActivity() {

    private var document: Document? = null

    private lateinit var user: User

    private var client: OkHttpClient = OkHttpClient()

    private lateinit var adapter: MessageAdapter

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_editing)
        setSupportActionBar(toolbar)

        // this is the document selected from list view
        document = intent.getSerializableExtra(MainActivity.DOCUMENT_DATA) as? Document
        user = intent.getSerializableExtra(LoginActivity.USER_DATA) as User

        Log.i(TAG, "Document object selected on user list: " + document.toString())
        val id: Int = document!!.id
        val url = Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + id
        val url2 = Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + id + "/editing-by/" + user.id + "/"


        GetDocumentDetailsTask().execute(url, url2)

        adapter = MessageAdapter(this)
        messages_view.adapter = adapter

        val request = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "chat/" + document?.id).build()
        val listener = EchoWebSocketListener()
        val ws = client.newWebSocket(request, listener)

        if (document?.editingBy == null) {

            send_button.setOnClickListener {
                sendMessage(ws)
            }
            buttonCancel.setOnClickListener {
                onBackPressed()
            }

            buttonDel.setOnClickListener {
                DeleteDocumentTask().execute()
                onBackPressed()

            }

            buttonSave.setOnClickListener {
                document?.content = documentContext.text.toString()
                UpdateDocumentTask().execute()
                onBackPressed()

            }
        } else {
            Toast.makeText(this, "You can't edit the file now", Toast.LENGTH_LONG).show()
            documentContext.setKeyListener(null)
            buttonDel.isEnabled=false
            buttonSave.isEnabled=false
            buttonCancel.setOnClickListener {
                onBackPressed()
            }
        }

    }

    override fun onBackPressed() {
        UnlockDocument().execute()
        super.onBackPressed()
    }

    companion object {
        const val TAG: String = "ODE_DocumentEditingActivity" // ODE - online document editor
        const val NORMAL_CLOSURE_STATUS = 1000
    }


    private inner class EchoWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Web-socket on open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Receiving : $text")
            val m = gson.fromJson(text, Message::class.java)
            if (m.username == user.username) {
                m.belongsToCurrentUser = true
            }
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

    private fun sendMessage(ws: WebSocket) {
        val message = Message(chat_message_edit_text.text.toString(), user.username)
        ws.send(gson.toJson(message))
        //client.dispatcher().executorService().shutdown()
    }

    inner class UnlockDocument : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg url: String?): Boolean {
            try {
                val requestBlock = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + "/stop-editing/")
                    .post(RequestBody.create(null, ""))
                    .build()
                val response = OkHttpClient().newCall(requestBlock).execute()
                println("*****Response: " + response)
                if (response.isSuccessful) {
                    return true
                }

            } catch (ex: Exception) {
                Log.e(DocumentEditingActivity.TAG, "Cant get data from rest api server", ex)
            }
            return false

        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
        }
    }

    inner class DeleteDocumentTask : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean {
            try {
                val request = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + '/')
                    .delete()
                    .build()
                val response = OkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    return true
                }

            } catch (ex: Exception) {
                Log.e(DocumentEditingActivity.TAG, "Cant get data from rest api server", ex)
            }
            return false

        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
        }
    }

    inner class UpdateDocumentTask : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg url: String?): Boolean {

            try {
                val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

                val toJson = gson.toJson(document)
                Log.d(TAG, "Json sending in put " + toJson)
                val request = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + '/')
                    .put(RequestBody.create(Constants.JSON, toJson))
                    .build()
                val response = OkHttpClient().newCall(request).execute()


                if (response.isSuccessful) {
                    return true
                }

            } catch (ex: Exception) {
                Log.e(DocumentEditingActivity.TAG, "Cant get data from rest api server", ex)
            }
            return false

        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
        }
    }

    inner class GetDocumentDetailsTask : AsyncTask<String, String, Document>() {

        override fun doInBackground(vararg url: String?): Document? {

            try {
                val requestBlock = Request.Builder()
                    .url(url[1])
                    .post(RequestBody.create(null, ""))
                    .build()
                val response2 = OkHttpClient().newCall(requestBlock).execute()
                println("*****Response: " + response2)
                if (response2.isSuccessful) {

                    val request = Request.Builder().url(url[0]).build()
                    val response = OkHttpClient().newCall(request).execute()
                    val string = response.body()?.string()
                    return GsonBuilder().create().fromJson(string, Document::class.java)
                }
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
