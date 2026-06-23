package com.idiotfrogs.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface MSNavigator {
    fun navigate(routes: Routes)
    fun replace(routes: Routes)
    fun popBackStack()
    fun isLastRoutes(): Boolean
    fun clear()
}

class MSNavigatorImpl(private val backStack: NavBackStack<NavKey>) : MSNavigator {
    override fun navigate(routes: Routes) { backStack.add(routes) }
    override fun replace(routes: Routes) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = routes
        } else {
            backStack.add(routes)
        }
    }
    override fun popBackStack() { backStack.removeLastOrNull() }
    override fun isLastRoutes(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeLastOrNull() != null
        } else {
            false
        }
    }
    override fun clear() { backStack.clear() }
}

