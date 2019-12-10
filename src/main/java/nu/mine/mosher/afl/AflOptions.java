package nu.mine.mosher.afl;



import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AflOptions {
    public List<URL> input = new ArrayList<>();
    public boolean intermediate;
    public boolean force;

    public void __(Optional<String> s) throws IOException {
        this.input.add(asUrl(s.get()));
    }

    public void x(Optional<String> s) {
        intermediate = true;
    }

    public void force(Optional<String> s) {
        force = true;
    }

    private static URL asUrl(final String pathOrUrl) throws IOException {
        Throwable urlExcept ;
        try {
            return new URI(pathOrUrl).toURL();
        } catch (final Throwable e) {
            urlExcept = e;
        }

        Throwable pathExcept ;
        try {
            return Paths.get(pathOrUrl).toUri().toURL();
        } catch (final Throwable e) {
            pathExcept = e;
        }

        final IOException except = new IOException("Invalid path or URL: "+pathOrUrl);
        except.addSuppressed(pathExcept);
        except.addSuppressed(urlExcept);
        throw except;
    }
}
