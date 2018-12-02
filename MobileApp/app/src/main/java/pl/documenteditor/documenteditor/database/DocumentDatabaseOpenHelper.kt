package pl.documenteditor.documenteditor.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import pl.documenteditor.documenteditor.model.Document

class DocumentDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DATABASE_NAME, null, 1) {
    companion object {

        private const val DATABASE_NAME = "documentDB.db"
        const val TABLE_DOCUMENTS = "documents"
        const val TABLE_USERS = "users"

        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_VERSION = "version"
        //val COLUMN_CREATED_BY = "createdBy_id"
        const val COLUMN_EDITING_BY = "editingBy_id"

        const val COLUMN_USER_NAME = "username"
        const val COLUMN_USER_PASSWORD = "password"


        fun insertDocument(db: SQLiteDatabase, doc: Document): Long {
            return db.insert(
                DocumentDatabaseOpenHelper.TABLE_DOCUMENTS,
                DocumentDatabaseOpenHelper.COLUMN_TITLE to doc.title,
                DocumentDatabaseOpenHelper.COLUMN_CONTENT to doc.content,
                DocumentDatabaseOpenHelper.COLUMN_VERSION to doc.version,
                DocumentDatabaseOpenHelper.COLUMN_EDITING_BY to doc.editingBy
            )
        }


        private var singletonInstance: DocumentDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DocumentDatabaseOpenHelper {
            if (singletonInstance == null) {
                singletonInstance = DocumentDatabaseOpenHelper(ctx.applicationContext)
            }
            return singletonInstance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Document table
        db.createTable(
            TABLE_DOCUMENTS, true,
            COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
            COLUMN_TITLE to TEXT,
            COLUMN_CONTENT to BLOB,
            COLUMN_VERSION to INTEGER,
            //COLUMN_CREATED_BY to INTEGER,
            COLUMN_EDITING_BY to TEXT
        )
        //user table
        db.createTable(
            TABLE_USERS, true,
            COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
            COLUMN_USER_NAME to TEXT,
            COLUMN_USER_PASSWORD to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(TABLE_DOCUMENTS, true)
    }
}

// Access property for Context
val Context.database: DocumentDatabaseOpenHelper
    get() = DocumentDatabaseOpenHelper.getInstance(applicationContext)
