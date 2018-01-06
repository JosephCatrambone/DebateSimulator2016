package com.josephcatrambone.debatesimulator.scenes

abstract class Scene {
	abstract fun render()
	abstract fun update(delta: Float)
	abstract fun dispose()
}