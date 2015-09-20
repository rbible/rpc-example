package zx.soft.rpc.example;

import zx.soft.rpc.core.Registry;
import zx.soft.rpc.service.IService;

public class Client {

	public static void main(String[] args) {
		Registry registry = new Registry();
		IService stub = registry.lookup(IService.class, "127.0.0.1", 1313);
		System.out.println(stub.sayHello());
	}

}
