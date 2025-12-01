package com.idiotfrogs.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface MSNavigator {
    fun navigate(routes: Routes)
    fun popBackStack()
    fun isLastRoutes(): Boolean
}

class MSNavigatorImpl(private val backStack: NavBackStack<NavKey>) : MSNavigator {
    override fun navigate(routes: Routes) { backStack.add(routes) }
    override fun popBackStack() { backStack.removeLastOrNull() }
    override fun isLastRoutes(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeLastOrNull() != null
        } else {
            false
        }
    }
}

