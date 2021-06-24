package com.example.stayhealthy.common.base

interface Persistable<DomainType> {
    fun asDomain(): DomainType
}

fun <T> List<Persistable<T>>.asDomain(): List<T> {
    val domainEntities = arrayListOf<T>()
    this.forEach { domainEntities.add(it.asDomain()) }
    return domainEntities
}