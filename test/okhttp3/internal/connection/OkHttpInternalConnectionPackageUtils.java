package okhttp3.internal.connection;

import okhttp3.OkHttpClient;
import okhttp3.internal.Internal;

public final class OkHttpInternalConnectionPackageUtils {

  @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
  public static void addFakeConnectionUsingInternalApi(OkHttpClient client) {
    RealConnectionPool realConnectionPool = Internal.instance.realConnectionPool(client.connectionPool());
    synchronized (realConnectionPool) {
      realConnectionPool.put(new RealConnection(realConnectionPool, null));
    }
  }

  private OkHttpInternalConnectionPackageUtils() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
