package messageSystem;

import java.util.concurrent.atomic.AtomicInteger;

public class AddressImpl implements Address{
    private static AtomicInteger abonentIdCreator = new AtomicInteger();
    private final int abonentId;

    public AddressImpl() {
        this.abonentId = abonentIdCreator.incrementAndGet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressImpl address = (AddressImpl) o;

        return abonentId == address.abonentId;

    }

    @Override
    public int hashCode() {
        return abonentId;
    }
}
