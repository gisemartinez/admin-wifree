package core;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import services.core.WiFreeFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackageClassesFinder {

    private static final String FUNCTIONS_PACKAGE_NAME = "services.core.functions";

    public static List<Class<? extends WiFreeFunction>> getFunctionsClasses() {
        List<Class<? extends WiFreeFunction>> functionsClasses = new ArrayList<>();
        for (Class<?> functionClass : of(FUNCTIONS_PACKAGE_NAME)) {
            final Class<? extends WiFreeFunction> castedFunctionClass = (Class<? extends WiFreeFunction>) functionClass;
            functionsClasses.add(castedFunctionClass);
        }
        return functionsClasses;
    }

    private static List<? extends Class<?>> of(String packageName) {
        try {
            final ImmutableSet<ClassPath.ClassInfo> classInfos = ClassPath.from(WiFreeFunction.class.getClassLoader()).getTopLevelClasses(packageName);
            return classInfos.stream().map(x -> x.load()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
