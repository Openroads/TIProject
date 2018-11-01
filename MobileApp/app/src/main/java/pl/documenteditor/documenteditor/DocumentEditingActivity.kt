package pl.documenteditor.documenteditor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_document_editing.*
import kotlinx.android.synthetic.main.content_document_editing.*
import okhttp3.*
import okio.ByteString
import pl.documenteditor.documenteditor.adapters.MessageAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.Message
import pl.documenteditor.documenteditor.utils.Constants


class DocumentEditingActivity : AppCompatActivity() {

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

        adapter = MessageAdapter(this)
        messages_view.adapter = adapter

        val request = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "say-hello/").build()
        val listener = EchoWebSocketListener()
        val ws = client.newWebSocket(request, listener)

        send_button.setOnClickListener {
            start(ws)
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
}
