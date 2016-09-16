package nablarch.core.util;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.JarFile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link ClassTraversal}のテストクラス。
 */
public class ClassTraversalTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * 指定したパッケージが存在しない場合、コールバックは実行されずに正常に終わること。
     */
    @Test
    public void forEachFile_notFoundPackage() throws Exception {
        File rootDir = new File("src/test/java/nablarch/core/util/classes");

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();

        ClassTraversal.forEach(rootDir, "notfound", new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });

        assertThat(classes, empty());
    }

    /**
     * トップレベルのパッケージを指定した場合でも正しく処理できること
     */
    @Test
    public void forEachFile_specifiedTopLevelPackage() throws Exception {
        File rootDir = new File("src/test/java/nablarch/core/util/classes");

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();

        ClassTraversal.forEach(rootDir, null, new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });

        assertThat(classes, containsInAnyOrder(
                new ClassHolder("package1", "Class1"),
                new ClassHolder("package1", "Class2"),
                new ClassHolder("package2", "Class1"),
                new ClassHolder("package2.subpackage", "Class1"),
                new ClassHolder("package2.subpackage", "Class2")
        ));
    }

    /**
     * 指定したルートディレクトリ配下にあるパッケージ内のクラスが処理できること。
     */
    @Test
    public void forEachFile_specifiedPackage() throws Exception {
        File rootDir = new File("src/test/java/nablarch/core/util/classes");

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();

        ClassTraversal.forEach(rootDir, "package1", new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });

        assertThat(classes, containsInAnyOrder(
                new ClassHolder("package1", "Class1"),
                new ClassHolder("package1", "Class2")
        ));
    }

    /**
     * 指定したルートディレクトリ配下にあるパッケージ内のクラスが再帰的に処理できること
     */
    @Test
    public void forEachFile_recursivePackage() throws Exception {
        File rootDir = new File("src/test/java/nablarch/core/util/classes");

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();

        ClassTraversal.forEach(rootDir, "package2", new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });

        assertThat(classes, containsInAnyOrder(
                new ClassHolder("package2", "Class1"),
                new ClassHolder("package2.subpackage", "Class1"),
                new ClassHolder("package2.subpackage", "Class2")
        ));
    }

    /**
     * 指定したルートディレクトリ配下にあるパッケージ内(サブパッケージまで指定)のクラスが処理できること
     */
    @Test
    public void forEachFile_multiplePackage() throws Exception {
        File rootDir = new File("src/test/java/nablarch/core/util/classes");

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();

        ClassTraversal.forEach(rootDir, "package2.subpackage", new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });

        assertThat(classes, containsInAnyOrder(
                new ClassHolder("package2.subpackage", "Class1"),
                new ClassHolder("package2.subpackage", "Class2")
        ));
    }

    /**
     * jarファイル内のクラスが処理できること
     */
    @Test
    public void forEachJar_allClassFile() throws Exception {
        JarFile jarFile = new JarFile(new File("src/test/java/nablarch/core/util/classes/Test.jar"));

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();
        ClassTraversal.forEach(jarFile, new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });
        assertThat(classes, containsInAnyOrder(
                new ClassHolder("package1", "Class1"),
                new ClassHolder("package1", "Class2"),
                new ClassHolder("package2", "Class1"),
                new ClassHolder("package2.subpackage", "Class1"),
                new ClassHolder("package2.subpackage", "Class2")
        ));
    }

    /**
     * warファイルの場合、WEB-INF/classes配下が処理されること
     */
    @Test
    public void forEachWar_allClassFile() throws Exception {
        JarFile warFile = new JarFile(new File("src/test/java/nablarch/core/util/classes/Test.war"));

        final ArrayList<ClassHolder> classes = new ArrayList<ClassHolder>();
        ClassTraversal.forEach(warFile, new ClassTraversal.ClassHandler() {
            @Override
            public void process(String packageName, String className) {
                classes.add(new ClassHolder(packageName, className));
            }
        });
        assertThat(classes, containsInAnyOrder(
                new ClassHolder(null, "Main"),
                new ClassHolder("package1", "Class1"),
                new ClassHolder("package1", "Class2"),
                new ClassHolder("package2", "Class1"),
                new ClassHolder("package2.subpackage", "Class1"),
                new ClassHolder("package2.subpackage", "Class2")
        ));
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
