package me.kristinasaigak.otus.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
        @Id
        @Column("id")
        var id: Int? = null,
        @Column("first_name")
        var first_name: String,
        @Column("second_name")
        var second_name: String,
        @Column("age")
        var age: Int,
        @Column("biography")
        var biography: String? = null,
        @Column("city")
        var city: String? = null,
        @Column("password")
        var password: String? = null
)