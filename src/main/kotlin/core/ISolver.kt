package org.example.core

interface ISolver<PROBLEM : IInstance> {

    fun solve(instance: PROBLEM) : ISolution

}