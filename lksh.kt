/*
 * Written by Safronov Valentin as an introductory work for LKSH camp
 * Copyright (c) 2018 SVDouble
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
    // test standart map
    //val test: MutableMap<String, String> = mutableMapOf()]
    //test.interact()

    // test IMap
    val test = IMap()
    test.interact()
}
