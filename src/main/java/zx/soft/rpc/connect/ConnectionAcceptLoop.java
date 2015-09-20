package zx.soft.rpc.connect;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import zx.soft.rpc.serial.ISerializer;
import zx.soft.rpc.util.Utils;

public class ConnectionAcceptLoop implements Runnable {

    private final Object serviceInstance;

    private final Map<Integer, Method> code2MethodMap;

    private final Map<Method, Class<?>[]> method2ParamsTypeMap;

    private final ISerializer serializer;

    private final int port;

    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    public ConnectionAcceptLoop(Object serviceInstance, ISerializer serializer, int port) {
        this.serviceInstance = serviceInstance;
        code2MethodMap = Utils.getCode2methodMap(serviceInstance.getClass());
        method2ParamsTypeMap = Utils.getMethod2ParamsType(serviceInstance.getClass());
        this.serializer = serializer;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            serverSocket.setReceiveBufferSize(1024 * 64);
            serverSocket.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                int n = selector.select();
                if (n == 0) {
                    continue;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        channel.socket().setTcpNoDelay(true);
                        if (channel != null) {
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                    if (key.isReadable()) {
                        pool.execute(new ConnectionHandler(key, this));
                    }
                    it.remove();
                }
                Thread.yield();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeObject(OutputStream outputStream, Class<?> type, Object obj) {
        serializer.writeObject(outputStream, type, obj);
    }

    public Method getMethod(int code) {
        return code2MethodMap.get(code);
    }

    public Class<?>[] getParameterTypes(Method method) {
        return method2ParamsTypeMap.get(method);
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }

    public Object[] readObjects(InputStream in, Class<?>[] types) {
        return serializer.readObjects(in, types);
    }

}
