package com.example.book_slide.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.book_slide.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class SSORepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun silentSSO(context: Context): Result<FirebaseUser> = runCatching {
        executeGoogleCredentialRequest(context, filterByAuthorizedAccounts = true, autoSelectEnabled = true)
    }

    suspend fun interactiveSSO(context: Context): Result<FirebaseUser> = runCatching {
        executeGoogleCredentialRequest(context, filterByAuthorizedAccounts = false, autoSelectEnabled = false)
    }

    private suspend fun executeGoogleCredentialRequest(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): FirebaseUser {
        val credentialManager = CredentialManager.create(context)
        val hashedNonce = generateCryptoNonce()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(autoSelectEnabled)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = try {
            credentialManager.getCredential(request = request, context = context)
        } catch (c: GetCredentialCancellationException) {
            throw Exception("El usuario canceló el inicio de sesión.")
        } catch (e: GetCredentialException) {
            throw Exception("Fallo en Credential Manager: ${e.message}")
        }

        val credential = response.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            return authResult.user ?: throw IllegalStateException("Usuario no válido devuelto por Firebase.")
        } else {
            throw UnsupportedOperationException("Tipo de credencial no soportado: ${credential.type}")
        }
    }

    private fun generateCryptoNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
