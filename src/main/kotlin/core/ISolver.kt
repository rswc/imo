package org.example.core

interface ISolver<PROBLEM : IInstance> {

    fun solve(instance: PROBLEM, experimentStep: Int? = null) : ISolution

    fun getDisplayName(): String

}