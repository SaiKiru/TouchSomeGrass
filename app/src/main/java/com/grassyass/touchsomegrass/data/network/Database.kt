package com.grassyass.touchsomegrass.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Database {
    private const val dbURL: String = "https://touch-some-grass-default-rtdb.asia-southeast1.firebasedatabase.app"
    private var dbRef: DatabaseReference

    init {
        FirebaseDatabase.getInstance(dbURL).setPersistenceEnabled(true)
        dbRef = FirebaseDatabase.getInstance(dbURL).reference
    }

    fun writeData(path: String, data: Any): Task<Void> {
        return dbRef.child(path).setValue(data)
    }

    fun pushData(path: String, data: Any): Task<Void> {
        val key: String? = dbRef.child(path).push().key

        return writeData("$path/$key", data)
    }

    fun updateChildren(updates: Map<String, Any>): Task<Void> {
        return dbRef.updateChildren(updates)
    }

    fun readData(path: String): Task<DataSnapshot> {
        return dbRef.child(path).get()
    }
}