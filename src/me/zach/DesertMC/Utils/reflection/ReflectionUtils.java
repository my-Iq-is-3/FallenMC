package me.zach.DesertMC.Utils.reflection;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static void setValue(Object object, String name, Object value){
        Class<?> clazz = object.getClass();
        try{
            Field variable = clazz.getField(name);
            variable.setAccessible(true);
            variable.set(object, value);
        }catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static void setValues(Object object, String[] names, Object[] values){
        if(names.length != values.length) throw new IllegalArgumentException("Amount of variable names and amount of values to set cannot be unequal");
        for(int i = 0; i<names.length; i++){
            setValue(object, names[i], values[i]);
        }
    }
}
