package zx.soft.rpc.example;

import zx.soft.rpc.core.Registry;
import zx.soft.rpc.service.HelloService;

public class Server {

    public static void main(String[] args) {
        Registry registry = new Registry();
        registry.register(new HelloService(), 1313);
        System.err.println("Server ready");
    }
}
