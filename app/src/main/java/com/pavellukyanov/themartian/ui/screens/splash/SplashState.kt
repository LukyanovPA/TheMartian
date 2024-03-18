package com.pavellukyanov.themartian.ui.screens.splash

import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

class SplashState : State()
object SplashAction : Action()
data class SplashEffect(val state: Boolean = false) : Effect()