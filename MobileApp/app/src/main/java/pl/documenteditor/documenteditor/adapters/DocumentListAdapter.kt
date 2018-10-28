package pl.documenteditor.documenteditor.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import pl.documenteditor.documenteditor.R
import pl.documenteditor.documenteditor.model.Document

class DocumentListAdapter(val context: Context, val documentList : List<Document>) :BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_layout,parent,false);

        //val documentId = view.findViewById(R.id.document_id) as AppCompatTextView
        val documentTitle = view.findViewById(R.id.document_title) as AppCompatTextView
        //documentId.text = documentList[position].id.toString()
        documentTitle.text = documentList[position].title

        if (documentList[position].editingBy != null)
        {
            println("Nie jest null")
            val documentEditingBy = view.findViewById(R.id.document_editingBy) as AppCompatTextView
            documentEditingBy.text=documentList[position].editingBy
            val documentLock=view.findViewById(R.id.lockIcon) as AppCompatImageView
            documentLock.visibility = View.VISIBLE
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return documentList[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getCount(): Int {
        return documentList.size
    }
}