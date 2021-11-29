package com.fakhrulasa.accountathenticator.authentication

import android.accounts.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import java.io.IOException

/**
 * Class Name: AccountHelper.
 * Description: Singleton class containing common methods for working with Accounts.
 */
class AccountHelper private constructor(c: Context) {
    //region Private Variables
    private var _accountManager: AccountManager? = null
    private var _context: Context? = null
    //endregion
    //region Public Methods
    /**
     * Adds an account directly to the AccountManager.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     *
     * @param account  The Account to add.
     * @param password The password to associate with the account.
     * @param userdata String values to use for the account's userdata, null for none.
     * @return true if the Account was added, otherwise false.
     */
    fun addAccountExplicitly(account: Account?, password: String?, userdata: Bundle?): Boolean {

        val bAdded: Boolean = _accountManager!!.addAccountExplicitly(account, password, userdata)
        if (bAdded) {
            val toast = Toast.makeText(_context, "Acc created", Toast.LENGTH_LONG)
            toast.show()
        } else {
            val toast = Toast.makeText(_context, "Acc not created", Toast.LENGTH_LONG)
            toast.show()
        }
        return bAdded
    }

    /**
     * Determines if an Account has already been created on the device.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     *
     * @return true if an Account exists, otherwise false.
     */
    fun accountExists(): Boolean {
        val bAccountExists: Boolean
        val availableAccounts: Array<Account> = _accountManager!!.getAccountsByType(ACCOUNT_TYPE)
        bAccountExists = availableAccounts.isNotEmpty()
        if (bAccountExists) {
            //acc exist
        } else {
            //acc not found
        }
        return bAccountExists
    }

    /**
     * Gets the Account if one has been created.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     *
     * @return An Account instance if an Account is found, otherwise null.
     */
    @get:SuppressLint("LongLogTag")
    val account: Account?
        get() {

            val availableAccounts: Array<Account> = _accountManager!!.getAccountsByType(ACCOUNT_TYPE)
            return if (availableAccounts.isNotEmpty()) {
                val account: Account = availableAccounts[0]
                Log.d("Triggering Auth: ", "Account found. Name: " + account.name)
                account
            } else {
                Log.d("Triggering Auth: ", "Account not found.")
                null
            }
        }

    /**
     * Gets the password for a given Account.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     *
     * @param account The Account to get the password from.
     * @return The password for the Account if it is found, otherwise null.
     */
    @SuppressLint("LongLogTag")
    fun getPassword(account: Account): String? {

        Log.d("Triggering Auth: ", "Getting Password for Account: " + account.name)
        val password: String? = _accountManager!!.getPassword(account)
        if (password != null && password.isNotEmpty()) Log.d(
            "Triggering Auth: ",
            "Password found for Account: " + account.name
        ) else Log.d("Triggering Auth: ", "Password not found for Account: " + account.name)
        return password
    }

    /**
     * Determines if a given user name is valid.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     *
     * @param userName The user name to validate.
     * @return true if the user name is valid, otherwise false.
     */
    @SuppressLint("LongLogTag")
    fun isValidUser(userName: String): Boolean {

        Log.d("Triggering Auth: ", "Validating User Name: $userName")
        val account: Account? = this.account
        if (account == null) {

            // No Account.  Could have been deleted.  Should not happen, but if it does,
            // App will show UserRegistration Activity.
            Log.d("Triggering Auth: ", "No Accounts found.")
            val toast = Toast.makeText(_context, "No acc", Toast.LENGTH_SHORT)
            toast.show()
            return false
        }
        if (account.name != userName) {

            // No Account for the user name provided.
            Log.d("Triggering Auth: ", "No Account found for user: $userName")
            val toast = Toast.makeText(_context, "No acc for user", Toast.LENGTH_SHORT)
            toast.show()
            return false
        }
        Log.d("Triggering Auth: ", "User name: $userName is valid.")
        return true
    }

    /**
     * Gets an auth token of a specified type for an Account.
     * This method requires the caller to hold the permission USE_CREDENTIALS.
     * Note: This method cannot be called from the main thread.
     *
     * @param account       The account to fetch an auth token for.
     * @param authTokenType The auth token type, an authenticator-dependent string token, must not be null.
     * @param options       Authenticator-specific options for the request, may be null or empty.
     * @param activity      The Activity context to use for launching a new authenticator-defined sub-Activity to
     * prompt the user for a password if necessary; used only to call startActivity(); must not be null.
     * @param callback      Callback to invoke when the request completes, null for no callback.
     * @param handler       Handler identifying the callback thread, null for the main thread.     *
     * @return Bundle containing the results from the call. The Auth Token, if present is in the KEY_AUTHTOKEN field.
     */
    @SuppressLint("LongLogTag")
    fun getAuthToken(
        account: Account?,
        authTokenType: String?,
        options: Bundle?,
        activity: Activity?,
        callback: AccountManagerCallback<Bundle?>?,
        handler: Handler?
    ): Bundle? {

        Log.d("Triggering Auth: ", "Getting Auth Token.")

        //android.os.Debug.waitForDebugger();
        val future: AccountManagerFuture<Bundle>
        future = _accountManager!!.getAuthToken(
            account,
            authTokenType,
            options,
            activity,
            callback,
            handler
        )
        var result: Bundle? = null
        try {
            result = future.result
        } catch (e: OperationCanceledException) {
            Log.e("Triggering Auth: ", e.toString())
        } catch (e: IOException) {
            Log.e("Triggering Auth: ", e.toString())
        } catch (e: AuthenticatorException) {
            Log.e("Triggering Auth: ", e.toString())
        }
        return result
    }

    /**
     * Gets an Auth Token from the AccountManager's cache for a given Account type
     * and given Auth Token type.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     * Note: This method is intended to be used by the Authenticator only. The app should call getAuthToken().
     *
     * @param account       The Account to get the Auth Token from.
     * @param authTokenType The type of Auth Token to get.
     * @return The cached Auth Token for this Account and type, or null if no Auth Token is cached or the Account does not exist.
     */
    @SuppressLint("LongLogTag")
    fun peekAuthToken(account: Account, authTokenType: String): String? {

        Log.d(
            "Triggering Auth: ",
            "Attempting to get Auth Token type: " + authTokenType + " for Account: " + account.name
        )
        val authToken: String? = _accountManager!!.peekAuthToken(account, authTokenType)
        if (authToken != null && authToken.isNotEmpty()) Log.d(
            "Triggering Auth: ",
            "Auth Token type: " + authTokenType + " found for Account: " + account.name
        ) else Log.d(
            "Triggering Auth: ",
            "Auth Token type: " + authTokenType + " not found for Account: " + account.name
        )
        return authToken
    }

    /**
     * Adds an auth token to the AccountManager cache for an account.
     *
     * @param account       The account to set an auth token for.
     * @param authTokenType The type of the auth token.
     * @param authToken     The auth token to add to the cache.
     */
    fun setAuthToken(account: Account, authTokenType: String?, authToken: String?) {

        Log.d("Triggering Auth: ", "Attempting to set Auth Token for Account: " + account.name)
        _accountManager!!.setAuthToken(account, authTokenType, authToken)
        Log.d("Triggering Auth: ", "Auth Token set for Account: " + account.name)
    }

    /**
     * This method requires the caller to hold the permission MANAGE_ACCOUNTS or USE_CREDENTIALS.
     *
     * @param accountType The Account type of the Auth Token to invalidate, must not be null.
     * @param authToken   The Auth Token to invalidate, may be null.
     */
    @SuppressLint("LongLogTag")
    fun invalidateAuthToken(accountType: String?, authToken: String?) {

        Log.d("Triggering Auth: ", "Invalidating Auth Token.")
        _accountManager!!.invalidateAuthToken(accountType, authToken)
        Log.d("Triggering Auth: ", "Auth Token invalidated.")
    }

    /**
     * Confirms that the user knows the password for an account to make extra sure they are the owner
     * of the account.
     * This method requires the caller to hold the permission MANAGE_ACCOUNTS and GET_ACCOUNTS.
     *
     * @param password The password to validate.
     * @return An AccountManagerFuture which resolves to a Bundle.  The result will be in the KEY_BOOLEAN_RESULT field.
     */
    @SuppressLint("LongLogTag")
    fun validatePassword(password: String?): AccountManagerFuture<Bundle> {

        Log.d("Triggering Auth: ", "Confirming credentials with Account Manager.")
        val account: Account? = this.account
        val options: Bundle = Bundle()
        options.putString(AccountManager.KEY_PASSWORD, password)
        return _accountManager!!.confirmCredentials(account, options, null, null, null)
    } //endregion

    companion object {
        //endregion
        //region Constants
        const val CLASS_TAG = "AccountHelper"
        const val ARG_CONFIRMCREDENTIALS = "CONFIRMCREDENTIALS"

        /**
         * Account name
         */
        const val ARG_ACCOUNT_NAME = "ACCOUNT_NAME"

        /**
         * Account type id
         */
        const val ACCOUNT_TYPE = "com.fakhrulasa.accountmanagement"
        const val ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
        const val ARG_AUTH_TYPE = "AUTH_TYPE"
        const val ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT"

        /**
         * Auth Token
         */
        const val AUTH_TOKEN = "fakhrulasa_auth_token"

        /**
         * Auth token types
         */
        const val AUTHTOKEN_TYPE_READ_ONLY = "Read only"
        const val AUTHTOKEN_TYPE_READ_ONLY_LABEL =
            "Read only access to a com.fakhrulasa.accountathenticator account"
        const val AUTHTOKEN_TYPE_FULL_ACCESS = "Full access"
        const val AUTHTOKEN_TYPE_FULL_ACCESS_LABEL =
            "Full access to a com.fakhrulasa.accountathenticator account"

        //endregion
        //region Singleton Implementation (not thread safe)
        private var _instance: AccountHelper? = null

        /**
         * Singleton accessor.
         *
         * @param c Application Context
         * @return Single instance of AccountHelper
         */
        fun getInstance(c: Context): AccountHelper? {
            if (_instance == null) {
                _instance = AccountHelper(c)
            }
            return _instance
        }
    }
    // Private constructor to prevent instantiation.
    /**
     * Constructor: Private to prevent instantiation.
     *
     * @param c Application Context
     */
    init {
        _accountManager = AccountManager.get(c)
        _context = c
    }
}