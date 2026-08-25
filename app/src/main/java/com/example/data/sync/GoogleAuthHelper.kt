package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthHelper(private val context: Context) {
    suspend fun signInWithGoogle(webClientId: String): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            handleSignIn(result)
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Sign in failed", e)
            Result.failure(e)
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Result<String> {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                Result.success("Login sukses: ${authResult.user?.displayName ?: "User"}")
            } catch (e: Exception) {
                Log.e("GoogleAuth", "Firebase auth failed", e)
                Result.failure(e)
            }
        } else {
            return Result.failure(Exception("Tipe kredensial tidak valid."))
        }
    }
}
