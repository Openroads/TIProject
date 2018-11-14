package pl.documenteditor.documenteditor

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_document_editing.*
import kotlinx.android.synthetic.main.content_document_editing.*
import okhttp3.*
import org.json.JSONObject
import pl.documenteditor.documenteditor.adapters.MessageAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.Message
import pl.documenteditor.documenteditor.model.Operation
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants


class DocumentEditingActivity : AppCompatActivity() {

    private var document: Document? = null

    private lateinit var user: User

    private var client: OkHttpClient = OkHttpClient()

    private lateinit var adapter: MessageAdapter

    private val messageGson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    private lateinit var wsChat: WebSocket
    private lateinit var wsBroadcast: WebSocket

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

        GetDocumentDetailsTask().execute(url)

        adapter = MessageAdapter(this)
        messages_view.adapter = adapter

        val request = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "chat/" + document?.id).build()
        val listener = ChatWebSocketListener()
        wsChat = client.newWebSocket(request, listener)

        val wsBroadcastRequest = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "broadcast/").build()
        wsBroadcast = client.newWebSocket(wsBroadcastRequest, BroadcastWebSocketListener())

        send_button.setOnClickListener {
            sendMessage(wsChat)
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

            //onBackPressed()//TODO TO EXPLAIN
            UnlockDocument().execute()
            finish()


        }
        if (document?.editingBy != null && document?.editingBy != user.username) {
            Toast.makeText(this, "You can't edit the file now", Toast.LENGTH_LONG).show()
            documentContext.setKeyListener(null)
            buttonDel.isEnabled = false
            buttonSave.isEnabled = false
        }

    }

    override fun onBackPressed() {

        if (document?.editingBy == user.username) {
            UnlockDocument().execute()
        } else {
            finish()
        }

        super.onBackPressed()
    }

    override fun onDestroy() {
        wsChat.close(NORMAL_CLOSURE_STATUS, "Activity destroyed")
        super.onDestroy()
    }

    companion object {
        const val TAG: String = "DocumentEditingActivity" // ODE - online document editor
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private inner class ChatWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Web-socket on open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.i(TAG, "Receiving chat message : $text")
            val m = messageGson.fromJson(text, Message::class.java)
            if (m.username == user.username) {
                m.belongsToCurrentUser = true
            }
            runOnUiThread {
                adapter.add(m)
                messages_view.setSelection(messages_view.count - 1)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "Websocket closing: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "Web socket failure with response: $response", t)
        }
    }

    private fun sendMessage(ws: WebSocket) {
        val message = Message(chat_message_edit_text.text.toString(), user.username)
        ws.send(messageGson.toJson(message))
        chat_message_edit_text.text.clear()
    }

    inner class UnlockDocument : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg url: String?): Boolean {
            try {
                val requestBlock = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + "/stop-editing/")
                    .post(RequestBody.create(null, ""))
                    .build()
                val response = OkHttpClient().newCall(requestBlock).execute()

                if (response.isSuccessful) {
                    return true
                }

            } catch (ex: Exception) {
                Log.e(TAG, "Cant get data from rest api server", ex)
            }
            return false

        }

        override fun onPostExecute(result: Boolean) {
            Log.i(TAG, "Document unlock status: $result")
            super.onPostExecute(result)
        }
    }

    inner class LockDocument : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg url: String?): Boolean {
            try {
                val requestLock = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/document/" + document?.id + "/editing-by/" + user.id + "/")
                    .post(RequestBody.create(null, ""))
                    .build()
                val response = OkHttpClient().newCall(requestLock).execute()

                if (response.isSuccessful) {
                    val rootObject = JSONObject()
                    rootObject.put("operation", Operation.LOCK)
                    val dataRootObject = JSONObject()
                    dataRootObject.put("documentId", document?.id)
                    dataRootObject.put("editingBy", user.username)
                    rootObject.put("data", dataRootObject)
                    val asString = rootObject.toString()
                    wsBroadcast.send(asString)
                    return true
                }
            } catch (ex: Exception) {
                Log.e(DocumentEditingActivity.TAG, "Cant get data from rest api server", ex)
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            Log.i(TAG, "Document lock status: $result")
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
            if (result) {
                Toast.makeText(this@DocumentEditingActivity, "File deleted! ", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(
                    this@DocumentEditingActivity,
                    "Something went wrong, file not deleted! ",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    inner class UpdateDocumentTask : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg url: String?): Boolean {

            try {
                val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                val toJson = gson.toJson(document)
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
            if (result) {
                Toast.makeText(this@DocumentEditingActivity, "File saved! ", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@DocumentEditingActivity,
                    "Something went wrong, file not saved! ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    inner class GetDocumentDetailsTask : AsyncTask<String, String, Document>() {

        override fun doInBackground(vararg url: String?): Document? {

            try {
                val request = Request.Builder().url(url[0]).build()
                val response = OkHttpClient().newCall(request).execute()
                val string = response.body()?.string()
                return GsonBuilder().create().fromJson(string, Document::class.java)

            } catch (ex: Exception) {
                Log.e(MainActivity.TAG, "Cant get data from rest api server", ex)
            }
            return null

        }

        override fun onPostExecute(result: Document?) {
            super.onPostExecute(result)
            document = result ?: document
            if (document?.editingBy == null) {
                LockDocument().execute()
                document?.editingBy = user.username
            }

            documentContext.setText(document!!.content)
            this@DocumentEditingActivity.title = document!!.title

        }
    }

    private inner class BroadcastWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "BroadcastWebSocketListener echo on open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "BroadcastWebSocketListener receiving message : $text")

        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "BroadcastWebSocketListener socket closing: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "BroadcastWebSocketListener  failure with response: $response", t)
        }
    }
}