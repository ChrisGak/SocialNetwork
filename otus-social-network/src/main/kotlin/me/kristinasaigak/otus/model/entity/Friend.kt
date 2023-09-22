package me.kristinasaigak.otus.model.entity

import org.springframework.data.relational.core.mapping.Column

data class Friend (
        @Column("user_id")
        var userId: Int)