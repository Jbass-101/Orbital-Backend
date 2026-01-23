package com.jbass.util


import java.util.UUID

/**Generic Id Generator*/
object IdGenerator {
    fun newId(): String = UUID.randomUUID().toString()
}
