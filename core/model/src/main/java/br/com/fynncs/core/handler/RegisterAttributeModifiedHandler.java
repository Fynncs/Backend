package br.com.fynncs.core.handler;

import br.com.fynncs.core.ModelState;
import br.com.fynncs.core.annotation.RegisterAttributeModified;
import br.com.fynncs.core.enums.State;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RegisterAttributeModifiedHandler<T> implements MethodInterceptor {

    private T data;

    public RegisterAttributeModifiedHandler(T data) {
        this.data = data;
    }

    @Override
    public Object intercept(Object data, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.isAnnotationPresent(RegisterAttributeModified.class)) {
            RegisterAttributeModified modified = method.getAnnotation(RegisterAttributeModified.class);
            addModifiedAttributes(modified);
        }
        return method.invoke(this.data, args);
    }

    private void addModifiedAttributes(RegisterAttributeModified modified) {
        ModelState<T> modelState = (ModelState<T>) cast(ModelState.class, this.data);
        if (modelState.getRegisterModifiedAttributes() && !(modelState.isDeleted() || modelState.isNew())) {
            modelState.addModifiedAttributes(modified.value());
            modelState.setState(State.MODIFIED);
        }
    }

    private Object cast(Class<?> clazz, Object value) {
        if (value == null) {
            return value;
        }
        return clazz.cast(value);
    }

}
