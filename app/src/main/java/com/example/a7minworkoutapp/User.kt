package com.example.a7minworkoutapp

data class User(
    var username:String,
    var emailId:String,
    var password:String,
    var isLoggedIn:Boolean = false
)