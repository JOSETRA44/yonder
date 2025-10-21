package com.josetra.yonder.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleAuthHelper {
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("TU_ID_DE_CLIENTE_WEB")  // Reemplaza esto
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}