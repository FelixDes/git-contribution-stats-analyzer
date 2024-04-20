package gus.external.dto

import gus.external.User

class UserPair(a: User, b: User) {
    val set: Set<User> = hashSetOf(a, b)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPair

        return set == other.set
    }

    override fun hashCode(): Int {
        return set.hashCode()
    }

    override fun toString(): String {
        return "UserPair(user1=${set.first().name}, user2=${set.last().name})"
    }
}