package com.example.trekkly.data.mapper

import com.example.trekkly.domain.model.User
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Firestore document shape for users/{uid}.
 *
 * Fields are read/written through explicit maps rather than Firestore's POJO
 * serializer, because Kotlin `isX` boolean properties get renamed to `x` by the
 * automatic mapper and silently break round-tripping.
 */
object UserFields {
    const val UID = "uid"
    const val NAME = "name"
    const val PHONE_NUMBER = "phoneNumber"
    const val EMAIL = "email"
    const val PROFILE_IMAGE_URL = "profileImageUrl"
    const val IS_PROFILE_COMPLETE = "isProfileComplete"
    const val CREATED_AT = "createdAt"
    const val UPDATED_AT = "updatedAt"
}

fun User.toFirestoreMap(): Map<String, Any?> = mapOf(
    UserFields.UID to uid,
    UserFields.NAME to name,
    UserFields.PHONE_NUMBER to phoneNumber,
    UserFields.EMAIL to email,
    UserFields.PROFILE_IMAGE_URL to profileImageUrl,
    UserFields.IS_PROFILE_COMPLETE to isProfileComplete
)

/** Returns null when the document does not exist, so callers can treat it as "no account". */
fun DocumentSnapshot.toUserOrNull(): User? {
    if (!exists()) return null
    val uid = getString(UserFields.UID) ?: id
    return User(
        uid = uid,
        name = getString(UserFields.NAME).orEmpty(),
        phoneNumber = getString(UserFields.PHONE_NUMBER).orEmpty(),
        email = getString(UserFields.EMAIL),
        profileImageUrl = getString(UserFields.PROFILE_IMAGE_URL),
        isProfileComplete = getBoolean(UserFields.IS_PROFILE_COMPLETE) ?: false
    )
}
