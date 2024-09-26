package com.example.snake

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// Esta clase representa la vista del juego donde se dibuja la serpiente y la comida
class SnakeGameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()  // Herramienta de dibujo
    private val snakeGameEngine = SnakeGameEngine(15, 25) // Lógica del juego con una matriz de 20x20
    private val handler = Handler(Looper.getMainLooper()) // Handler para el movimiento automático
    private val updateInterval = 230L  // Tiempo en milisegundos para mover la serpiente (0.3 segundos)

    init {
        paint.color = Color.BLACK // Configurar el color de la serpiente
        startGame() // Iniciar el juego
    }

    // Iniciar el movimiento automático de la serpiente
    private fun startGame() {
        // Usar un Runnable para actualizar el juego cada cierto tiempo
        handler.postDelayed(object : Runnable {
            override fun run() {
                snakeGameEngine.updateGame() // Actualiza el estado del juego (mover la serpiente)

                if (snakeGameEngine.isGameOver) {
                    showGameOverDialog() // Mostrar diálogo de "Juego Terminado" si la serpiente muere
                } else {
                    invalidate() // Redibujar la vista (actualiza la pantalla)
                    handler.postDelayed(this, updateInterval) // Repetir la actualización cada 300ms
                }
            }
        }, updateInterval)
    }

    // Método que dibuja la serpiente y la comida en la pantalla
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calcular el tamaño de cada celda en función del tamaño de la pantalla
        val cellWidth = width / snakeGameEngine.width
        val cellHeight = height / snakeGameEngine.height

        // Dibujar cada parte del cuerpo de la serpiente
        for (part in snakeGameEngine.snake) {
            canvas.drawRect(
                part.x * cellWidth.toFloat(),
                part.y * cellHeight.toFloat(),
                (part.x + 1) * cellWidth.toFloat(),
                (part.y + 1) * cellHeight.toFloat(),
                paint
            )
        }

        // Cambiar el color para dibujar la comida
        paint.color = Color.RED
        canvas.drawRect(
            snakeGameEngine.food.x * cellWidth.toFloat(),
            snakeGameEngine.food.y * cellHeight.toFloat(),
            (snakeGameEngine.food.x + 1) * cellWidth.toFloat(),
            (snakeGameEngine.food.y + 1) * cellHeight.toFloat(),
            paint
        )
        // Restaurar el color de la serpiente
        paint.color = Color.BLACK
    }

    // Método que detecta toques en la pantalla para cambiar la dirección de la serpiente
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val screenWidth = width
            val screenHeight = height

            // Dividir la pantalla en 4 áreas según la imagen
            val leftArea = screenWidth / 3
            val rightArea = 2 * screenWidth / 3
            val topArea = screenHeight / 4
            val bottomArea = 3 * screenHeight / 4

            // Lógica de los toques según la imagen proporcionada
            when {
                // Área superior (ARRIBA)
                y < topArea.toFloat() -> {
                    snakeGameEngine.setDirection(Direction.UP)
                }
                // Área inferior (ABAJO)
                y > bottomArea.toFloat() -> {
                    snakeGameEngine.setDirection(Direction.DOWN)
                }
                // Área izquierda (IZQUIERDA)
                x < leftArea.toFloat() && y in topArea.toFloat()..bottomArea.toFloat() -> {
                    snakeGameEngine.setDirection(Direction.LEFT)
                }
                // Área derecha (DERECHA)
                x > rightArea.toFloat() && y in topArea.toFloat()..bottomArea.toFloat() -> {
                    snakeGameEngine.setDirection(Direction.RIGHT)
                }
            }
        }
        return true
    }


    // Mostrar un diálogo cuando el juego termine (cuando la serpiente muera)
    private fun showGameOverDialog() {
        AlertDialog.Builder(context).apply {
            setTitle("Perdiste por opa") // Título del diálogo
            setMessage("Que malo q sos !!!") // Mensaje que indica que el juego terminó
            setPositiveButton("De nuevo ") { _, _ ->
                restartGame() // Opción para reiniciar el juego
            }
            setNegativeButton("Terminar") { _, _ ->
                // Opción para salir del juego, finaliza la actividad principal
                (context as MainActivity).finish()
            }
            setCancelable(false) // No permitir que el diálogo se cierre sin elegir una opción
            show() // Mostrar el diálogo
        }
    }

    // Reiniciar el juego cuando el usuario elija "Reiniciar"
    private fun restartGame() {
        snakeGameEngine.resetGame() // Resetear el estado del juego
        startGame() // Iniciar el loop del movimiento nuevamente
        invalidate() // Redibujar la pantalla
    }
}

