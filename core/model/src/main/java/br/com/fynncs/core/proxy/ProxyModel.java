package br.com.fynncs.core.proxy;

import br.com.fynncs.core.handler.RegisterAttributeModifiedHandler;
import org.springframework.cglib.proxy.Enhancer;

public abstract class ProxyModel {

    public static <T> T createProxy(T data) {
        Class<T> classType = (Class<T>) data.getClass();
        return (T) Enhancer.create(classType, new RegisterAttributeModifiedHandler<>(data));
    }
}
