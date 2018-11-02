package pl.documenteditor.documenteditor.model

data class Message(val text: String, val username: String, var belongsToCurrentUser:Boolean = false)