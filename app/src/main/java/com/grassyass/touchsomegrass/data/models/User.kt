package com.grassyass.touchsomegrass.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import kotlin.math.pow

@IgnoreExtraProperties
data class User(
    var name: String? = "",
    var exp: Double? = 0.0,
    var birthDate: Long? = null,
) : Serializable {

    private var _id: String = ""

    var id: String
        @Exclude
        get() { return _id }
        set(value) { _id = value}

    fun addExp(amount: Double) {
        exp = exp?.plus(amount)
    }

    @Exclude
    fun getLvl(): Int {
        for(lvl in 10 downTo 2) {
            val multiplier = 4.2
            val falloff = 0.5
            val baseCase = 0.57
            val ratio = multiplier.pow(lvl - 1) * falloff.pow(lvl - 2)
            val target = baseCase * ratio

            if (exp!! >= target) {
                return lvl
            }
        }

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
