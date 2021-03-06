package zx.soft.rpc.serial;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializer {

	Object readObject(InputStream inputStream, Class<?> type);

	Object[] readObjects(InputStream inputStream, Class<?>[] types);

	void writeObject(OutputStream outputStream, Class<?> type, Object obj);

	void writeObjects(OutputStream outputStream, Class<?>[] types, Object[] objs);

}
