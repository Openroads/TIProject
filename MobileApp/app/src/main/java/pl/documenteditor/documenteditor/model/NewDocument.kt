package pl.documenteditor.documenteditor.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class NewDocument( var title: String, var content: String="Input you own text" ) : Serializable