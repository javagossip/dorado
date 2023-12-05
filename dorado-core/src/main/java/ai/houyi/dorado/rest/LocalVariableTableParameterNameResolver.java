/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.dorado.rest;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.util.ClassLoaderUtils;
import ai.houyi.dorado.rest.util.Constant;

/**
 * 利用asm从类的局部变量表中获取方法参数名
 *
 * @author wangwp
 */
public class LocalVariableTableParameterNameResolver implements ParameterNameResolver {

    @Override
    public String[] getParameterNames(Method method) {
        final String methodName = method.getName();

        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String[] parameterNames = new String[parameterTypes.length];

        if (parameterTypes.length == 0) {
            return null;
        }

        boolean isStatic = Modifier.isStatic(method.getModifiers());

        final Type[] types = new Type[parameterTypes.length];
        final int[] lvtSlotIndex = new int[types.length];
        int nextIndex = (isStatic ? 0 : 1);

        for (int i = 0; i < types.length; i++) {
            types[i] = Type.getType(parameterTypes[i]);
            lvtSlotIndex[i] = nextIndex;
            if (types[i] == Type.LONG_TYPE || types[i] == Type.DOUBLE_TYPE) {
                nextIndex += 2;
            } else {
                nextIndex++;
            }
        }

        try {
            String classPath =
                    method.getDeclaringClass().getName().replace(Constant.DOT, Constant.SLASH) + Constant.CLASS_SUFFIX;
            InputStream resourceStream = ClassLoaderUtils.getStream(classPath);
            ClassReader classReader = new ClassReader(resourceStream);
            classReader.accept(new ClassVisitor(Opcodes.ASM6) {
                @Override
                public MethodVisitor visitMethod(int access,
                        String name,
                        String descriptor,
                        String signature,
                        String[] exceptions) {
                    Type[] argumentTypes = Type.getArgumentTypes(descriptor);
                    if (!methodName.equals(name) || !Arrays.equals(argumentTypes, types)) {
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }

                    return new MethodVisitor(Opcodes.ASM6) {
                        @Override
                        public void visitLocalVariable(String name,
                                String descriptor,
                                String signature,
                                Label start,
                                Label end,
                                int index) {
                            for (int i = 0; i < lvtSlotIndex.length; i++) {
                                if (lvtSlotIndex[i] == index) {
                                    parameterNames[i] = name;
                                }
                            }
                        }
                    };
                }
            }, 0);
        } catch (Exception ex) {
            String errorMsg = String.format("Get method parameter names error, method: %s", method.getName());
            throw new DoradoException(errorMsg, ex);
        }
        return parameterNames;
    }
}
