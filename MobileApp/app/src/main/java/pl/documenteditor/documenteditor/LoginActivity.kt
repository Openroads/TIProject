package pl.documenteditor.documenteditor

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import pl.documenteditor.documenteditor.database.DocumentDatabaseOpenHelper
import pl.documenteditor.documenteditor.database.database
import pl.documenteditor.documenteditor.model.User
import pl.documenteditor.documenteditor.utils.Constants
import pl.documenteditor.documenteditor.utils.JsonUtils
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        //populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(username, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok,
                    { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete()
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        username.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(usernameStr)) {
            username.error = getString(R.string.error_field_required)
            focusView = username
            cancel = true
        } else if (!isUsernameValid(usernameStr)) {
            username.error = getString(R.string.error_invalid_email)
            focusView = username
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            val user = User(username = usernameStr, password = passwordStr)
            mAuthTask = UserLoginTask(user)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        if (username.length < 5) {
            return false
        }
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(
            this,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(
                ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
            ), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
                ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE
            ),

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        )
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(
            this@LoginActivity,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection
        )

        username.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        )
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    inner class UserLoginTask internal constructor(private val user: User) :
        AsyncTask<Void, Void, User>() {
        private val db = database.writableDatabase
        override fun doInBackground(vararg params: Void): User? {
            return try {
                val gson = Gson()
                val request = Request.Builder()
                    .url(Constants.REST_SERVERS_ADDRESS + "online-docs/users/login/")
                    .post(RequestBody.create(JsonUtils.JSON, gson.toJson(this.user)))
                    .build()
                val response = OkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    val userJson = response.body()?.string()
                    val user = gson.fromJson(userJson, User::class.java)



                    return user
                }
                return null

            } catch (ex: Exception) {
                Log.e(TAG, "Cant connect to rest api server", ex)
                return null
            }

        }

        override fun onPostExecute(user: User?) {
            mAuthTask = null
            showProgress(false)

            if (user != null) {
                if (user.username.isNotBlank() && user.id != 0) {
                    user.password = this.user.password
                    println("User id : " + user.id)
                    println("User name : " + user.username)
                    println("User name : " + user.password)

                    Log.d(TAG, "Saving user to local database")
                    val userFromDb = selectUserDataFromLocalDb()
                    if (userFromDb == null) {
                        val insert = db.insert(
                            DocumentDatabaseOpenHelper.TABLE_USERS,
                            DocumentDatabaseOpenHelper.COLUMN_ID to user.id,
                            DocumentDatabaseOpenHelper.COLUMN_USER_NAME to user.username,
                            DocumentDatabaseOpenHelper.COLUMN_USER_PASSWORD to user.password
                        )
                        Log.i(TAG, "Inserting status $insert")
                    }
                    startMainActivity(user)
                    return
                }
            } else {
                Log.i(TAG, "Trying to get user from local database")
                val userFromDb = selectUserDataFromLocalDb()

                if (userFromDb != null) {
                    startMainActivity(userFromDb)
                    return
                }

            }

            password.error = getString(R.string.error_incorrect_password)
            password.requestFocus()

        }

        private fun selectUserDataFromLocalDb(): User? {
            val userList = db.select(DocumentDatabaseOpenHelper.TABLE_USERS)
                .whereArgs(
                    "(" + DocumentDatabaseOpenHelper.COLUMN_USER_NAME + "= {userName} ) AND ("
                            + DocumentDatabaseOpenHelper.COLUMN_USER_PASSWORD + " = {password} )",
                    "userName" to this.user.username,
                    "password" to this.user.password
                ).exec {
                    parseList(classParser<User>())
                }
            if (userList.isNotEmpty()) {
                return userList[0]
            }

            return null
        }

        private fun startMainActivity(user: User) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra(USER_DATA, user)
            startActivity(intent)
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private const val REQUEST_READ_CONTACTS = 0
        const val USER_DATA = "user_data_object"
        const val TAG = "ODE_LoginActivity"
    }
}
