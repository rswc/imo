package org.example.core

interface ISolver<PROBLEM> {

    fun solve(instance: PROBLEM) : ISolution

}