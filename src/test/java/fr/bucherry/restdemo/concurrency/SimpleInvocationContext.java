package fr.bucherry.restdemo.concurrency;

import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public class SimpleInvocationContext implements InvocationContext, Serializable {
    private final Object target;
    private final Method method;
    private Object[] parameters = new Object[0];

    public SimpleInvocationContext(Object target, String methodName, Object... parameters) {
        this.target = target;
        try {
            this.method = target.getClass().getMethod(methodName, int.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        this.parameters = parameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object getTimer() {
        return null;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Constructor<?> getConstructor() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(Object[] objects) {
        this.parameters = objects;
    }

    @Override
    public Map<String, Object> getContextData() {
        return null;
    }

    @Override
    public Object proceed() throws Exception {
        return method.invoke(target, parameters);
    }
}
