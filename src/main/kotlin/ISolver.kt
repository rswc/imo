package org.example

interface ISolver<PROBLEM> {

    fun solve(instance: PROBLEM) : ISolution

}