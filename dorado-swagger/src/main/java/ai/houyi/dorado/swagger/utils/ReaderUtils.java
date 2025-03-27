package ai.houyi.dorado.swagger.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import ai.houyi.dorado.swagger.ext.SwaggerExtension;
import ai.houyi.dorado.swagger.ext.SwaggerExtensions;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.util.ParameterProcessor;
import io.swagger.util.ReflectionUtils;


public class ReaderUtils {

    /**
     * Collects constructor-level parameters from class.
     *
     * @param cls is a class for collecting
     * @param swagger is the instance of the Swagger
     * @return the collection of supported parameters
     */
    public static List<Parameter> collectConstructorParameters(Class<?> cls, Swagger swagger) {
        if (cls.isLocalClass() || (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers()))) {
            return Collections.emptyList();
        }

        List<Parameter> selected = Collections.emptyList();
        int maxParamsCount = 0;

        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (!ReflectionUtils.isConstructorCompatible(constructor) &&
                    !ReflectionUtils.isInject(Arrays.asList(constructor.getDeclaredAnnotations()))) {
                continue;
            }

            final Type[] genericParameterTypes = constructor.getGenericParameterTypes();
            final Annotation[][] annotations = constructor.getParameterAnnotations();

            int paramsCount = 0;
            final List<Parameter> parameters = new ArrayList<Parameter>();
            for (int i = 0; i < genericParameterTypes.length; i++) {
                final List<Annotation> tmpAnnotations = Arrays.asList(annotations[i]);
                if (isContext(tmpAnnotations)) {
                    paramsCount++;
                } else {
                    final Type genericParameterType = genericParameterTypes[i];
                    final List<Parameter> tmpParameters = collectParameters(genericParameterType, tmpAnnotations);
                    if (tmpParameters.size() >= 1) {
                        for (Parameter tmpParameter : tmpParameters) {
                            if (ParameterProcessor.applyAnnotations(swagger,
                                    tmpParameter,
                                    genericParameterType,
                                    tmpAnnotations) != null) {
                                parameters.add(tmpParameter);
                            }
                        }
                        paramsCount++;
                    }
                }
            }

            if (paramsCount >= maxParamsCount) {
                maxParamsCount = paramsCount;
                selected = parameters;
            }
        }

        return selected;
    }

    /**
     * Collects field-level parameters from class.
     *
     * @param cls is a class for collecting
     * @param swagger is the instance of the Swagger
     * @return the collection of supported parameters
     */
    public static List<Parameter> collectFieldParameters(Class<?> cls, Swagger swagger) {
        final List<Parameter> parameters = new ArrayList<Parameter>();
        for (Field field : ReflectionUtils.getDeclaredFields(cls)) {
            final List<Annotation> annotations = Arrays.asList(field.getAnnotations());
            final Type genericType = field.getGenericType();
            for (Parameter parameter : collectParameters(genericType, annotations)) {
                if (ParameterProcessor.applyAnnotations(swagger, parameter, genericType, annotations) != null) {
                    parameters.add(parameter);
                }
            }
        }
        return parameters;
    }

    /**
     * Splits the provided array of strings into an array, using comma as the separator. Also removes leading and
     * trailing whitespace and omits empty strings from the results.
     *
     * @param strings is the provided array of strings
     * @return the resulted array of strings
     */
    public static String[] splitContentValues(String[] strings) {
        final Set<String> result = new LinkedHashSet<>();

        for (String string : strings) {
            Iterables.addAll(result, Splitter.on(",").trimResults().omitEmptyStrings().split(string));
        }

        return result.toArray(new String[result.size()]);
    }

    private static List<Parameter> collectParameters(Type type, List<Annotation> annotations) {
        final Iterator<SwaggerExtension> chain = SwaggerExtensions.chain();
        return chain.hasNext()
                ? chain.next().extractParameters(annotations, type, new HashSet<>(), chain)
                : Collections.<Parameter>emptyList();
    }

    private static boolean isContext(List<Annotation> annotations) {
        return false;
    }
}
