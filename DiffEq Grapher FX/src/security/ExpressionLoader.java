package security;
 
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
 
public class ExpressionLoader extends URLClassLoader
{
 
        public ExpressionLoader(URL[] urls)
        {
                super(urls);
        }
 
        public ExpressionLoader(URL[] urls, ClassLoader parent)
        {
                super(urls, parent);
        }
 
        public ExpressionLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory)
        {
                super(urls, parent, factory);
        }
       
        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
                switch(name)
                {
                case "java.lang.Object":
                case "java.util.function.DoubleBinaryOperator":
                case "java.lang.Math":
                case "java.util.Random":
                case "input.Expression":
                return super.loadClass(name);
                default: throw new SecurityException(name + "is not allowed in expressions");
                }
        }
}