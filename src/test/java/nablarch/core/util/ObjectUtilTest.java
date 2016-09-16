package nablarch.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * {@link ObjectUtil}のテストクラス。
 * 
 * @author Kiyohito Itoh
 */
public class ObjectUtilTest {

    /**
     * {@link ObjectUtil#createInstance(String)}のテスト。
     * <br/>
     * クラス名に対応するクラスが存在する場合。
     */
    @Test
    public void testCreateInstanceForClassFound() {
        
        String className = ObjectUtilTest.class.getName();
        ObjectUtilTest test = ObjectUtil.createInstance(className);
        assertTrue(test != null);
    }
    
    /**
     * {@link ObjectUtil#createInstance(String)}のテスト。
     * <br/>
     * クラス名に対応するクラスが存在しない場合。
     */
    @Test
    public void testCreateInstanceForClassNotFound() {
        try {
            ObjectUtil.createInstance("hoge.Dummy");
            fail("must be thrown the IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getCause().getClass(), ClassNotFoundException.class);
        }
    }

    /**
     * {@link ObjectUtil#createInstance(String)}のテスト。
     * <br/>
     * クラス名に対応するクラスにデフォルトコンストラクタが存在しない場合。
     */
    @Test
    public void testCreateInstanceForDefaultConstructorNotFound() {
        String className = ObjectUtilTest.ConstructorNotFound.class.getName();
        try {
            ObjectUtil.createInstance(className);
            fail("must be thrown the IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getCause().getClass(), InstantiationException.class);
        }
    }

    /**
     * {@link ObjectUtil#createInstance(String)}のテスト。
     * <br/>
     * クラス名に対応するクラスにアクセスできない場合。
     */
    @Test
    public void testCreateInstanceForIllegalAccess() {
        String className = ObjectUtilTest.IllegalAccess.class.getName();
        try {
            ObjectUtil.createInstance(className);
            fail("must be thrown the IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getCause().getClass(), IllegalAccessException.class);
        }
    }
    
    public static final class ConstructorNotFound {
        private String value;
        public ConstructorNotFound(String value) {
            this.value = value;
        }
    }
    
    private static final class IllegalAccess {
    }

    /**
     * {@link ObjectUtil#setProperty(Object, String, Object)}のテスト。
     * <br/>
     */
    @Test
    public void testSetProperty() {
        TargetClass target = new TargetClass();
        ObjectUtil.setProperty(target, "prop1", "test01");

        assertEquals("test01", target.prop1);
    }

    /**
     * {@link ObjectUtil#getProperty(Object, String)}のテスト。
     * <br/>
     */
    @Test
    public void testGetProperty() {
        {

            TargetClass target = new TargetClass();
            target.setProp1("test01");
            String actual = ObjectUtil.getProperty(target, "prop1").toString();

            assertEquals("test01", actual);
        }
        { 
            try {
                ObjectUtil.getProperty(null, "hoge");
                fail("例外が発生するはず");
            } catch (IllegalArgumentException e) {
                
            }
        }
        { 
            try {
                ObjectUtil.getProperty(new TargetClass(), null);
                fail("例外が発生するはず");
            } catch (IllegalArgumentException e) {
                
            }
        }
        { 
            try {
                ObjectUtil.getProperty(new TargetClass(), "noExists");
                fail("例外が発生するはず");
            } catch (IllegalArgumentException e) {
                assertEquals("property noExists not found on class " + TargetClass.class.getName(), e.getMessage());
            }
        }
        
    }

    @Test
    public void testGetPropertyIfExists() {
        {
            TargetClass target = new TargetClass();
            target.setProp1("test01");
            String actual = ObjectUtil.getPropertyIfExists(target, "prop1").toString();

            assertEquals("test01", actual);
        }
        { 
            try {
                ObjectUtil.getPropertyIfExists(null, "hoge");
                fail("例外が発生するはず");
            } catch (IllegalArgumentException e) {
                
            }
        }
        { 
            try {
                ObjectUtil.getPropertyIfExists(new TargetClass(), null);
                fail("例外が発生するはず");
            } catch (IllegalArgumentException e) {
                
            }
        }
        { 
            // 存在しないプロパティ
            assertNull(ObjectUtil.getPropertyIfExists(new TargetClass(), "noExists"));
        }
        
    }
    /**
     * {@link ObjectUtil#getProperty(Object, String)}のテスト。
     * Mapから取得する場合。
     * <br/>
     */
    @Test
    public void testGetPropertyFromMap() {
        Map<String, String> target = new HashMap<String, String>();
        target.put("prop1", "test01");
        String actual = ObjectUtil.getProperty(target, "prop1").toString();

        assertEquals("test01", actual);
    }
    
    /**
     * {@link ObjectUtil#setProperty(Object, String, Object)}のテスト。
     * 実行が失敗する場合。
     * <br/>
     */
    @Test
    public void testSetPropertyFail() {
        TargetClass target = new TargetClass();
        try {
            ObjectUtil.setProperty(target, "prop", "test01");
            fail("property not found");
        } catch (RuntimeException e) {
            // OK
        }
    }
    
    /**
     * {@link ObjectUtil#getProperty(Object, String)}のテスト。
     * 実行が失敗する場合。
     * <br/>
     */
    @Test
    public void testGetPropertyFail() {
        TargetClass target = new TargetClass();
        try {
            ObjectUtil.getProperty(target, "prop");
            fail("property not found");
        } catch (RuntimeException e) {
            // OK
        }
    }

    /**
     * {@link nablarch.core.util.ObjectUtil#getProperty(Object, String)}のテスト。
     *
     * getter内で例外が発生するケース
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyInvokeFail() {
        Object o = new Object() {
          public String getProp() {
              throw new NullPointerException();
          }
        };

        ObjectUtil.getProperty(o, "prop");
    }
    
    /**
     * {@link ObjectUtil#setProperty(Object, String, Object)}のテスト。
     * <br/>
     */
    @Test
    public void testSetPropertyWithInterface() {
        TargetClass5 target = new TargetClass5();
        TargetClass4 prop = new TargetClass4();
        
        ObjectUtil.setProperty(target, "prop1", prop);

        assertEquals(prop, target.prop1);
    }
    
    /**
     * {@link ObjectUtil#setProperty(Object, String, Object)}のテスト。
     * <br/>
     */
    @Test
    public void testSetPropertyPrimitiveType() {
        TargetClass6 target = new TargetClass6();        
        ObjectUtil.setProperty(target, "boolProp", true);
        ObjectUtil.setProperty(target, "charProp", 'x');
        ObjectUtil.setProperty(target, "byteProp", (byte) 1);
        ObjectUtil.setProperty(target, "shortProp", (short) 2);
        ObjectUtil.setProperty(target, "intProp", 3);
        ObjectUtil.setProperty(target, "longProp", 4l);
        ObjectUtil.setProperty(target, "floatProp", 1.0f);
        ObjectUtil.setProperty(target, "doubleProp", 2.0);
        

        assertEquals(true, target.boolProp);
        assertEquals('x', target.charProp);
        assertEquals((byte) 1, target.byteProp);
        assertEquals((short) 2, target.shortProp);
        assertEquals(3, target.intProp);
        assertEquals(4l, target.longProp);
        assertTrue(1.0f - target.floatProp < 0.1);
        assertTrue(2.0 - target.doubleProp < 0.1);
    }

    /**
     * {@link ObjectUtil#getAncestorClasses(Class)}のテスト。
     * 実行が失敗する場合。
     * <br/>
     */
    @Test
    public void testGetAncesterClasses() {
        List<Class<?>> classes = ObjectUtil.getAncestorClasses(TargetClass3.class);
        assertEquals(TargetClass2.class, classes.get(0));
        assertEquals(TargetClass.class, classes.get(1));

        classes = ObjectUtil.getAncestorClasses(Object.class);
        assertTrue(classes.isEmpty());
        
    }

    /**
     * {@link ObjectUtil#getPropertyType(Class, String)}のテスト。
     */
    @Test
    public void testGetPropertyType() {
        Class<?> boolPropType = ObjectUtil.getPropertyType(TargetClass6.class, "boolProp");
        Class<?> charPropType = ObjectUtil.getPropertyType(TargetClass6.class, "charProp");
        Class<?> strPropType = ObjectUtil.getPropertyType(TargetClass.class, "prop1");
        
        assertEquals(boolean.class, boolPropType);
        assertEquals(char.class, charPropType);
        assertEquals(String.class, strPropType);
    }

    /**
     * {@link ObjectUtil#getPropertyType(Class, String)}のテスト。<br/>
     * 親クラスのプロパティ
     */
    @Test
    public void testGetPropertyTypeParentProperty() {
        Class<?> boolPropType = ObjectUtil.getPropertyType(TargetClass7.class, "boolProp");
        Class<?> charPropType = ObjectUtil.getPropertyType(TargetClass7.class, "charProp");
        
        assertEquals(boolean.class, boolPropType);
        assertEquals(char.class, charPropType);
        
    }
    /**
     * {@link ObjectUtil#getWritablePropertyNames(Class)}のテスト。
     */
    @Test
    public void testGetWritablePropertyNames() {
        List<String> writablePropertyNames = ObjectUtil.getWritablePropertyNames(TargetClass6.class);
        Collections.sort(writablePropertyNames);
        assertArrayEquals(new String[] {"boolProp",
                        "byteProp",
                        "charProp",
                        "doubleProp",
                        "floatProp",
                        "intProp",
                        "longProp",
                        "shortProp",
                        }, 
                        writablePropertyNames.toArray());
    }

    /**
     * {@link ObjectUtil#getPropertyNameFromSetter(java.lang.reflect.Method)}のテスト。
     */
    @Test
    public void testGetPropertyNameFromSetter() throws Throwable {
        String name = ObjectUtil.getPropertyNameFromSetter(TargetClass6.class.getMethod("setBoolProp", new Class<?>[] {boolean.class}));
        assertEquals("boolProp", name);
        
        try {
            ObjectUtil.getPropertyNameFromSetter(TargetClass6.class.getMethod("testBoolProp", new Class<?>[] {boolean.class}));
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }
    
    /**
     * {@link ObjectUtil#getPropertyNameFromGetter(java.lang.reflect.Method)}のテスト。
     */
    @Test
    public void testGetPropertyNameFromGetter() throws Throwable {
        String name = ObjectUtil.getPropertyNameFromGetter(TargetClass.class.getMethod("getProp1"));
        assertEquals("prop1", name);
        
        try {
            ObjectUtil.getPropertyNameFromGetter(TargetClass.class.getMethod("setProp1", new Class<?>[] {String.class}));
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }
    
    @Test
    public void testGetSetterMethods() throws Exception {
        List<Method> setterMethods = ObjectUtil.getSetterMethods(TargetClass8.class);
        
        assertEquals(1, setterMethods.size());
        assertEquals("setProp1", setterMethods.get(0).getName()); 
    }
    
    @Test
    public void testGetGetterMethods() throws Exception {
        List<Method> getterMethods = ObjectUtil.getGetterMethods(TargetClass.class);
        
        assertEquals(1, getterMethods.size());
        assertEquals("getProp1", getterMethods.get(0).getName()); 
    }

    @Test
    public void testFindMatchMethodFail() {
        assertNull(ObjectUtil.findMatchMethod(TargetClass.class, "setProp1", new Class<?>[]{int.class}));
        assertNull(ObjectUtil.findMatchMethod(TargetClass.class, "setProp1", new Class<?>[]{}));
    }

    @Test
    public void testGetSetterMethod() throws Exception {
        assertEquals(TargetClass.class.getMethod("setProp1", new Class<?>[] {String.class}) , ObjectUtil.getSetterMethod(TargetClass.class, "prop1"));
    }

    @Test
    public void testGetGetterMethod() throws Exception {
        Object o = new Object() {
            public String getMethod1() {
                return null;
            }
            public Long getMethod2() {
                return null;
            }
        };
        Method method1 = ObjectUtil.getGetterMethod(o.getClass(), "method1");
        assertEquals(o.getClass().getMethod("getMethod1"), method1);
        Method method2 = ObjectUtil.getGetterMethod(o.getClass(), "method2");
        assertEquals(o.getClass().getMethod("getMethod2"), method2);
    }

    @Test
    public void testGetGetterMethodEmpty() {
        try {
            ObjectUtil.getGetterMethod(Object.class, "");
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            assertEquals("propertyName must not null or empty.", e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGetGetterMethodFail() {
        Object o = new Object() {
            public String getMethod1() {
                return null;
            }
            public Long getMethod2() {
                return null;
            }
        };
        ObjectUtil.getGetterMethod(o.getClass(), "notfound");
    }


    @Test
    public void testGetSetterMethodFail() throws Exception {
        try {
            ObjectUtil.getSetterMethod(TargetClass.class, "prop");
            fail("例外が発生するはず");
        } catch (RuntimeException e) {
            // OK 
        }
    }

    @Test
    public void testGetPropertyTypeFail() {
        ObjectUtil.getPropertyType(TargetClass8.class, "prop1");
    }


    /**
     * セッタの引数が1個以外のメソッドはプロパティとして判定されないこと。
     */
    @Test
    public void testGetPropertyPropertySignatureIsInvalid() {
    	List<String> names = ObjectUtil.getWritablePropertyNames(TargetClass9.class);
    	assertFalse(names.contains("property"));
    }

    /**
     * {@link nablarch.core.util.ObjectUtil#createExceptionsClassList(java.util.List)}のテスト
     */
    @Test
    public void testCreateExceptionsClassList() {
        List<String> exceptions = new ArrayList<String>();
        exceptions.add("java.lang.NullPointerException");
        ObjectUtil.createExceptionsClassList(exceptions);
    }



    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass {
        private String prop1;
        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }
        public String getProp1() {
            return prop1;
        }
    }


    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass2 extends TargetClass {
        
    }

    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass3 extends TargetClass2 {
        
    }

    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass4 implements TargetInterface {
        
    }

    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass5 implements TargetInterface {
        private TargetClass4 prop1;
        public void setProp1(TargetClass4 prop1) {
            this.prop1 = prop1;
        }
    }

    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass6 implements TargetInterface {
        private boolean boolProp;
        private char charProp;
        private byte byteProp;
        private short shortProp;
        private int intProp;
        private long longProp;
        private float floatProp;
        private double doubleProp;
        /**
         * boolPropをセットする。
         * @param boolProp セットするboolProp。
         */
        public void setBoolProp(boolean boolProp, String dummy) {
            this.boolProp = boolProp;
        }

        /**
         * boolPropをセットする。
         * @param boolProp セットするboolProp。
         */
        public void setBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }
        /**
         * charPropをセットする。
         * @param charProp セットするcharProp。
         */
        public void setCharProp(char charProp) {
            this.charProp = charProp;
        }
        /**
         * bytePropをセットする。
         * @param byteProp セットするbyteProp。
         */
        public void setByteProp(byte byteProp) {
            this.byteProp = byteProp;
        }
        /**
         * shortPropをセットする。
         * @param shortProp セットするshortProp。
         */
        public void setShortProp(short shortProp) {
            this.shortProp = shortProp;
        }
        /**
         * intPropをセットする。
         * @param intProp セットするintProp。
         */
        public void setIntProp(int intProp) {
            this.intProp = intProp;
        }
        /**
         * longPropをセットする。
         * @param longProp セットするlongProp。
         */
        public void setLongProp(long longProp) {
            this.longProp = longProp;
        }
        /**
         * floatPropをセットする。
         * @param floatProp セットするfloatProp。
         */
        public void setFloatProp(float floatProp) {
            this.floatProp = floatProp;
        }
        /**
         * doublePropをセットする。
         * @param doubleProp セットするdoubleProp。
         */
        public void setDoubleProp(double doubleProp) {
            this.doubleProp = doubleProp;
        }
     
        public void testBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }
    }
    
    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass7 extends TargetClass6 {
    }
    

    /**
     * テスト対象クラス。
     *
     */
    public static class TargetClass8 {
        public void setProp1(String prop1) {
            
        }

        // このメソッドはセッタではない
        public void setprop2() {
            
        }
    }

    /**
     * テスト対象クラス。<br/>
     * セッタのメソッドシグネチャがJava Beans使用に合わない場合。
     */
    public static class TargetClass9 {
    	public void setProperty(String key, String value) {
    		
    	}
    }
    
    /**
     * テスト用インタフェース
     *
     */
    public static interface TargetInterface {
        
    }
}
