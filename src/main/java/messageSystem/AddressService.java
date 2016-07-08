package messageSystem;

import java.util.HashMap;
import java.util.Map;

public class AddressService {
    private final Map<Class<?>, Address> services = new HashMap<>();

    public void addService(Class<?> clazz, Abonent service) {
        services.put(clazz, service.getAddress());
    }

    public Address getServiceAddress(Class<?> clazz) {
        return services.get(clazz);
    }
}
