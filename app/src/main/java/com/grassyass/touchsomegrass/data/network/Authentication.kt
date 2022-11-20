package com.grassyass.touchsomegrass.data.network

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object Authentication {
    private val auth = Firebase.auth

    fun createAccount(email: String, password: String,
        onSuccess: OnSuccessListener<in AuthResult>? = null,
        onFailure: OnFailureListener? = null) {

        val task = auth.createUserWithEmailAndPassword(email, password)
        if (onSuccess != null) { task.addOnSuccessListener(onSuccess) }
        if (onFailure != null) { task.addOnFailureListener(onFailure)}
    }

    fun signIn(email: String, password: String,
        onSuccess: OnSuccessListener<in AuthResult>? = null,
        onFailure: OnFailureListener? = null) {

        val task = auth.signInWithEmailAndPassword(email, password)
        if (onSuccess != null) { task.addOnSuccessListener(onSuccess) }
        if (onFailure != null) { task.addOnFailureListener(onFailure) }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}