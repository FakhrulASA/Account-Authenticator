package com.fakhrulasa.accountathenticator.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fakhrulasa.accountathenticator.authentication.AccountAuthenticator

/**
 * Class Name: AuthenticationService
 * Description: The AuthenticationService is the Authenticator Service for the application.
 */
class AuthenticationService : Service() {
    //region Overrides
    override fun onBind(intent: Intent): IBinder? {
        val accountAuthenticator = AccountAuthenticator(this)
        return accountAuthenticator.iBinder
    } //endregion
}