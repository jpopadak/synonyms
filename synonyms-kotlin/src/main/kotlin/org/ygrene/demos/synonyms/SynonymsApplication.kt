package org.ygrene.demos.synonyms

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class SynonymsApplication

fun main(args: Array<String>) {
    SpringApplication.run(SynonymsApplication::class.java, *args)
}
