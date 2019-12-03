package nu.mine.mosher.afl.syntax;



import java.util.Objects;



public class AflId {
    private final String id;

    AflId(final String i) {
        this.id = Objects.requireNonNull(i);
        if (this.id.isEmpty()) {
            throw new IllegalStateException("AflId cannot be empty.");
        }
        if (this.id.contains("\n") || this.id.contains("\r")) {
            throw new IllegalStateException("AflId cannot contain newlines.");
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof AflId)) {
            return false;
        }
        final AflId that = (AflId)object;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id;
    }
}
