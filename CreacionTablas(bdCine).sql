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
