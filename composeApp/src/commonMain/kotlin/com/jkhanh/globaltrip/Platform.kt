package com.jkhanh.globaltrip

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform