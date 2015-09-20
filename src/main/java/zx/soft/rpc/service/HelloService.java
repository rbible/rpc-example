package zx.soft.rpc.service;


public class HelloService implements IService {

	@Override
	public String sayHello() {
		return "Hello, world!";
	}

}
