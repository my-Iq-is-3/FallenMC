package me.zach.DesertMC.Utils.debug;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfoDumper {
    static final String[] DEFAULT_KEYWORDS = new String[]{"get", "is"};

    final Object obj;
    final String name;
    final String[] methodKeywords;
    public InfoDumper(Object obj, String name, boolean includeDefaultMethodKeywords, String... methodKeywords){
        this.obj = obj;
        this.name = name;
        if(includeDefaultMethodKeywords){
            String[] resizedKeywords = new String[methodKeywords.length + DEFAULT_KEYWORDS.length];
            System.arraycopy(methodKeywords, 0, resizedKeywords, DEFAULT_KEYWORDS.length, methodKeywords.length);
            System.arraycopy(DEFAULT_KEYWORDS, 0, resizedKeywords, 0, DEFAULT_KEYWORDS.length);
            this.methodKeywords = resizedKeywords;
        }else this.methodKeywords = methodKeywords;
    }

    public InfoDumper(Object obj, boolean includeDefaultMethodKeywords,  String... methodKeywords){
        this(obj, "object", includeDefaultMethodKeywords, methodKeywords);
    }

    public void dump(Level level){dump(Bukkit.getLogger(), level);}
    public void dump(Logger logger, Level level){
        long startTime = System.currentTimeMillis();
        logger.log(level, "Dumping info for object \"" + name + "\" of type " + obj.getClass().getName() + "...");
        Class<?> objClass = obj.getClass();
        List<Exception> exceptions = new ArrayList<>();
        if(methodKeywords.length != 0){
            HashSet<Method> methods = new HashSet<>();
            Predicate<Method> filter = method -> {
                if(!method.getReturnType().equals(void.class) && method.getParameterCount() == 0 && !Modifier.isStatic(method.getModifiers())){
                    boolean keywordFound = false;
                    String methodName = method.getName();
                    for(int i = 0; i < methodKeywords.length && !keywordFound; i++)
                        keywordFound = methodName.contains(methodKeywords[i]);
                    return keywordFound;
                }else return false;
            };

            for(Method method : getMethodsThorough(objClass, methods, filter)){
                String methodName = method.getName();
                try{
                    logger.log(level, methodName + "(): (" + method.getReturnType().getSimpleName() + ") " + method.invoke(obj));
                }catch(IllegalAccessException | InvocationTargetException e){
                    logger.warning("An error occurred while running method " + method.getName() + "(). Stack trace to be printed after dump.");
                    exceptions.add(e);
                }
            }
        }

        for(Field field : objClass.getFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                try{
                    logger.log(level, field.getName() + ": (" + field.getType().getSimpleName() + ") " + field.get(obj).toString());
                }catch(IllegalAccessException ignored){/*IllegalAccessException should be unthrowable by field.get() since Class#getFields returns all public fields of that class.*/}
            }
        }
        float elapsedTime = ((float) startTime - System.currentTimeMillis()) / 1000000000;
        DecimalFormat formatter = new DecimalFormat("#.#####");
        logger.log(level, "Debug info dump finished in " + formatter.format(elapsedTime) + "s for object \"" + name + "\". ");
        if(!exceptions.isEmpty()){
            logger.log(level, "Stack traces of any exceptions that occurred during method dump will now be printed.");
            for(Exception ex : exceptions) logger.log(level, ex.getMessage(), ex);
        }
    }

    private HashSet<Method> getMethodsThorough(Class<?> type, HashSet<Method> existing, Predicate<Method> filter){
        List<Method> filteredMethods = Arrays.asList(Arrays.stream(type.getMethods()).filter(filter).toArray(Method[]::new));
        Bukkit.getLogger().info("Added these methods from class " + type.getName() + ": " + filteredMethods);
        existing.addAll(filteredMethods);
        Class<?> superclass = type.getSuperclass();
        Class<?>[] classInterfaces = type.getInterfaces();
        if(superclass != null) getMethodsThorough(superclass, existing, filter);
        if(classInterfaces.length != 0){
            for(Class<?> implementee : classInterfaces) getMethodsThorough(implementee, existing, filter);
        }
        return existing;
    }
}