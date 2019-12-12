package nu.mine.mosher.afl.syntax;



import java.util.Objects;



public class ID {
    private final String id;

    public ID(final String id) {
        this.id = Objects.requireNonNull(id);
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof ID)) {
            return false;
        }
        final ID that = (ID)object;
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
