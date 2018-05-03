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

/* MAP based on BST */
class IMap {
    data class IEntry(val key: String, val value: String)

    private data class TreeNode(
            var key: String,
            var value: String,
            var parent: TreeNode? = null,
            var left: TreeNode? = null,
            var right: TreeNode? = null,
            var size: Int = 1)

    private var root: TreeNode? = null

    /* Public interfaces */

    operator fun get(key: String): String? {
        var e = root
        while (e != null)
            if (key < e.key)
                e = e.left
            else if (key > e.key)
                e = e.right
            else
                return e.value
        return null
    }

    operator fun set(key: String, value: String) {
        root = add(key, value, root)
    }

    val size: Int
        get() = size(root)

    val entries: MutableSet<IEntry>
        get() {
            val e = mutableSetOf<IEntry>()
            iterate(root) { e.add(IEntry(it.key, it.value))}
            return e
        }

    fun add(key: String, value: String) {
        root = add(key, value, root)
    }

    fun remove(key: String) {
        root = remove(key, root)
    }

    fun clear() {
        root = null
    }

    fun isEmpty() = size == 0

    /* Background */

    private fun add(key: String, value: String, e: TreeNode?): TreeNode? {
        if (e == null)
            return TreeNode(key, value)
        if (key < e.key)
            e.left = add(key, value, e.left)
        else if (key > e.key)
            e.right = add(key, value, e.right)
        else
            e.value = value
        e.size = size(e.left) + size(e.right) + 1
        return e
    }

    private fun remove(key: String, parent: TreeNode?): TreeNode? {
        var e: TreeNode = parent ?: throw NoSuchElementException()
        if (key < e.key)
            e.left = remove(key, e.left)
        else if (key > e.key)
            e.right = remove(key, e.right)
        else {
            if (e.left == null)
                return e.right
            if (e.right == null)
                return e.left
            val saved = e
            e = pollMin(e.right!!)!!
            e.right = min(saved.right)
            e.left = saved.left
        }
        e.size = size(e.left) + size(e.right) + 1
        return e
    }

    private fun size(e: TreeNode?): Int {
        if (e == null) return 0 else return e.size
    }

    private fun min(node: TreeNode?): TreeNode {
        if (node == null) throw NoSuchElementException()
        var e: TreeNode = node
        while (e.left != null) {
            e = e.left!!
        }
        return e
    }

    private fun pollMin(e: TreeNode): TreeNode? {
        if (e.left == null) return e.right
        e.left = pollMin(e.left!!)
        e.size = size(e.left) + size(e.right) + 1
        return e
    }

    private fun iterate(e: TreeNode?, predicate: (TreeNode) -> (Unit)) {
        if (e == null)
            return
        iterate(e.left, predicate)
        predicate(e)
        iterate(e.right, predicate)
    }
}


/* Additional utilities */
fun IMap.forEach(action: (IMap.IEntry) -> Unit) {
    for (element in this.entries) action(element)
}

fun IMap.filterKeys(predicate: (String) -> Boolean): IMap {
    val result = IMap()
    this.forEach { if (predicate(it.key)) result.add(it.key, it.value) }
    return result
}

fun IMap.filterValues(predicate: (String) -> Boolean): IMap {
    val result = IMap()
    this.forEach { if (predicate(it.value)) result.add(it.key, it.value) }
    return result
}


/* Interact function */
fun IMap.interact() {
    /* Function for filtering map by key / value */
    fun mask(key: String, value: String): IMap =
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
                            del.forEach { this.remove(it.key) }
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
