 package com.fakhrulasa.accountathenticator

import android.accounts.AccountManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Intent
import android.accounts.AccountAuthenticatorActivity

import android.accounts.Account
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.fakhrulasa.accountathenticator.authentication.AccountHelper

 class MainActivity : AppCompatActivity() {
    lateinit var userNameET:EditText
    lateinit var buttonGO:Button
    lateinit var passwordET:EditText
    var authToken="29012A"
     val ARG_ACCOUNT_TYPE = "accountType"
     val ARG_AUTH_TOKEN_TYPE = "authTokenType"
     val ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount"
     val PARAM_USER_PASSWORD = "password"
     private lateinit var mAccountManager: AccountManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonGO=findViewById(R.id.button)
        userNameET=findViewById(R.id.editTextTextPersonUserName)
        passwordET=findViewById(R.id.editTextTextPersonUserPassword)
        buttonGO.setOnClickListener {
            goButton()
        }

    }

     //endregion
     //region Event Handlers
     fun goButton() {


         // Check to see if an Account has already been created.  If so, notify the user and return.
         // This check is here in the event the UI is presented via the Settings applet.
         if (AccountHelper.getInstance(this)?.accountExists() == true) {
             val toast: Toast = Toast.makeText(
                 this,
                 "Already exist",
                 Toast.LENGTH_SHORT
             )
             toast.show()
             return
         }

         //
         // Validate the user name.
         //

         val newUserName: String=userNameET.text.toString()

         val password: String =passwordET.text.toString()

         val intent: Intent? = createAccount(newUserName, password)
         if (intent != null) {
             finish()
             this.startActivity(intent)

             // TODO: If this Activity was launched via the Settings applet via the AuthenticationService,
             // Then we should call finish() here and exit the app UI.  Otherwise, get the token, and go to the
             // Application Home screen.
             // this.finish();
         }
         return
     }

     //endregion

     //region Private Methods

     //endregion
     //region Private Methods
     /**
      * Creates a new Account in the Android Account Manager
      * @param userName The User Name.
      * @param password The Password.
      * @return A new Intent that can be used to start the App Home Activity.
      */
     private fun createAccount(userName: String, password: String): Intent? {

         // Set the Account Type string.
         var accountType: String?
         accountType = this.intent.getStringExtra(AccountHelper.ARG_ACCOUNT_TYPE)
         if (accountType == null || accountType.length == 0) {
             accountType = AccountHelper.ACCOUNT_TYPE
         }

         // Add the Account to the Android Account Manager.
         Log.d(
             "Validating :",
             "Adding Account $userName (Explicitly) to Android Account Manager."
         )
         val account: Account = Account(userName, accountType)
         return if (AccountHelper.getInstance(this)
                 ?.addAccountExplicitly(account, password, null) == true
         ) {
             Log.d("Validating :", "Account: $userName added to Android Account Manager.")

             // Set the Auth Token
             var authTokenType: String?
             authTokenType = intent.getStringExtra(AccountHelper.ARG_AUTH_TYPE)
             if (authTokenType == null || authTokenType.length == 0) authTokenType =
                 AccountHelper.AUTHTOKEN_TYPE_FULL_ACCESS
             AccountHelper.getInstance(this)
                 ?.setAuthToken(account, authTokenType, AccountHelper.AUTH_TOKEN)

             // Create the Intent to start the Application Home Activity
             val intent: Intent
             intent = Intent(this, LoginActivity::class.java)
             intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName)
             intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
             this.setResult(RESULT_OK, intent)
             intent
         } else {

             // Failed to add account.
             Log.e("Validating :", "Account $userName was not added to Android Account Manager.")
             null
         }
     }
 }