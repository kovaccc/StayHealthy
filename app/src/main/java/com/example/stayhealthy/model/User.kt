package com.example.stayhealthy.model


data class User(var id: String = "", var name: String = "", var gender: String = "", var age: Long = 0, var height: Long = 0, var weight: Long = 0, var activityLevel: String = ""){


    override fun toString(): String {
        return "${this.id}, ${this.name}, ${this.gender}, ${this.age}, ${this.height}, ${this.weight}, ${this.activityLevel}"
    }
}



