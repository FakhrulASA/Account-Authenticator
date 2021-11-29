package com.fakhrulasa.accountathenticator.authjava;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fakhrulasa.accountathenticator.authentication.AccountAuthenticator;

/**
 * Class Name: AuthenticationService
 * Description: The AuthenticationService is the Authenticator Service for the application.
 */
public class AuthenticationService extends Service {
    public AuthenticationService() {
    }

    //region Overrides

    @Override
    public IBinder onBind(Intent intent) {

        AccountAuthenticator accountAuthenticator = new AccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }

    //endregion
}
