package me.kristinasaigak.otus.utils

import me.kristinasaigak.otus.model.AuthenticatedUser
import me.kristinasaigak.otus.model.User
import org.springframework.security.core.userdetails.UserDetails

//fun User.toAuthUser(): UserDetails {
//    return AuthenticatedUser().apply {
//        password: this.password
//        username = "$first_name $second_name"
//    }
//}