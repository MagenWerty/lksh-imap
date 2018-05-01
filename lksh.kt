/* Written by Safronov Valentin aka SVDouble in 2018 as an introductory work for LKSH camp*/

fun MutableMap<String, String>.interact() {
    /* Function for filtering map by key / value */
    fun mask(key: String, value: String): Map<String, String> =
        if (key == "_" && value == "_")
            this
        else if (value == "_")
            this.filterKeys { it.contains(key) }
        else if (key == "_")
            this.filterValues { it.contains(value) }
        else
            this.filterKeys { it.contains(key) }.filterValues { it.contains(value) }

    /* UI */
    println("Type 'help' to see usage")
    while (true) {
        print(">> ")
        val asw = readLine()
        try {
            if (!(asw.isNullOrEmpty() || asw.isNullOrBlank()))
                when (asw!!.split(' ')[0]) {
                    "help" -> println("""
                                    Commands:
                                    ' help                       ' - call help
                                    ' exit                       ' - exit
                                    ' print                      ' - show map
                                    ' clear                      ' - clear map
                                    ' add  [key]     [value]     ' - add element
                                    ' del  [key / _] [value / _] ' - delete by key or value
                                    ' find [key / _] [value / _] ' - find by key or value fragment
                                    Use underscore to omit one or more arguments.
                                    If you want to use underscore as a key / value part write '\_' instead of '_'
                                            """.trimIndent())
                    "exit" -> return
                    "print" -> {
                        if (this.isEmpty())
                            println("< empty >")
                        else
                            this.forEach { println("'${it.key.replace("\\_", "_")}' -> '${it.value.replace("\\_", "_")}'") }
                    }
                    "add" -> {
                        val (key, value) = asw.split(' ').subList(1, 3)
                        if (key == "_" || value == "_")
                            println("Error! Use '\\_' instead of '_'")
                        if (this.containsKey(key))
                            println("Already in the map!")
                        else
                            this[key] = value
                    }
                    "del" -> {
                        val (key, value) = asw.split(' ').subList(1, 3)
                        val del = mask(key, value)
                        if (del.isEmpty())
                            println("No values to delete exist!")
                        else if (this.size == del.size)
                            this.clear()
                        else
                            del.forEach { this.remove(it.key, it.value) }
                    }
                    "find" -> {
                        val (key, value) = asw.split(' ').subList(1, 3)
                        val res = mask(key, value)
                        println("${res.size} element${if (res.size == 1) "" else "s"} found${if (res.isEmpty()) '.' else ':'}")
                        res.forEach { println("'${it.key.replace("\\_", "_")}' -> '${it.value.replace("\\_", "_")}'") }
                    }
                    "clear" -> this.clear()
                    else -> throw IllegalArgumentException("Unexpected token found!")
                }
        } catch (e: RuntimeException) {
            when (e) {
                is IllegalArgumentException -> println(e.message)
                is NullPointerException -> println("Wrong input!")
                else -> println("Unrecognized error occurred!")
            }
            continue
        }
    }
}

fun main(args: Array<String>) {
    val test: MutableMap<String, String> = mutableMapOf()
    test.interact()
}
