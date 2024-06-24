-- Database: Cine

-- DROP DATABASE IF EXISTS "Cine";

CREATE DATABASE "Cine"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Chile.1252'
    LC_CTYPE = 'Spanish_Chile.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
-- Database: Cine

-- DROP DATABASE IF EXISTS "Cine";

CREATE DATABASE "Cine"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Chile.1252'
    LC_CTYPE = 'Spanish_Chile.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
-- Crear la tabla de clasificaciones
CREATE TABLE clasificaciones (
    clasificacion VARCHAR(10) PRIMARY KEY,
    edad_minima INTEGER NOT NULL,
    restriccion VARCHAR(255) NOT NULL
);

-- Insertar las clasificaciones según la LSF
INSERT INTO clasificaciones (clasificacion, edad_minima, restriccion) VALUES
('SU', 0, 'Para todos los públicos'),
('A', 0, 'Contenido para adolescentes y niños'),
('BO', 0, 'Con supervisión de los padres'),
('R', 13, 'Contenido para adolescentes'),
('17', 17, 'Para mayores de 17 años'),
('D', 18, 'Contenido para adultos'),
('21', 21, 'Para mayores de 21 años');

-- Crear la tabla de películas
CREATE TABLE peliculas (
    movie_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    duration INTEGER NOT NULL,
    clasificacion VARCHAR(10) REFERENCES clasificaciones(clasificacion)
);

-- Crear la tabla de salas de cine
CREATE TABLE salas_de_cine (
    room_id SERIAL PRIMARY KEY,
    capacity INTEGER NOT NULL
);

-- Crear la tabla de horarios de proyección
CREATE TABLE horarios_de_proyeccion (
    schedule_id SERIAL PRIMARY KEY,
    movie_id INTEGER REFERENCES peliculas(movie_id),
    room_id INTEGER REFERENCES salas_de_cine(room_id),
    start_time TIMESTAMP NOT NULL
);


--------- Insertar Datos ---------
-- Insertar datos en la tabla de películas
INSERT INTO peliculas (title, genre, duration, clasificacion) VALUES
('Toy Story', 'Animación', 81, 'A'),
('The Godfather', 'Crimen', 175, '17'),
('Inception', 'Ciencia Ficción', 148, 'R'),
('Pulp Fiction', 'Crimen', 154, '17'),
('The Dark Knight', 'Acción', 152, 'R');

-- Insertar datos en la tabla de salas de cine
INSERT INTO salas_de_cine (capacity) VALUES
(100),
(150),
(200);

-- Insertar datos en la tabla de horarios de proyección
INSERT INTO horarios_de_proyeccion (movie_id, room_id, start_time) VALUES
(1, 1, '2024-06-22 10:00:00'),
(2, 2, '2024-06-22 13:00:00'),
(3, 3, '2024-06-22 16:00:00'),
(4, 1, '2024-06-22 19:00:00'),
(5, 2, '2024-06-22 22:00:00');
