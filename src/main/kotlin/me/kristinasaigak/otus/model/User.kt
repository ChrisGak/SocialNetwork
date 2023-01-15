package me.kristinasaigak.otus.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user")
data class User(
    @Id
    @Column("id")
    var id: String?,
    @Column("first_name")
    var first_name: String,
    @Column("second_name")
    var second_name: String,
    @Column("age")
    var age: Int,
    @Column("biography")
    var biography: String,
    @Column("city")
    var city: String,
    @Column("password")
    var password: String
)

//    : UserDetails {
//    private val roles: Set<GrantedAuthority> = HashSet()
//
//    override fun getAuthorities(): Set<GrantedAuthority> {
//        return roles
//    }
//
//    override fun getPassword(): String {
//        return "null"
//    }
//
//    override fun getUsername(): String {
//        return ""
//    }
//
//    override fun isAccountNonExpired(): Boolean {
//        return true
//    }
//
//    override fun isAccountNonLocked(): Boolean {
//        return false
//    }
//
//    override fun isCredentialsNonExpired(): Boolean {
//        return true
//    }
//
//    override fun isEnabled(): Boolean {
//        return true
//    }
//}
