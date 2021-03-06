/*
 Source:
 http://www.programcreek.com/java-api-examples/index.php?source_dir=ipdburt-master/iddb-web/src/main/java/jipdbs/xmlrpc/InstanceHandlerMapping.java

 This class is needed for communication between java and any other language based XML-RPC
 */
package javarpcimpl;

import java.lang.reflect.Method;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;

public class InstanceHandlerMapping implements XmlRpcHandlerMapping {

    private final Object instance;

    public InstanceHandlerMapping(Object instance) {
        this.instance = instance;
    }

    @Override
    public XmlRpcHandler getHandler(String methodName) throws XmlRpcNoSuchHandlerException, XmlRpcException {
        return new XmlRpcHandler() {

            @Override
            public Object execute(XmlRpcRequest req) throws XmlRpcException {
                try {
                    /*
                     * For backward compatibility. Strip out namespace
                     */
                    String[] methods = req.getMethodName().split("\\.");
                    String methodName = methods[methods.length - 1];

                    Class<?>[] types = new Class<?>[req.getParameterCount()];
                    Object[] args = new Object[req.getParameterCount()];

                    for (int i = 0; i < args.length; i++) {
                        Object param = req.getParameter(i);
                        args[i] = param;
                        types[i] = param.getClass();
                    }

                    Method method = instance.getClass().getMethod(methodName, types);
//                    System.out.println(method.getName());
                    Object result = method.invoke(instance, args);

                    return result != null ? result : "";
                } catch (Exception e) {
                    System.out.println("Some Exception");
                    throw new XmlRpcException(e.getMessage());
                }
            }
        };
    }
}
