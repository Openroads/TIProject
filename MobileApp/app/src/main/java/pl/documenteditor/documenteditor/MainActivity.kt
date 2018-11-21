package pl.documenteditor.documenteditor

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.*
import okhttp3.*
import pl.documenteditor.documenteditor.LoginActivity.Companion.USER_DATA
import pl.documenteditor.documenteditor.adapters.DocumentListAdapter
import pl.documenteditor.documenteditor.model.Document
import pl.documenteditor.documenteditor.model.NewDocument
import pl.documenteditor.documenteditor.model.Operation
import pl.documenteditor.documenteditor.model.Operation.*
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants
import pl.documenteditor.documenteditor.utils.Constants.Companion.NORMAL_CLOSURE_SOCKET_STATUS
import pl.documenteditor.documenteditor.utils.JsonUtils
import pl.documenteditor.documenteditor.utils.JsonUtils.Companion.DATA_PROPERTY
import pl.documenteditor.documenteditor.utils.JsonUtils.Companion.DOCUMENT_ID_PROPERTY
import pl.documenteditor.documenteditor.utils.JsonUtils.Companion.DOCUMENT_PROPERTY
import pl.documenteditor.documenteditor.utils.JsonUtils.Companion.OPERATION_PROPERTY

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var user: User? = User(1, username = "John", password = "pass")

    private var document: NewDocument? = null

    private val client: OkHttpClient = OkHttpClient()

    private val DOCUMENT_LIST_REST_ENDPOINT = Constants.REST_SERVERS_ADDRESS + "online-docs/documents/"

    private val gson = Gson()

    private val jsonParser = JsonParser()

    private lateinit var documentListAdapter: DocumentListAdapter

    private lateinit var ws: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        user = intent.getSerializableExtra(LoginActivity.USER_DATA) as? User ?: user

        fab.setOnClickListener { view ->
            document = NewDocument(NewFileEdit.text.toString())
            NewDocumentTask().execute()
            NewFileEdit.text.clear()
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        val request = Request.Builder().url(Constants.WEB_SOCKET_ADDRESS + "broadcast/").build()

        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        updateButton.setOnClickListener {
            AsyncTaskHandleRestApi().execute(DOCUMENT_LIST_REST_ENDPOINT)
        }

        docListView.setOnItemClickListener { parent, view, position, id ->
            val selectedDocument = documentListAdapter.getItem(position)
            sendIntentToDocumentEditing(user!!, selectedDocument)
        }
    }

    override fun onStart() {
        super.onStart()
        AsyncTaskHandleRestApi().execute(DOCUMENT_LIST_REST_ENDPOINT)
    }

    override fun onDestroy() {
        ws.close(NORMAL_CLOSURE_SOCKET_STATUS, "Main activity destroyed")
        super.onDestroy()
    }


    inner class AsyncTaskHandleRestApi : AsyncTask<String, String, List<Document>>() {

        override fun doInBackground(vararg url: String?): List<Document> {

            try {
                val request = Request.Builder().url(url[0]).build()
                val response = client.newCall(request).execute();
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

            documentListAdapter = DocumentListAdapter(this@MainActivity, result!!.toMutableList())
            docListView.adapter = documentListAdapter
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

    inner class NewDocumentTask : AsyncTask<String, String, Document>() {

        override fun doInBackground(vararg url: String?): Document? {

            try {
                val toJson = gson.toJson(document)
                Log.d(TAG, "Main Json           $toJson")
                val request = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/documents/")
                    .post(RequestBody.create(JsonUtils.JSON, toJson))
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d(TAG, "Response body with new created document: $responseBody")
                    val newCreatedDocument = gson.fromJson(responseBody, Document::class.java)

                    val rootElement = JsonObject()
                    val dataRootElement = JsonObject()
                    dataRootElement.add(DOCUMENT_PROPERTY, jsonParser.parse(responseBody))

                    rootElement.addProperty(OPERATION_PROPERTY, Operation.ADD.name)
                    rootElement.add(DATA_PROPERTY, dataRootElement)

                    val asString = gson.toJson(rootElement)

                    ws.send(asString)

                    return newCreatedDocument
                }

            } catch (ex: Exception) {
                Log.e(DocumentEditingActivity.TAG, "Cant get data from rest api server", ex)
            }
            return null
        }

        override fun onPostExecute(result: Document?) {
            if (result != null) {
                Toast.makeText(this@MainActivity, "New file created!", Toast.LENGTH_LONG).show()
                sendIntentToDocumentEditing(user!!, result)
            } else {
                Toast.makeText(this@MainActivity, "Can't create new file", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendIntentToDocumentEditing(user: User, document: Document) {
        val intent = Intent(this@MainActivity, DocumentEditingActivity::class.java)
        intent.putExtra(DOCUMENT_DATA, document)
        intent.putExtra(USER_DATA, user)
        startActivity(intent)
    }

    private inner class EchoWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "Web-socket echo on open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(DocumentEditingActivity.TAG, "Receiving echo message : $text")
            val receivedJson = jsonParser.parse(text).asJsonObject
            val operation = Operation.valueOf(receivedJson.get(OPERATION_PROPERTY).asString)
            val operationData = receivedJson.get(DATA_PROPERTY).asJsonObject

            when (operation) {
                ADD -> {
//                    val asString = operationData.get(DOCUMENT_PROPERTY).asString
                    val asString2 = operationData.get(DOCUMENT_PROPERTY).toString()
                    val doc = gson.fromJson(asString2, Document::class.java)
                    Log.d(TAG, "ADD  document with id $doc")
                    runOnUiThread {
                        documentListAdapter.add(doc)
                        documentListAdapter.notifyDataSetChanged()
                    }
                }

                LOCK -> {
                    val documentId = operationData.get(DOCUMENT_ID_PROPERTY).asInt
                    val editingBy = operationData.get("editingBy").asString
                    Log.d(TAG, "LOCK document with id $documentId by $editingBy")
                    runOnUiThread {
                        documentListAdapter.lockDocument(documentId, editingBy)
                    }
                }

                UNLOCK -> {
                    val documentId = operationData.get(DOCUMENT_ID_PROPERTY).asInt
                    Log.d(TAG, "UNLOCK document with id $documentId")
                    runOnUiThread {
                        documentListAdapter.unlockDocument(documentId)
                    }
                }
                DELETE -> {
                    val documentId = operationData.get(DOCUMENT_ID_PROPERTY).asInt
                    Log.d(TAG, "Delete document with id $documentId")
                    runOnUiThread {
                        documentListAdapter.deleteDocument(documentId)
                    }
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "Web socket echo closing: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "Web socket echo failure with response: $response", t)
        }
    }

}
