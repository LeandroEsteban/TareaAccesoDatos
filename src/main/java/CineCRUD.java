import java.sql.*;
import java.util.Scanner;

public class CineCRUD {
    private Connection conn;
    private Scanner scanner;

    public CineCRUD() {
        try {
            conn = DatabaseConnection.getConnection();
            scanner = new Scanner(System.in);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        while (true) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearFuncion();
                    break;
                case 2:
                    verFunciones();
                    break;
                case 3:
                    actualizarFuncion();
                    break;
                case 4:
                    eliminarFuncion();
                    break;
                case 5:
                    cerrarConexion();
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("Cine MOCKITA");
        System.out.println(" /\\_/\\");
        System.out.println("( o.o )");
        System.out.println(" > ^ <");
        System.out.println("---MENU---");
        System.out.println("1. Crear función");
        System.out.println("2. Ver cartelera");
        System.out.println("3. Actualizar función");
        System.out.println("4. Eliminar función");
        System.out.println("5. Salir");
        System.out.print("Elige una opción: ");
    }

    private void crearFuncion() {
        try {
            conn.setAutoCommit(false);

            mostrarPeliculasDisponibles();
            System.out.print("Ingrese el ID de la película: ");
            int movieId = scanner.nextInt();

            mostrarSalasDisponibles();
            System.out.print("Ingrese el ID de la sala: ");
            int roomId = scanner.nextInt();

            scanner.nextLine();
            System.out.print("Fecha y hora de inicio (YYYY-MM-DD HH:MM:SS): ");
            String startTime = scanner.nextLine();

            String lockSql = "SELECT * FROM horarios_de_proyeccion WHERE movie_id = ? AND room_id = ? AND start_time = ? FOR UPDATE";
            try (PreparedStatement lockStmt = conn.prepareStatement(lockSql)) {
                lockStmt.setInt(1, movieId);
                lockStmt.setInt(2, roomId);
                lockStmt.setTimestamp(3, Timestamp.valueOf(startTime));
                ResultSet rs = lockStmt.executeQuery();
            }

            String sql = "INSERT INTO horarios_de_proyeccion (movie_id, room_id, start_time) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, movieId);
                pstmt.setInt(2, roomId);
                pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Función creada exitosamente.");
                }
            }

            conn.commit(); // Confirma la transacción si todo está bien
        } catch (SQLException e) {
            try {
                conn.rollback(); // Revierte la transacción en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Error al crear la función: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true); // Restaura el modo de autocommit
            } catch (SQLException autoCommitEx) {
                System.out.println("Error al restaurar autocommit: " + autoCommitEx.getMessage());
            }
        }
    }


    private void mostrarPeliculasDisponibles() throws SQLException {
        String sql = "SELECT movie_id, title FROM peliculas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nPelículas disponibles:");
            while (rs.next()) {
                System.out.printf("%d - %s\n", rs.getInt("movie_id"), rs.getString("title"));
            }
            System.out.println();
        }
    }

    private void mostrarSalasDisponibles() throws SQLException {
        String sql = "SELECT room_id, capacity FROM salas_de_cine";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nSalas disponibles:");
            while (rs.next()) {
                System.out.printf("%d - Capacidad: %d\n", rs.getInt("room_id"), rs.getInt("capacity"));
            }
            System.out.println();
        }
    }

    private void verFunciones() {
        String sql = "SELECT hp.schedule_id, p.title, sc.room_id, hp.start_time " +
                "FROM horarios_de_proyeccion hp " +
                "JOIN peliculas p ON hp.movie_id = p.movie_id " +
                "JOIN salas_de_cine sc ON hp.room_id = sc.room_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID Función: %d | Película: %s | Sala: %d | Fecha y hora: %s\n",
                        rs.getInt("schedule_id"),
                        rs.getString("title"),
                        rs.getInt("room_id"),
                        rs.getTimestamp("start_time"));
            }
        } catch (SQLException e) {
            System.out.println("Error al ver las funciones: " + e.getMessage());
        }
    }

    private void mostrarFuncionesDisponibles() throws SQLException {
        String sql = "SELECT hp.schedule_id, p.title, hp.room_id, hp.start_time " +
                "FROM horarios_de_proyeccion hp " +
                "JOIN peliculas p ON hp.movie_id = p.movie_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nFunciones disponibles:");
            while (rs.next()) {
                System.out.printf("ID Función: %d, Película: %s, Sala ID: %d, Fecha y hora: %s\n",
                        rs.getInt("schedule_id"),
                        rs.getString("title"),
                        rs.getInt("room_id"),
                        rs.getTimestamp("start_time"));
            }
            System.out.println();
        }
    }

    private void actualizarFuncion() {
        try {
            conn.setAutoCommit(false); // Inicia la transacción

            mostrarFuncionesDisponibles();
            System.out.print("ID de la función a actualizar: ");
            int scheduleId = scanner.nextInt();

            // Adquirir un bloqueo en la fila específica que se va a actualizar
            String lockSql = "SELECT * FROM horarios_de_proyeccion WHERE schedule_id = ? FOR UPDATE";
            try (PreparedStatement lockStmt = conn.prepareStatement(lockSql)) {
                lockStmt.setInt(1, scheduleId);
                ResultSet rs = lockStmt.executeQuery();
                // No es necesario procesar el ResultSet, pero ejecutamos esta consulta para adquirir el bloqueo
            }

            mostrarPeliculasDisponibles();
            System.out.print("Nuevo ID de película: ");
            int newMovieId = scanner.nextInt();

            mostrarSalasDisponibles();
            System.out.print("Nuevo ID de sala: ");
            int newRoomId = scanner.nextInt();

            scanner.nextLine(); // Consumir el salto de línea
            System.out.print("Nueva fecha y hora (YYYY-MM-DD HH:MM:SS): ");
            String newStartTime = scanner.nextLine();

            String sql = "UPDATE horarios_de_proyeccion SET movie_id = ?, room_id = ?, start_time = ? WHERE schedule_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, newMovieId);
                pstmt.setInt(2, newRoomId);
                pstmt.setTimestamp(3, Timestamp.valueOf(newStartTime));
                pstmt.setInt(4, scheduleId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Función actualizada exitosamente.");
                } else {
                    System.out.println("No se encontró la función especificada.");
                }
            }

            conn.commit(); // Confirma la transacción si todo está bien
        } catch (SQLException e) {
            try {
                conn.rollback(); // Revierte la transacción en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Error al actualizar la función: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true); // Restaura el modo de autocommit
            } catch (SQLException autoCommitEx) {
                System.out.println("Error al restaurar autocommit: " + autoCommitEx.getMessage());
            }
        }
    }


    private void eliminarFuncion() {
        try {
            conn.setAutoCommit(false);
            mostrarFuncionesDisponibles();
            System.out.print("ID de la función a eliminar: ");
            int scheduleId = scanner.nextInt();

            String lockSql = "SELECT * FROM horarios_de_proyeccion WHERE schedule_id = ? FOR UPDATE";
            try (PreparedStatement lockStmt = conn.prepareStatement(lockSql)) {
                lockStmt.setInt(1, scheduleId);
                ResultSet rs = lockStmt.executeQuery();
            }

            String sql = "DELETE FROM horarios_de_proyeccion WHERE schedule_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, scheduleId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Función eliminada exitosamente.");
                } else {
                    System.out.println("No se encontró la función especificada.");
                }
            }

            conn.commit(); // Confirma la transacción si todo está bien
        } catch (SQLException e) {
            try {
                conn.rollback(); // Revierte la transacción en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Error al eliminar la función: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true); // Restaura el modo de autocommit
            } catch (SQLException autoCommitEx) {
                System.out.println("Error al restaurar autocommit: " + autoCommitEx.getMessage());
            }
        }
    }


    private void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión cerrada. Adiós!");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CineCRUD app = new CineCRUD();
        app.run();
    }
}
