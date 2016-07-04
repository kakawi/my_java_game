package main;


import interfaces.serviceWithDIC;

import java.util.*;

public class DIC {
    private Map<Class<?>, Object> context = new HashMap<>();
    private List<Class<?>> servicesWithContext = new LinkedList<>();

    public void add(Class<?> clazz, Object object) throws Exception{
        if (context.containsKey(clazz)) {
            throw new Exception("Такой сервис уже есть");
        }
        context.put(clazz, object);
    }

    public void addServiceWithContext(Class<?> clazz, serviceWithDIC object) throws Exception{
        add(clazz, object);

        servicesWithContext.add(clazz);
    }

    public void start() {
        for (Class<?> clazz : servicesWithContext) {
            serviceWithDIC service = (serviceWithDIC) context.get(clazz);
            service.setDIC(this);
        }
    }

    public <T> T get(Class<T> clazz) {
        return (T) context.get(clazz);
    }
}
