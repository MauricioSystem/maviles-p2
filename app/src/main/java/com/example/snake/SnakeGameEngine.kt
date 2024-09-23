package com.example.snake

import kotlin.random.Random

// Enumeración para las direcciones posibles de la serpiente
enum class Direction { UP, DOWN, LEFT, RIGHT }

// Clase que representa un punto (coordenada) en la matriz
data class Point(val x: Int, val y: Int)

// Esta clase maneja toda la lógica del juego de la serpiente
class SnakeGameEngine(val width: Int, val height: Int) {

    var snake = mutableListOf(Point(width / 2, height / 2)) // Inicializar la serpiente en el centro del tablero
    private var currentDirection = Direction.RIGHT // Dirección inicial de la serpiente
    var food: Point = generateFood() // Generar la comida en una posición aleatoria
    var isGameOver = false // Variable que indica si el juego terminó

    // Cambiar la dirección de la serpiente
    fun setDirection(newDirection: Direction) {
        if (canChangeDirectionTo(newDirection)) {
            currentDirection = newDirection
        }
    }

    // Verificar si la serpiente puede cambiar a una nueva dirección sin hacer un giro de 180 grados
    private fun canChangeDirectionTo(newDirection: Direction): Boolean {
        return when (newDirection) {
            Direction.UP -> currentDirection != Direction.DOWN
            Direction.DOWN -> currentDirection != Direction.UP
            Direction.LEFT -> currentDirection != Direction.RIGHT
            Direction.RIGHT -> currentDirection != Direction.LEFT
        }
    }

    // Actualizar el estado del juego (mover la serpiente)
    fun updateGame() {
        if (isGameOver) return // Si el juego terminó, no hacer nada

        // Obtener la cabeza actual de la serpiente
        val head = snake.first()

        // Determinar la nueva posición de la cabeza dependiendo de la dirección actual
        val newHead = when (currentDirection) {
            Direction.UP -> Point(head.x, (head.y - 1 + height) % height) // Mover hacia arriba
            Direction.DOWN -> Point(head.x, (head.y + 1) % height) // Mover hacia abajo
            Direction.LEFT -> Point((head.x - 1 + width) % width, head.y) // Mover hacia la izquierda
            Direction.RIGHT -> Point((head.x + 1) % width, head.y) // Mover hacia la derecha
        }

        // Verificar si la nueva cabeza colisiona con el cuerpo de la serpiente
        if (snake.contains(newHead)) {
            isGameOver = true // Si colisiona, el juego termina
            return
        }

        // Añadir la nueva cabeza a la serpiente
        snake.add(0, newHead)

        // Verificar si la serpiente ha comido la comida
        if (newHead == food) {
            food = generateFood() // Generar nueva comida si ha comido
        } else {
            // Si no ha comido, eliminar la cola (mantener el tamaño de la serpiente)
            snake.removeAt(snake.size - 1)
        }

        // Verificar si la serpiente ha ganado (si ocupa toda la matriz)
        if (snake.size == width * height) {
            isGameOver = true
        }
    }

    // Generar un nuevo punto de comida en una posición aleatoria que no esté ocupada por la serpiente
    private fun generateFood(): Point {
        var newFood: Point
        do {
            newFood = Point(Random.nextInt(width), Random.nextInt(height))
        } while (snake.contains(newFood)) // Asegurarse de que la comida no aparezca en el cuerpo de la serpiente
        return newFood
    }

    // Reiniciar el estado del juego
    fun resetGame() {
        snake = mutableListOf(Point(width / 2, height / 2)) // Reiniciar la serpiente en el centro
        currentDirection = Direction.RIGHT // Reiniciar la dirección a la derecha
        food = generateFood() // Generar nueva comida
        isGameOver = false // Reiniciar el estado de "Game Over"
    }
}
