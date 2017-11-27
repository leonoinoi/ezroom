package com.leonoinoi.ezroom

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignIn(private val context: Context, private val callback: (message: String) -> Unit)
    : GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 741
    private val TAG = "EZROOM_DEBUG"

    fun signIn() {
        Log.d(TAG, "#SIGNIN")

        val id = context.getString(R.string.default_web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(id)
                .requestEmail()
                .build()

        if (O.GOOGLE_API_CLIENT == null) {
            O.GOOGLE_API_CLIENT = GoogleApiClient.Builder(context)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .enableAutoManage(context as FragmentActivity, this)
                    .build()
        }

        if (O.CREDENTIAL == null) {
            Log.d(TAG, "CREDENTIAL_NOT_READY")
            val intent = Auth.GoogleSignInApi.getSignInIntent(O.GOOGLE_API_CLIENT)
            (context as FragmentActivity).startActivityForResult(intent, RC_SIGN_IN)
        } else {
            Log.d(TAG, "CREDENTIAL_READY")
            firebaseSignIn()
        }
    }

    //cannot build google api client
    override fun onConnectionFailed(p0: ConnectionResult) {
        callback("Google API Client Connection Failed")
    }

    //respond from activity after intent to signin
    fun onActivityResult(requestCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "ON_ACTIVITY_RESULT")
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                Log.d(TAG, "GOOGLE_SIGNIN_SUCCESS")
                val account = result.signInAccount
                O.CREDENTIAL = GoogleAuthProvider.getCredential(account!!.idToken, null)
                firebaseSignIn()
            } else {
                callback("Google SignIn API Failed")
            }
        }
    }

    private fun firebaseSignIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.d(TAG, "FIREBASE_NOT_READY")
            FirebaseAuth.getInstance().signInWithCredential(O.CREDENTIAL!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback("SignIn Success")
                } else {
                    callback("Firebase SignIn Failed")
                }
            }
        } else {
            callback("SignIn Success")
        }
    }
}

