package pl.documenteditor.documenteditor.model

import com.google.gson.annotations.Expose

data class Message(@Expose val text: String, @Expose val username: String, @Expose(serialize = false, deserialize = false) var belongsToCurrentUser:Boolean = false)