package zx.soft.rpc.service;


public class HelloService implements IService {

    @Override
    public String sayHello() {
        String msg = "Hello, world!";
        System.out.println("server: " + msg);
        return "client " + msg;
    }

}
