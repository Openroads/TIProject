package pl.documenteditor.documenteditor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_document_editing.*
import pl.documenteditor.documenteditor.model.Document

class DocumentEditingActivity : AppCompatActivity() {

    private var document: Document? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_editing)
        setSupportActionBar(toolbar)

        // this is the document selected from list view
        document = intent.getSerializableExtra(MainActivity.DOCUMENT_DATA) as? Document

        Log.i(TAG, "Document object selected on user list: " + document.toString())

    }

    companion object {
        const val TAG: String = "ODE_DocumentEditingActivity" // ODE - online document editor
    }

}
