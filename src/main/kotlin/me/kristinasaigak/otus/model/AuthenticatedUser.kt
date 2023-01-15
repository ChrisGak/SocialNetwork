package me.kristinasaigak.otus.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.HashSet

class AuthenticatedUser(): UserDetails {
    private val roles: Set<GrantedAuthority> = HashSet()

    override fun getAuthorities(): Set<GrantedAuthority> {
        return roles
    }

    override fun getPassword(): String {
        return "null"
    }

    override fun getUsername(): String {
        return ""
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}