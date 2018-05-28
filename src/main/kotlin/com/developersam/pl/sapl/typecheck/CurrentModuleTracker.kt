package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpList
import com.developersam.fp.cons

/**
 * [CurrentModuleTracker] is a functional data structure that keeps track of the current module's
 * fully qualified name and the full stack of modules.
 */
internal data class CurrentModuleTracker(
        val fullyQualifiedName: String,
        val moduleStack: FpList<String>
) {

    /**
     * This constructor creates a tracker started at the top level with name [topLevelModuleName].
     */
    constructor(topLevelModuleName: String) : this(
            fullyQualifiedName = topLevelModuleName,
            moduleStack = FpList.singleton(data = topLevelModuleName)
    )

    /**
     * [enterSubModule] produces a new tracker that enters the sub-module with name [subModuleName].
     */
    fun enterSubModule(subModuleName: String): CurrentModuleTracker =
            CurrentModuleTracker(
                    fullyQualifiedName = "$fullyQualifiedName.$subModuleName",
                    moduleStack = subModuleName cons moduleStack
            )

    /**
     * [leaveSubModule] produces a new tracker that leaves the current sub-module and goes one level
     * up. The current tracker must not be the top level with only one module.
     *
     * @throws IllegalStateException if the current tracker is at the top level.
     */
    fun leaveSubModule(): CurrentModuleTracker = when (this.moduleStack) {
        FpList.Nil -> throw IllegalStateException()
        is FpList.Node<String> -> {
            val popped = moduleStack.data
            val remained = moduleStack.next
            when (remained) {
                is FpList.Nil -> throw IllegalStateException()
                is FpList.Node<String> -> CurrentModuleTracker(
                        fullyQualifiedName = fullyQualifiedName.substring(
                                startIndex = 0,
                                endIndex = fullyQualifiedName.length - popped.length - 1
                        ),
                        moduleStack = remained
                )
            }
        }
    }

}
