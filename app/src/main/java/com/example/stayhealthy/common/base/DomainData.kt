package com.example.stayhealthy.common.base

interface DomainData <DatabaseType> {
    fun asDatabase(): DatabaseType
}

fun <T> List<DomainData<T>>.asDatabase(): List<T> {
    val dbEntities = arrayListOf<T>()
    this.forEach { dbEntities.add(it.asDatabase()) }
    return dbEntities
}
