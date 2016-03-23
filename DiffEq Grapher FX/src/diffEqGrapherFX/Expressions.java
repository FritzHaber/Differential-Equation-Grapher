package diffEqGrapherFX;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import security.ExpressionLoader;

import diffEqGrapherFX.InputParser;

public class Expressions {
  private static int num = 0;

  public static DoubleBinaryOperator compile(String expression) {
    num++;
    String name = "Expression" + num;
    expression = javify(expression);
    System.out.println(expression);
    File temp;
    temp = new File("Machintosh HD/Users/Hank/Desktop/input/" + name + ".java");
    // temp.mkdirs();
    // temp.delete();
    try {
      temp.createNewFile();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    temp.deleteOnExit();
    try (PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(temp)))) {
      out.println("package input;");
      out.println("import java.util.function.DoubleBinaryOperator;");
      // out.println("import static java.lang.Math.*;");
      out.println("public class " + name + " implements DoubleBinaryOperator");
      out.println("{");
      // out.println("\tpublic double ln(double x)");
      // out.println("\t{");
      // out.println("\t\treturn log(x);");
      // out.println("\t}");
      out.println("\tpublic double applyAsDouble(double x, double y)");
      out.println("\t{");
      out.printf("\t\treturn %s;\n", expression);
      out.println("\t}");
      out.println("}");
      out.flush();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager fileManager = javac.getStandardFileManager(diagnostics, null, null);

    Iterable<? extends JavaFileObject> compilationUnit =
        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(temp));


    List<String> options = new ArrayList<>();


    JavaCompiler.CompilationTask task =
        javac.getTask(null, fileManager, diagnostics, options, null, compilationUnit);
    if (task.call()) {
      try (ExpressionLoader classLoader =
          new ExpressionLoader(new URL[] {new File("./src/").toURI().toURL()})) {
        classLoader.setName("input." + name);
        // Load the class from the classloader by name....
        Class<?> loadedClass = classLoader.loadClass("input." + name);
        // Create a new instance...
        Object obj = loadedClass.newInstance();
        return (DoubleBinaryOperator) obj;

      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
          | IOException | ClassCastException e) {
        throw new RuntimeException(e);
      }
    } else
      throw new RuntimeException();

  }

  private static String javify(String expression) {
    return InputParser.parse(expression);
  }

}
