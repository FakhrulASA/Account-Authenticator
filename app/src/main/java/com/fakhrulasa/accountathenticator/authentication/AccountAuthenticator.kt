package com.fakhrulasa.accountathenticator.authentication

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fakhrulasa.accountathenticator.LoginActivity
import com.fakhrulasa.accountathenticator.MainActivity
import com.fakhrulasa.accountathenticator.authjava.AccountHelper

/**
 * Class Name: AccountAuthenticator
 * Description: Class that extends AbstractAccountAuthenticator
 */
class AccountAuthenticator     //endregion
//region Constructor
    (  //region Private Variables
    private val _context: Context
) :
    AbstractAccountAuthenticator(_context) {
    //endregion
    //region Overrides
    /**
     * Called from the system to add an account of the specified accountType.
     * Invokes the Application's registration UI, which will create the account explicitly.
     * @param response The response to send the result back to the AccountManager, will never be null.
     * @param accountType The type of account to add, will never be null (defined in authenticator.xml).
     * @param authTokenType The type of auth token to retrieve after adding the account, may be null.
     * @param requiredFeatures A String array of authenticator-specific features that the added account must support, may be null.
     * @param options A Bundle of authenticator-specific options, may be null.
     * @return a Bundle result or null if the result is to be returned via the response. The result will contain either:
     * KEY_INTENT, or
     * KEY_ACCOUNT_NAME and KEY_ACCOUNT_TYPE of the account that was added, or
     * KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error
     * @throws NetworkErrorException
     */
    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val METHOD_TAG: String
        METHOD_TAG = CLASS_TAG + ".addAccount()"
        val intent: Intent
        val bundle: Bundle
        intent = Intent(_context, MainActivity::class.java)
        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, accountType)
        intent.putExtra(AccountHelper.ARG_AUTH_TYPE, authTokenType)
        intent.putExtra(AccountHelper.ARG_IS_ADDING_NEW_ACCOUNT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    /**
     * Checks that the user knows the credentials of an account.
     * @param response The response to send the result back to the AccountManager, will never be null.
     * @param account The account whose credentials are to be checked, will never be null.
     * @param options A Bundle of authenticator-specific options, may be null.  If verifying a password, must contain AccountManager.KEY_PASSWORD.
     * @return A Bundle result or null if the result is to be returned via the response. The result will contain either:
     * KEY_INTENT, or
     * KEY_BOOLEAN_RESULT, true if the check succeeded, false otherwise
     * KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error
     * @throws NetworkErrorException
     */
    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle
    ): Bundle {
        val METHOD_TAG: String
        METHOD_TAG = CLASS_TAG + ".confirmCredentials()"
        if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
            val passwordEntered: String?
            passwordEntered = options.getString(AccountManager.KEY_PASSWORD)
            val accountPassword: String
            accountPassword = AccountHelper.getInstance(_context).getPassword(account)
            val bValid: Boolean
            bValid = passwordEntered == accountPassword
            val bundle: Bundle
            bundle = Bundle()
            bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, bValid)
            return bundle
        }

        // Launch UserLogin to confirm credentials. This will be the case if confirmCredentials is called from elsewhere besides the UserLogin Activity
        val intent: Intent
        intent = Intent(_context, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(AccountHelper.ARG_ACCOUNT_NAME, account.name)
        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, account.type)
        intent.putExtra(AccountHelper.ARG_CONFIRMCREDENTIALS, true)
        val bundle: Bundle
        bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle {
        val METHOD_TAG: String
        METHOD_TAG = CLASS_TAG + ".getAuthToken()"
        val authToken = AccountHelper.getInstance(_context).peekAuthToken(account, authTokenType)

        // If we get an authToken - we return it
        if (authToken != null && authToken.length != 0) {
            val result: Bundle
            result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our UserLogin Activity.

        // TODO: Take the getAuthToken method call out of the AsyncTask so the system can start this Activity on the main thread.
//        final Intent intent;
//        intent = new Intent(_context, UserLogin.class);
//
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//        intent.putExtra(AccountHelper.ARG_ACCOUNT_NAME, account.name);
//        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, account.type);
//        intent.putExtra(AccountHelper.ARG_AUTH_TYPE, authTokenType);
        val bundle: Bundle
        bundle = Bundle()

//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? {
        val METHOD_TAG: String
        METHOD_TAG = CLASS_TAG + ".editProperties()"
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        val METHOD_TAG: String = CLASS_TAG + ".getAuthTokenLabel()"
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle? {
        val METHOD_TAG: String = "$CLASS_TAG.updateCredentials()"
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<String>
    ): Bundle? {
        val METHOD_TAG: String = CLASS_TAG + ".hasFeatures()"
        return null
    } //endregion

    companion object {
        //endregion
        //region Constants
        const val CLASS_TAG = "AccountAuthenticator"
    }
}