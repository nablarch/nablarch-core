package nablarch.core.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link ResourcesUtil}のテストクラス。
 */
public class ResourcesUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * パッケージにnullを指定した場合、空が戻されること
     */
    @Test
    public void getResourcesType_specifiedNullInPackage() throws Exception {
        ResourcesUtil.Resources[] result = ResourcesUtil.getResourcesTypes(null);
        assertThat(result, arrayWithSize(0));
    }

    /**
     * 存在しないパッケージを指定した場合、空が戻されること
     */
    @Test
    public void getResourcesType_specifiedNotFundPackage() throws Exception {
        ResourcesUtil.Resources[] result = ResourcesUtil.getResourcesTypes("notfound.packagename");
        assertThat(result, arrayWithSize(0));
    }

    /**
     * 指定したパッケージ配下のクラスが取得出来ること
     */
    @Test
    public void getResourcesType_returnedUnderSpecifiedPackageClass() throws Exception {
        ResourcesUtil.Resources[] types = ResourcesUtil.getResourcesTypes("nablarch.core.util.classes");

        assertThat(types, arrayWithSize(1));

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();
        for (ResourcesUtil.Resources type : types) {
            type.forEach(new ClassTraversal.ClassHandler() {
                @Override
                public void process(String packageName, String className) {
                    classes.add(new ClassHolder(packageName, className));
                }
            });
            type.close();
        }

        assertThat(classes, containsInAnyOrder(
                new ClassHolder("nablarch.core.util.classes", "Component1"),
                new ClassHolder("nablarch.core.util.classes", "Component2"),
                new ClassHolder("nablarch.core.util.classes.sub", "SubComponent")
        ));
    }

    /**
     * 指定したパッケージがjarファイル内にある場合でもそのクラスが取得出来ること
     */
    @Test
    public void getResourcesType_specifiedPackageInJarFile() throws Exception {
        ResourcesUtil.Resources[] result = ResourcesUtil.getResourcesTypes("org.junit");

        assertThat("junitの2jarが対象", result, arrayWithSize(1));

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();
        for (ResourcesUtil.Resources resources : result) {
            resources.forEach(new ClassTraversal.ClassHandler() {
                @Override
                public void process(String packageName, String className) {
                    classes.add(new ClassHolder(packageName, className));
                }
            });
            resources.close();
        }
        assertThat(new ClassHolder("org.junit", "Before"), isIn(classes));
        assertThat(new ClassHolder("org.junit", "After"), isIn(classes));
        assertThat(new ClassHolder("org.junit.rules", "TemporaryFolder"), isIn(classes));
    }

    /**
     * 指定したパッケージがzipファイル内にある場合でもそのクラスが取得出来ること
     */
    @Test
    public void getResourcesType_specifiedPackageInZipFile() throws Exception {
        final URL mockUrl = mock(URL.class);

        // zipプロトコルのURLを作れないため、モックを使う。
        when(mockUrl.getProtocol()).thenReturn("zip");
        when(mockUrl.getPath()).thenReturn("src/test/java/nablarch/core/util/classes/Test.zip!/package2");

        // ClassLoaderをモックにできないため、テスト中だけ入れ替える。
        final ClassLoader originalCurrentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new ClassLoader() {
            @Override
            public Enumeration<URL> getResources(String name) {
                return Collections.enumeration(Arrays.asList(mockUrl));
            }
        });

        try {
            ResourcesUtil.Resources[] result = ResourcesUtil.getResourcesTypes("package2");

            assertThat("Test.zipが対象", result, arrayWithSize(1));

            final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();
            for (ResourcesUtil.Resources resources : result) {
                resources.forEach(new ClassTraversal.ClassHandler() {
                    @Override
                    public void process(String packageName, String className) {
                        classes.add(new ClassHolder(packageName, className));
                    }
                });
                resources.close();
            }
            assertThat(classes, containsInAnyOrder(
                    new ClassHolder("package2", "Class1"),
                    new ClassHolder("package2.subpackage", "Class1"),
                    new ClassHolder("package2.subpackage", "Class2")
            ));
        } finally {
            // 必ず元のClassLoaderに戻す。
            Thread.currentThread().setContextClassLoader(originalCurrentLoader);
        }
    }

    /**
     * 指定したパッケージがzipファイル内にある場合でもそのクラスが取得出来ること
     */
    @Test
    public void getResourcesType_unsupportedProtocol() throws Exception {
        final URL mockUrl = mock(URL.class);

        // zipプロトコルのURLを作れないため、モックを使う。
        when(mockUrl.getProtocol()).thenReturn("hoge");

        // ClassLoaderをモックにできないため、テスト中だけ入れ替える。
        final ClassLoader originalCurrentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new ClassLoader() {
            @Override
            public Enumeration<URL> getResources(String name) {
                return Collections.enumeration(Arrays.asList(mockUrl));
            }
        });

        try {
            ResourcesUtil.Resources[] result = ResourcesUtil.getResourcesTypes("package");
            assertThat("対応していないプロトコルなのでリソースは取得できない", result, emptyArray());
        } finally {
            Thread.currentThread().setContextClassLoader(originalCurrentLoader);
        }
    }

    /**
     * {@link ResourcesUtil#toJarFile(URL)}にサポートしないデータタイプを指定した場合、{@link IllegalArgumentException}が送出されること。
     */
    @Test
    public void toJarFile_unsupportedType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("url must be JarURLConnection class. specified url class=[java.net.URL]");
        ResourcesUtil.toJarFile(new URL("file://."));
    }

    /**
     * パッケージ名とクラス名を保持するクラス。
     */
    private static class ClassHolder {

        private final String packageName;

        private final String className;

        public ClassHolder(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ClassHolder holder = (ClassHolder) o;

            if (packageName != null ? !packageName.equals(holder.packageName) : holder.packageName != null) {
                return false;
            }
            return className.equals(holder.className);

        }

        @Override
        public int hashCode() {
            int result = packageName != null ? packageName.hashCode() : 0;
            result = 31 * result + className.hashCode();
            return result;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ClassHolder{");
            sb.append("packageName='")
              .append(packageName)
              .append('\'');
            sb.append(", className='")
              .append(className)
              .append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
