# Domino
Descripción

Domino es un juego desarrollado para la materia Programación Orientada a Objetos de la Universidad Nacional de Luján. Este proyecto fue creado desde cero, utilizando el patrón Observer para reflejar los cambios realizados en el modelo en la vista del usuario. Además, la estructura del código se organizó siguiendo el patrón Model-View-Controller (MVC) para mantener una organización clara y eficiente del código.

El juego permite jugar con dos "vistas" distintas: una vista de consola simulada con Swing y una vista gráfica, también utilizando la librería Swing.

# Características
* Número de Jugadores: 2 a 4 jugadores.
* Modo de Juego: Online (con un servidor, no soporta multipartidas).
* Persistencia: Permite guardar el estado de los juegos por medio de RMI, así como también mantener un ranking top 5 de los mejores jugadores históricos del juego.

# ¿Cómo ejecutar la aplicación?
Se debe ejecutar primero la AppServidor y luego levantar tantos AppCliente como se especifique en el juego, e.g si son dos jugadores, se necesitará ejecutar 2 veces la aplicación AppCliente (se debe habilitar la opción de multiples instancias si se usa IntelliJ para poder ejecutar más de una instancia de la misma aplicación).


## Screenshots

![App Screenshot](https://github.com/ochoaFranco/Domino-Java/pictures/game.png)
![App Screenshot](https://github.com/ochoaFranco/Domino-Java/pictures/login.png)
![App Screenshot](https://github.com/ochoaFranco/Domino-Java/pictures/ranking.png)


## 🛠 Skills y patrones de diseño
Java - Swing- Git - MVC - Observer

