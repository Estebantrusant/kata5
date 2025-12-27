package software.ulpgc.kata5.application;

import software.ulpgc.kata5.architecture.io.Store;
import software.ulpgc.kata5.architecture.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class RemoteStore implements Store {
    private final Function<String, Movie> deserialize;
    private static final String RemoteUrl = "https://datasets.imdbws.com/title.basics.tsv.gz";

    public RemoteStore(Function<String, Movie> deserialize) {
        this.deserialize = deserialize;
    }

    @Override
    public Stream<Movie> movies() {
        try {
            return loadAllFrom(new URL(RemoteUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> loadAllFrom(URL url) throws IOException {
        return loadAllFrom(url.openConnection());
    }

    private Stream<Movie> loadAllFrom(URLConnection connection) throws IOException {
        return loadAllFrom(unzip(connection.getInputStream()));
    }

    private InputStream unzip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(inputStream));
    }

    private Stream<Movie> loadAllFrom(InputStream is) throws IOException {
        return loadFrom(toReader(is));
    }

    private Stream<Movie> loadFrom(BufferedReader reader) throws IOException {
        return reader.lines().skip(1).map(deserialize);
    }

    private BufferedReader toReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
