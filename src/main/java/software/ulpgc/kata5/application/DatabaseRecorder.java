package software.ulpgc.kata5.application;

import software.ulpgc.kata5.architecture.io.Recorder;
import software.ulpgc.kata5.architecture.model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

public class DatabaseRecorder implements Recorder {
    private final Connection connection;
    private final PreparedStatement statement;

    public DatabaseRecorder(Connection connection) throws SQLException {
        this.connection = connection;
        this.createTableIfNotExists();
        this.statement = connection.prepareStatement("INSERT INTO movies (title, year, duration) VALUES (?, ?, ?)");
    }

    private void createTableIfNotExists() throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS movies (title String, year INTEGER, duration INTEGER)");
    }

    @Override
    public void record(Stream<Movie> movies) {
        try {
            movies.forEach(this::record);
            flush();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int count = 0;
    private void record(Movie movie) {
        try {
            write(movie);
            flushIfNeeded();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(Movie movie) throws SQLException {
        statement.setString(1, movie.title());
        statement.setInt(2, movie.year());
        statement.setInt(3, movie.duration());
        statement.addBatch();
    }

    private void flushIfNeeded() throws SQLException {
        if(mustFlush()) flush();
    }

    private boolean mustFlush() {
        return ++count % 10_000 == 0;
    }

    private void flush() throws SQLException {
        statement.executeBatch();
    }
}
