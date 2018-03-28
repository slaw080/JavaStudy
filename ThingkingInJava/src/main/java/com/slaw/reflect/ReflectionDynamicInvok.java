package com.slaw.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: ReflectionDynamicInvok
* @Description: 动态加载并执行
* @author lijun
* @date 2018年3月27日 下午3:34:03
*
 */
public class ReflectionDynamicInvok {

    public static void main(String[] args) {
        String className = "com.slaw.reflect.TestClass";
        String methodName = "eachOrtherToAdd";
        String[] paramTypes = new String[] { "Integer", "Double", "int" };
        String[] paramValues = new String[] { "1", "4.3321", "5" };
        // 动态加载对象并执行方法
        initLoadClass(className, methodName, paramTypes, paramValues);
    }

    @SuppressWarnings("rawtypes")
    private static void initLoadClass(String className, String methodName, String[] paramTypes, String[] paramValues) {
        try {
            // 根据calssName得到class对象
            Class cls = Class.forName(className);
            // 实例化对象
            Object obj = cls.newInstance();
            // 根据参数类型数组得到参数类型的Class数组
            Class[] parameterTypes = constructTypes(paramTypes);
            // 得到方法
            Method method = cls.getMethod(methodName, parameterTypes);
            // 根据参数类型数组和参数值数组得到参数值的obj数组
            Object[] parameterValues = constructValues(paramTypes, paramValues);
            // 执行这个方法并返回obj值
            Object returnValue = method.invoke(obj, parameterValues);
            System.out.println("结果：" + returnValue);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
    * @Title: constructValues
    * @Description: 格式化参数
    * @param paramTypes
    * @param paramValues
    * @return    参数说明
    * @return Object[]    返回值说明
    * @throws
     */
    private static Object[] constructValues(String[] paramTypes, String[] paramValues) {
        Object[] obj = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] != null && !paramTypes[i].trim().equals("")) {
                if ("Integer".equals(paramTypes[i]) || "int".equals(paramTypes[i])) {
                    obj[i] = Integer.parseInt(paramValues[i]);
                } else if ("Double".equals(paramTypes[i]) || "double".equals(paramTypes[i])) {
                    obj[i] = Double.parseDouble(paramValues[i]);
                } else if ("Float".equals(paramTypes[i]) || "float".equals(paramTypes[i])) {
                    obj[i] = Float.parseFloat(paramValues[i]);
                } else {
                    obj[i] = paramTypes[i];
                }
            }
        }
        return obj;
    }

    /**
     * 
    * @Title: constructTypes
    * @Description: 获取参数类型
    * @param paramTypes
    * @return    参数说明
    * @return Class[]    返回值说明
    * @throws
     */
    @SuppressWarnings("rawtypes")
    private static Class[] constructTypes(String[] paramTypes) {
        Class[] cls = new Class[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] != null && !paramTypes[i].trim().equals("")) {
                if ("Integer".equals(paramTypes[i]) || "int".equals(paramTypes[i])) {
                    cls[i] = Integer.class;
                } else if ("Double".equals(paramTypes[i]) || "double".equals(paramTypes[i])) {
                    cls[i] = Double.class;
                } else if ("Float".equals(paramTypes[i]) || "float".equals(paramTypes[i])) {
                    cls[i] = Float.class;
                } else {
                    cls[i] = String.class;
                }
            }
        }
        return cls;
    }

    /**
     * 
    * @Title: getInfor
    * @Description: 
    * 1.通过类，拿到这个的所有属性（包括父类），形成一个数组arr[]
    * 2.然后写一个根据字段生成的set方法；
    * 3.循环遍历数组，通过request.getParameter("arr[i]")    获取请求传来的值；
    * 4.通过反射将3得到的数据set进方法中。
    * @param t
    * @param request
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException    参数说明
    * @return void    返回值说明
    * @throws
     */
    public static <T> void getInfor(T t, HttpServletRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method father[] = t.getClass().getGenericSuperclass().getClass().getMethods(); //取父亲类的方法
        Method son[] = t.getClass().getDeclaredMethods(); //取当前方法
        Method[] methods = ArrayUtils.addAll(father, son); //合并方法
        Field sonFil[] = t.getClass().getFields(); //当前类字段
        Field fatherFil[] = t.getClass().getGenericSuperclass().getClass().getFields(); //父类字段
        Field[] field = ArrayUtils.addAll(fatherFil, sonFil); //合并
        for (int j = 0; j < methods.length; j++) //遍历方法
        {
            for (int i = 0; i < field.length; i++) {
                if (methods[j].getName().equals(getFirstCharacterToUpper(field[i].getName()))) //如果方法名称与字段生成的方法名称一样，则从request中拿到值，并且设置值
                {
                    methods[j].invoke(t, request.getParameterValues(field[i].getName())); //反射设置值
                    break;
                }
            }
        }
    }

    /*根据字段获得它的set方法*/
    private static String getFirstCharacterToUpper(String srcStr) {
        String setMethod = "set" + StringUtils.capitalize(srcStr); // 首字母大写
        return setMethod;
    }
}
