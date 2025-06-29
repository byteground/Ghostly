package com.ghostly.network

enum class Endpoint(val path: String) {
    GET_ROLES("/api/admin/roles"),
    INVITE_USER("/api/admin/invites"),
    GET_USERS("/api/admin/users?include=count.posts%2Cpermissions%2Croles%2Croles.permissions"),
}