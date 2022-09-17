package services.core;

import core.PackageClassesFinder;
import operations.core.WiFreeRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functions {

    private static final Map<Class<? extends WiFreeRequest>, WiFreeFunction> allFunctions = new HashMap<>();

    static {
        try {
            initialize();
        } catch (IllegalAccessException e) {
            e.printStackTrace();                // TODO print properly
        } catch (InstantiationException e) {
            e.printStackTrace();                // TODO print properly
        }
    }

    public static <RQ extends WiFreeRequest> WiFreeFunction of(RQ request) {
        if (allFunctions.isEmpty()) {
            try {
                initialize();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return allFunctions.get(request.getClass());
    }

    private static void initialize() throws IllegalAccessException, InstantiationException {
        final List<Class<? extends WiFreeFunction>> functionsClasses = PackageClassesFinder.getFunctionsClasses();
        for (Class<? extends WiFreeFunction> functionClass : functionsClasses) {
            put(functionClass.newInstance());
        }
    }

    private static <F extends WiFreeFunction> void put(F... serviceFunctions) {
        Arrays.stream(serviceFunctions).forEach(function -> allFunctions.put(function.rqClass(), function));
    }

}
