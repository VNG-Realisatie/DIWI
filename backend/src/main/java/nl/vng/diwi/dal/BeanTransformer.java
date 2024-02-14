package nl.vng.diwi.dal;

import org.hibernate.query.TupleTransformer;
import java.lang.reflect.Method;

public class BeanTransformer<T> implements TupleTransformer<T> {
    Class<T> clazz;
    Method[] setters;

    public BeanTransformer(Class<T> clazz) {
        this.clazz = clazz;
        this.setters = null;
    }

    @Override
    public T transformTuple(Object[] tuple, String[] aliases) {
        if (setters == null) {
            setters = new Method[aliases.length];
            var methods = clazz.getMethods();
            for (int i = 0; i < aliases.length; ++i) {
                for (var method : methods) {
                    if (method.getName().equalsIgnoreCase("set" + aliases[i])) {
                        if (method.getParameterTypes()[0].isPrimitive()) {
                            throw new RuntimeException("Primitive types are not supported");
                        }
                        setters[i] = method;
                        break;
                    }
                }
            }
        }

        try {
            T instance = clazz.getConstructor().newInstance();
            for (int i = 0; i < setters.length; ++i) {
                final Object value = tuple[i];
                Method setter = setters[i];
                if (setter == null) {
                    throw new Exception("Setter for " + aliases[i] + " is null");
                }
                if (value != null && value.getClass() != String.class && setter.getParameterTypes()[0] == String.class) {
                    setter.invoke(instance, value.toString());
                } else if (value != null && value.getClass() == String.class && setter.getParameterTypes()[0].isEnum()) {
                    setter.invoke(instance, Enum.valueOf((Class<? extends Enum>) setter.getParameterTypes()[0], (String) value));
                } else {
                    setter.invoke(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of " + clazz.getName(), e);
        }
    }

}
