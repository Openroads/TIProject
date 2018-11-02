package pl.documenteditor.documenteditor.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class Document(@Expose(serialize = false) val id: Int,@Expose val title: String, @Expose var content: String, @Expose(serialize = false) val editingBy: String, @Expose val version:Int=0 ) : Serializable