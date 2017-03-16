package nablarch.core.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsArrayContainingInOrder;
import org.hamcrest.number.OrderingComparison;

import org.junit.Assert;
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
        Assert.assertThat(test, not(sameInstance(null)));
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
            assertThat(e.getCause(), instanceOf(ClassNotFoundException.class));
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
            assertThat(e.getCause(), instanceOf(InstantiationException.class));
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
            assertThat(e.getCause(), instanceOf(IllegalAccessException.class));
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

        Assert.assertThat(target.prop1, is("test01"));
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

            Assert.assertThat(actual, is("test01"));
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
                Assert.assertThat(e.getMessage(),
                        is("property noExists not found on class " + TargetClass.class.getName()));
            }
        }
        
    }

    @Test
    public void testGetPropertyIfExists() {
        {
            TargetClass target = new TargetClass();
            target.setProp1("test01");
            String actual = ObjectUtil.getPropertyIfExists(target, "prop1").toString();

            Assert.assertThat(actual, is("test01"));
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
            Assert.assertThat(ObjectUtil.getPropertyIfExists(new TargetClass(), "noExists"), nullValue());
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

        Assert.assertThat(actual, is("test01"));
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

        Assert.assertThat(target.prop1, is(prop));
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
        

        Assert.assertThat(target.boolProp, is(true));
        Assert.assertThat(target.charProp, is('x'));
        Assert.assertThat(target.byteProp, is((byte) 1));
        Assert.assertThat(target.shortProp, is((short) 2));
        Assert.assertThat(target.intProp, is(3));
        Assert.assertThat(target.longProp, is(4l));
        Assert.assertThat(1.0f - target.floatProp, lessThan(0.1F));
        Assert.assertThat(2.0 - target.doubleProp, lessThan(0.1));
    }

    /**
     * {@link ObjectUtil#getAncestorClasses(Class)}のテスト。
     * 実行が失敗する場合。
     * <br/>
     */
    @Test
    public void testGetAncesterClasses() {
        List<Class<?>> classes = ObjectUtil.getAncestorClasses(TargetClass3.class);
        Assert.assertThat(classes.get(0), CoreMatchers.<Class<?>>is(TargetClass2.class));
        Assert.assertThat(classes.get(1), CoreMatchers.<Class<?>>is(TargetClass.class));

        classes = ObjectUtil.getAncestorClasses(Object.class);
        Assert.assertThat(classes.isEmpty(), is(true));
        
    }

    /**
     * {@link ObjectUtil#getPropertyType(Class, String)}のテスト。
     */
    @Test
    public void testGetPropertyType() {
        Class<?> boolPropType = ObjectUtil.getPropertyType(TargetClass6.class, "boolProp");
        Class<?> charPropType = ObjectUtil.getPropertyType(TargetClass6.class, "charProp");
        Class<?> strPropType = ObjectUtil.getPropertyType(TargetClass.class, "prop1");
        
        assertThat(boolPropType, CoreMatchers.<Class<?>>is(boolean.class));
        assertThat(charPropType, CoreMatchers.<Class<?>>is(char.class));
        assertThat(strPropType, CoreMatchers.<Class<?>>is(String.class));
    }

    /**
     * {@link ObjectUtil#getPropertyType(Class, String)}のテスト。<br/>
     * 親クラスのプロパティ
     */
    @Test
    public void testGetPropertyTypeParentProperty() {
        Class<?> boolPropType = ObjectUtil.getPropertyType(TargetClass7.class, "boolProp");
        Class<?> charPropType = ObjectUtil.getPropertyType(TargetClass7.class, "charProp");
        
        Assert.assertThat(boolPropType, CoreMatchers.<Class<?>>is(boolean.class));
        Assert.assertThat(charPropType, CoreMatchers.<Class<?>>is(char.class));
        
    }
    /**
     * {@link ObjectUtil#getWritablePropertyNames(Class)}のテスト。
     */
    @Test
    public void testGetWritablePropertyNames() {
        List<String> writablePropertyNames = ObjectUtil.getWritablePropertyNames(TargetClass6.class);
        Collections.sort(writablePropertyNames);
        Assert.assertThat(writablePropertyNames, contains(
                "boolProp",
                "byteProp",
                "charProp",
                "doubleProp",
                "floatProp",
                "intProp",
                "longProp",
                "shortProp"));
    }

    /**
     * {@link ObjectUtil#getPropertyNameFromSetter(java.lang.reflect.Method)}のテスト。
     */
    @Test
    public void testGetPropertyNameFromSetter() throws Throwable {
        String name = ObjectUtil.getPropertyNameFromSetter(TargetClass6.class.getMethod("setBoolProp", new Class<?>[] {boolean.class}));
        Assert.assertThat(name, is("boolProp"));
        
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
        Assert.assertThat(name, is("prop1"));
        
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
        
        Assert.assertThat(setterMethods.size(), is(1));
        Assert.assertThat(setterMethods.get(0)
                                       .getName(), is("setProp1")); 
    }
    
    @Test
    public void testGetGetterMethods() throws Exception {
        List<Method> getterMethods = ObjectUtil.getGetterMethods(TargetClass.class);
        
        Assert.assertThat(getterMethods.size(), is(1));
        Assert.assertThat(getterMethods.get(0)
                                       .getName(), is("getProp1")); 
    }

    @Test
    public void testFindMatchMethodFail() {
        Assert.assertThat(ObjectUtil.findMatchMethod(TargetClass.class, "setProp1", new Class<?>[] {int.class}),
                nullValue());
        Assert.assertThat(ObjectUtil.findMatchMethod(TargetClass.class, "setProp1", new Class<?>[] {}), nullValue());
    }

    @Test
    public void testGetSetterMethod() throws Exception {
        Assert.assertThat(ObjectUtil.getSetterMethod(TargetClass.class, "prop1"),
                is(TargetClass.class.getMethod("setProp1", new Class<?>[] {String.class})));
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
        Assert.assertThat(method1, is(o.getClass()
                                       .getMethod("getMethod1")));
        Method method2 = ObjectUtil.getGetterMethod(o.getClass(), "method2");
        Assert.assertThat(method2, is(o.getClass()
                                       .getMethod("getMethod2")));
    }

    @Test
    public void testGetGetterMethodEmpty() {
        try {
            ObjectUtil.getGetterMethod(Object.class, "");
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), is("propertyName must not null or empty."));
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
    	Assert.assertThat(names, not(hasItem("property")));
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
