package com.grassyass.touchsomegrass.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var name: String? = "",
    var exp: Int? = 0,
    var birthDate: Long? = null,
) {

    fun addExp(amount: Int) {
        exp = exp?.plus(amount)
    }

    @Exclude
    fun getLvl(): Int {
        // TODO: Implement getLvl() method
        return 1
    }

    @Exclude
    fun getTitle(): String {
        // TODO: Implement getTitle() method
        return ""
    }

    @Exclude
    fun getAge(): Int? {
        // TODO: Implement getAge() method
        return null
    }
}
