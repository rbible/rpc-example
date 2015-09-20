package zx.soft.rpc.example;

import zx.soft.rpc.core.Registry;
import zx.soft.rpc.service.HelloService;
import zx.soft.rpc.service.IService;

public class Server implements IService {

    public static void main(String[] args) {
        Registry registry = new Registry();
        registry.register(new HelloService(), 1313);
        System.err.println("Server ready");
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }
}
