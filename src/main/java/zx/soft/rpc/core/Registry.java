package zx.soft.rpc.core;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Proxy;
import java.util.Properties;

import zx.soft.rpc.connect.ConnectionAcceptLoop;
import zx.soft.rpc.serial.ISerializer;
import zx.soft.rpc.serial.JavaSerializer;
import zx.soft.rpc.serial.KryoSerializer;

public class Registry {

    private final ISerializer serializer;

    public Registry() {
        Properties properties = new Properties();
        File confFile = new File("./simpleRpc.properties");
        try {
            if (confFile.exists()) {
                properties.load(new FileReader(confFile));
                System.out.println("Load config: " + confFile.getAbsolutePath());
            } else {
                properties.load(this.getClass().getClassLoader().getResourceAsStream("simpleRpc.properties"));
                System.out.println("Load config from CLASSPATH.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String serializerProp = properties.getProperty("serializer");
        if (serializerProp.equals("java")) {
            serializer = new JavaSerializer();
        } else if (serializerProp.equals("kryo")) {
            serializer = new KryoSerializer();
        } else {
            throw new RuntimeException("Unsupported serializer: " + serializerProp);
        }
        System.out.println("serializer: " + serializerProp);

    }

    public Registry(ISerializer serializer) {
        this.serializer = serializer;
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> serviceType, String host, int port) {
        RemoteObjectInvocationHandler handler = new RemoteObjectInvocationHandler(host, port, serviceType, serializer);
        return (T) Proxy.newProxyInstance(serviceType.getClassLoader(), new Class[] { serviceType }, handler);
    }

    public <T> void register(T serviceInstance, int port) {
        try {
            new Thread(new ConnectionAcceptLoop(serviceInstance, serializer, port)).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
