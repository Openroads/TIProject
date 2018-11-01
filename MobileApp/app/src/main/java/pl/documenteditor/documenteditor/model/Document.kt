package pl.documenteditor.documenteditor.model

import java.io.Serializable

data class Document(val id: Int, val title: String, val content: String, val editingBy: String) : Serializable