package software.ulpgc.kata5.application;

import software.ulpgc.kata5.architecture.model.Movie;
import software.ulpgc.kata5.architecture.viewmodel.Histogram;
import software.ulpgc.kata5.architecture.viewmodel.HistogramBuilder;

import javax.swing.table.TableRowSorter;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:movies.db")) {
            connection.setAutoCommit(false);
            importIfNeededInto(connection);
            Desktop.create(new DatabaseStore(connection))
                    .display()
                    .setVisible(true);
        }
    }

    private static void importIfNeededInto(Connection connection) throws SQLException {
        if(new File("movies.db").length() > 0) {return;}
        Stream<Movie> movies = new RemoteStore(MovieDeserializer::fromTsv).movies();
        new DatabaseRecorder(connection).record(movies);
    }
}