package com.freegang.androidutils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


/**
 * 反射工具类, 对反射的各种封装
 */
public class GReflectUtils {
    private static final HashMap<String, Field> fieldCache = new HashMap<>();

    /**
     * 从 XposedHelpers 中移植出来的通用工具方法
     * <p>
     * 获取某个类下的某个字段, 从当前类向上查找(父类), 直到找到为止
     *
     * @param clazz     目标类
     * @param fieldName 字段名
     * @return 找到的字段
     * @throws NoSuchFieldError 未找到异常
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        String fullFieldName = clazz.getName() + '#' + fieldName;

        if (fieldCache.containsKey(fullFieldName)) {
            Field field = fieldCache.get(fullFieldName);
            if (field == null)
                throw new NoSuchFieldError(fullFieldName);
            return field;
        }

        try {
            Field field = findFieldRecursiveImpl(clazz, fieldName);
            field.setAccessible(true);
            fieldCache.put(fullFieldName, field);
            return field;
        } catch (NoSuchFieldException e) {
            fieldCache.put(fullFieldName, null);
            throw new NoSuchFieldError(fullFieldName);
        }
    }

    //向上父类查找, 直到Object为止
    private static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }

    /**
     * 获取某个类的类结构
     *
     * @param clazz 被获取的类
     * @return 类结构字符串
     */
    public static String getClassStructure(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();

        ///包名: package xxx.xxx.xx
        Package clazzPackage = clazz.getPackage();
        builder.append("package ");
        if (clazzPackage != null && !clazzPackage.getName().trim().isEmpty()) {
            builder.append(clazzPackage.getName());
        }
        builder.append(";\n");

        //构建类 public abstract class XXX {...}
        builder.append(_recursionClassStructure(clazz, ""));

        return builder.toString();
    }

    //递归 类、内部类
    private static StringBuilder _recursionClassStructure(Class<?> clazz, String indent) {
        StringBuilder builder = new StringBuilder();

        //获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        //获取所有构造方法
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        //获取所有方法
        Method[] methods = clazz.getDeclaredMethods();
        //获取内部类
        Class<?>[] classes = clazz.getDeclaredClasses();
        Class<?> superclass = clazz.getSuperclass();
        //接口
        Class<?>[] interfaces = clazz.getInterfaces();

        ///类头: public abstract class XXX {
        builder.append("\n");
        builder.append(indent); //缩进

        //权限(public ...)
        String clazzMod = Modifier.toString(clazz.getModifiers());
        if (clazz.isAnnotation()) {
            clazzMod = clazzMod.replaceAll("abstract|interface", "").trim();
            builder.append(clazzMod);
            builder.append(" @interface");
        } else if (clazz.isInterface()) {
            clazzMod = clazzMod.replaceAll("abstract|interface", "").trim();
            builder.append(clazzMod);
            builder.append(" interface");
        } else if (clazz.isEnum()) {
            builder.append(clazzMod);
            builder.append(" enum");
        } else {
            builder.append(clazzMod);
            builder.append(" class");
        }
        //类名
        builder.append(" ");
        builder.append(clazz.getSimpleName());

        //父类 extends EEE
        if (superclass != null && superclass != Object.class) {
            builder.append(" extends ");
            builder.append(superclass.getSimpleName());
        }

        //接口 implements III
        if (interfaces.length != 0) {
            builder.append(" implements ");
            for (Class<?> anInterface : interfaces) {
                String typeName = anInterface.getTypeName();
                builder.append(typeName);
                builder.append(", ");
            }
            //最后一个逗号去除
            builder.delete(builder.length() - 2, builder.length());
        }
        //大括号开始
        builder.append(" {\n");

        ///类体
        //字段 public String name;
        if (fields.length != 0) {
            builder.append(indent).append("\t"); //缩进
            builder.append("// fields\n");
            for (Field field : fields) {
                //获取权限(public ...)
                String fieldMod = Modifier.toString(field.getModifiers());
                builder.append(indent).append("\t"); //缩进
                if (fieldMod.length() > 0) {
                    builder.append(fieldMod);
                    builder.append(" ");
                }
                builder.append(field.getType().getTypeName());
                builder.append(" ");
                builder.append(field.getName());
                builder.append(";\n");
            }
        }

        //构造方法
        if (constructors.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// constructors\n");
            for (Constructor<?> constructor : constructors) {
                builder.append(indent).append("\t"); //缩进
                //获取权限(public ...)
                String methodMod = Modifier.toString(constructor.getModifiers());
                if (methodMod.length() > 0) {
                    builder.append(methodMod);
                    builder.append(" ");
                }
                String name = constructor.getName().substring(constructor.getName().lastIndexOf(".") + 1);
                if (name.contains("$")) {
                    name = name.substring(name.lastIndexOf("$") + 1);
                }
                builder.append(name).append("(");
                //参数: String abc;
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length != 0) {
                    for (Class<?> parameterType : parameterTypes) {
                        builder.append(parameterType.getTypeName());
                        //参数逗号分割
                        builder.append(", ");
                    }
                    //最后一个逗号去除
                    builder.delete(builder.length() - 2, builder.length());
                }
                builder.append(");\n");
            }
        }

        //方法: public abstract XX method(XX xx);
        if (methods.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// methods\n");
            for (Method method : methods) {
                builder.append(indent).append("\t"); //缩进
                //获取权限(public ...)
                String methodMod = Modifier.toString(method.getModifiers());
                if (methodMod.length() > 0) {
                    builder.append(methodMod);
                    builder.append(" ");
                }
                builder.append(method.getReturnType().getTypeName());
                builder.append(" ");
                builder.append(method.getName());
                builder.append("(");

                //参数: String abc;
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 0) {
                    for (Class<?> parameterType : parameterTypes) {
                        builder.append(parameterType.getTypeName());
                        //参数逗号分割
                        builder.append(", ");
                    }
                    //最后一个逗号去除
                    builder.delete(builder.length() - 2, builder.length());
                }
                //方法尾
                builder.append(");\n");
            }
        }

        //内部类, 递归构建
        if (classes.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// inner class");
            for (Class<?> aClass : classes) {
                StringBuilder innerClass = _recursionClassStructure(aClass, indent + "\t");
                builder.append(innerClass);
            }
        }

        ///类尾, 大括号结束
        builder.append(indent).append("}\n");
        return builder;
    }

    /**
     * 反射获取某个类中所有被使用的类, 并将其转换为import语句输出
     * 该方法能捕获：成员字段、成员方法(方法参数)、类实现接口、类注解(字段/方法/参数)、方法抛出异常
     * 但是一旦方法体内部代码逻辑引入一个全新的类型作为局部变量, 该方法则无法捕获
     *
     * @param clazz 被操作的类, 如: Test.class
     */
    public static HashSet<String> getClassImports(Class<?> clazz) {
        HashSet<String> imports = new HashSet<>();

        // 获取类的所有注解
        addAnnotations(imports, clazz.getAnnotations());

        // 获取超类
        Class<?> superclass = clazz.getSuperclass();

        // 如果超类不为空，且不是 Object 类，则将其转换为 import 语句
        if (superclass != null && !superclass.equals(Object.class)) {
            String className = superclass.getCanonicalName();
            // 如果是 java.lang 包中的类，则跳过
            if (!className.contains("java.lang.")) {
                imports.add("import " + className + ";");
            }
        }

        // 获取类实现的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> inter : interfaces) {
            String className = inter.getCanonicalName();
            if (className.contains("java.lang.")) continue;
            imports.add("import " + className + ";");
        }

        // 获取类中所有属性的类型
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取属性的所有注解
            addAnnotations(imports, field.getAnnotations());
            // 获取属性的类型
            addClasses(imports, clazz, field.getType());
        }

        // 获取类中所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 获取方法抛出的所有异常
            Class<?>[] exceptions = method.getExceptionTypes();
            for (Class<?> exception : exceptions) {
                String className = exception.getCanonicalName();
                imports.add("import " + className + ";");
            }
            // 获取方法的参数类型
            Class<?>[] paramTypes = method.getParameterTypes();
            for (Class<?> paramType : paramTypes) {
                // 获取方法参数的所有注解
                addAnnotations(imports, paramType.getAnnotations());
                // 参数类型
                addClasses(imports, clazz, paramType);
            }
            // 获取方法的所有注解
            addAnnotations(imports, method.getAnnotations());
            // 获取方法return类型
            addClasses(imports, clazz, method.getReturnType());
        }

        // 递归处理内部类
        Class<?>[] innerClasses = clazz.getDeclaredClasses();
        for (Class<?> innerClass : innerClasses) {
            imports.addAll(getClassImports(innerClass));
        }

        return imports;
    }

    /**
     * 转为驼峰命名
     *
     * @param name 变量名称
     * @return 驼峰命名之后的变量名
     */
    private static String humpName(String name) {
        if (name.trim().isEmpty()) {
            return name;
        }

        char at = name.charAt(0);
        if (at > 'a' && at < 'z') {
            return name;
        }

        String lowerCase = name.substring(0, 1).toLowerCase();
        if (name.trim().length() == 1) {
            return lowerCase;
        }

        return lowerCase + name.substring(1);
    }

    /**
     * 添加所有注解
     *
     * @param imports     import集合
     * @param annotations 被操作类中的注解语句
     */
    private static void addAnnotations(HashSet<String> imports, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            // 获取注解的类型
            Class<?> annotationType = annotation.annotationType();
            // 将注解转换为import语句
            String className = annotationType.getCanonicalName();
            imports.add("import " + className + ";");
        }
    }

    /**
     * 添加所有类
     *
     * @param imports     import集合
     * @param clazz       被操作的类, 如: Test.class
     * @param memberClazz 被操作类中的成员类型, 可以是 field.getType()、method.getParameterTypes()等等
     */
    private static void addClasses(HashSet<String> imports, Class<?> clazz, Class<?> memberClazz) {
        memberClazz = getArrayType(memberClazz);
        // 如果返回类型是其他类，则将其转换为import语句
        if (!memberClazz.isPrimitive()) {
            String className = clazz.getCanonicalName();
            String memberClassName = memberClazz.getCanonicalName();
            //如果该类型是内部类, 跳过
            if (clazz.getDeclaringClass() != null) return;
            //如果成员类型是内部类, 跳过
            if (memberClazz.getDeclaringClass() != null) return;
            //如果该类型是它本身, 跳过
            if (memberClassName.equals(className)) return;
            //如果是java.lang包中的类, 跳过
            if (memberClassName.contains("java.lang.")) return;
            imports.add("import " + memberClassName + ";");
        }
    }

    /**
     * 获取数组元素类型
     *
     * @param clazz 被操作类中的成员类型, 该参数应该接收一个 array类型的数组, 如果不是则原型返回
     * @return 返回数组类型, 如果不是数组, 则返回本身的类型
     */
    public static Class<?> getArrayType(Class<?> clazz) {
        // 如果属性的类型是数组类型，则获取数组元素的类型
        if (clazz.isArray()) return clazz.getComponentType();
        return clazz;
    }

    /**
     * 反射某个类, 将该类尽可能的转换为抽象类
     * 未加import引用, 需要手动导入, 或者可以尝试 GReflectUtils#getClassImports 方法
     * 该方法无法对匿名内部类进行逻辑反射, 请悉知
     *
     * @param clazz 被操作的class
     * @return 抽象类结构表示
     */
    public static String classToAbsClass(Class<?> clazz) {
        return classToAbsClass(clazz, null);
    }

    /**
     * 反射某个类, 将该类尽可能的转换为抽象类
     * 未加import引用, 需要手动导入, 或者可以尝试 GReflectUtils#getClassImports 方法
     * 该方法无法对匿名内部类进行逻辑反射, 请悉知
     *
     * @param clazz    被操作的class
     * @param instance 该class的实例对象, 主要用于基本类型的属性值的回填
     * @return 抽象类结构表示
     */
    public static String classToAbsClass(Class<?> clazz, Object instance) {
        return _classToAbsClass(clazz, instance, "");
    }

    /**
     * 反射某个类, 将该类尽可能的转换为抽象类
     * 未加import引用, 需要手动导入, 或者可以尝试 GReflectUtils#getClassImports 方法
     * 该方法无法对匿名内部类进行逻辑反射, 请悉知
     *
     * @param clazz    被操作的class
     * @param instance 该class的实例对象, 主要用于基本类型的属性值的回填
     * @param indent   缩进, 请固定填写为 `\t`或`　`
     * @return 抽象类结构表示
     */
    private static String _classToAbsClass(Class<?> clazz, Object instance, String indent) {
        StringBuilder builder = new StringBuilder();
        //抽象类统一权限
        String modifier = "public abstract ";

        ///类头
        // 类名: public abstract Test extends SuperTest implements MyInterface1, MyInterface2 {
        String clazzName = clazz.getSimpleName();
        builder.append(modifier);
        if (Modifier.isStatic(clazz.getModifiers())) {
            builder.append("static ");
        }
        builder.append("class ").append(clazzName);
        // 超类
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            builder.append(" extends ").append(superclass.getSimpleName());
        }
        // 接口
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            builder.append(" implements ");
            for (Class<?> anInterface : interfaces) {
                builder.append(anInterface.getSimpleName()).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(" {");

        ///类体
        // 字段
        Field[] fieldList = clazz.getDeclaredFields();
        if (fieldList.length > 0) {
            for (Field field : fieldList) {
                builder.append("\n\t").append(indent);
                builder.append("public ");
                String fieldModifiers = Modifier.toString(field.getModifiers());
                String modifiers = fieldModifiers.replaceAll("public|protected|private", "").trim(); //全部放开
                builder.append(modifiers).append(modifiers.isEmpty() ? "" : " ");
                builder.append(field.getType().getSimpleName());
                builder.append(" ");
                builder.append(field.getName());
                //如果有实例对象, 回填基本数据类型
                if (instance != null && instance.getClass() == clazz) {
                    try {
                        field.setAccessible(true);
                        if (field.getType().isPrimitive()) {
                            builder.append(" = ").append(field.get(instance));
                        } else if (field.getType() == String.class) {
                            builder.append(" = \"").append(field.get(instance)).append("\"");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                builder.append(";");
            }
            builder.append("\n");
        }

        // 构造方法
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length > 0) {
            for (Constructor<?> constructor : constructors) {
                builder.append("\n\t").append(indent);
                builder.append("public ").append(constructor.getDeclaringClass().getSimpleName());
                // 参数
                builder.append(getConstructorArgs(constructor));
                // 异常
                builder.append(getMethodThrows(constructor));
                builder.append(" {}");
            }
            builder.append("\n");
        }

        // 普通方法
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                builder.append("\n\t").append(indent);
                // public abstract String test
                builder.append(modifier).append(method.getReturnType().getSimpleName()).append(" ").append(method.getName());
                // 参数
                builder.append(getMethodArgs(method));
                // 异常
                builder.append(getMethodThrows(method));
                builder.append(";");
            }
        }

        // 内部类递归
        Class<?>[] declaredClasses = clazz.getDeclaredClasses();
        if (declaredClasses.length > 0) {
            for (Class<?> declaredClass : declaredClasses) {
                builder.append("\n\n\t").append(indent);
                builder.append(_classToAbsClass(declaredClass, null, indent.concat("\t")));
            }
        }

        ///类尾
        builder.append("\n").append(indent).append("}");
        return builder.toString();
    }

    /**
     * 获取某个构造方法参数列表
     */
    private static String getConstructorArgs(Constructor<?> constructor) {
        StringBuilder builder = new StringBuilder();
        Class<?> declaringClass = constructor.getDeclaringClass();

        String modifier = Modifier.toString(declaringClass.getModifiers());

        builder.append("(");
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length > 0) {
            int argsCount = 1;
            //是否静态内部类, 非静态内部类会自动生成一个外部类的构造方法参数引用, 这里因为将其抽象并静态了(详情: 内部类递归时 isStatic ), 所以没必要引用
            boolean isInnerStaticClass = !modifier.contains("static") && declaringClass.getDeclaringClass() != null;
            for (int i = isInnerStaticClass ? 1 : 0; i < parameterTypes.length; i++) {
                builder.append(parameterTypes[i].getSimpleName());
                builder.append(" ");
                builder.append(humpName(parameterTypes[i].getSimpleName())).append(argsCount++).append(", ");
            }
            if (builder.lastIndexOf(", ") != -1) {
                builder.delete(builder.length() - 2, builder.length());
            }
        }

        builder.append(")");
        return builder.toString();
    }

    /**
     * 获取某个方法的参数列表
     *
     * @param method 方法名
     * @return 参数列表, 如: (String string0, String string1)
     */
    private static String getMethodArgs(Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            int argsCount = 1;
            for (Class<?> type : parameterTypes) {
                builder.append(type.getSimpleName());
                builder.append(" ");
                builder.append(humpName(type.getSimpleName())).append(argsCount++).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 获取某个方法抛出的异常列表
     *
     * @param method 方法名
     * @return 异常列表, 如: throws IOException, Exception
     */
    private static String getMethodThrows(Executable method) {
        StringBuilder builder = new StringBuilder();
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            builder.append(" throws ");
            for (Class<?> type : exceptionTypes) {
                builder.append(type.getSimpleName()).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }
}
