package pl.documenteditor.documenteditor.model

import java.io.Serializable

data class User(val id: Int = 0, val username: String, var password: String) : Serializable