package com.sybsuper.checker

class SolutionLoader {
    fun loadString(input: String): List<Comment> {
        val comments = mutableListOf<Comment>()
        if (input.isEmpty()) {
            comments.add(Comment("Solution input is empty", Severity.ERROR))
            return comments
        }

        val lines = input.lines()


        return comments
    }
}