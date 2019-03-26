package eu.goodlike.sub.box.app.launch;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.awt.*;

import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkBrowserTest {

  private final LinkLauncher browserNotAvailable = new LinkBrowser(null);

  private final Desktop desktop = Mockito.mock(Desktop.class);
  private final LinkLauncher browserMocked = new LinkBrowser(desktop);

  private final LinkLauncherListener listener = Mockito.mock(LinkLauncherListener.class,
      (InvocationOnMock invocation) -> {throw new AssertionError(String.valueOf(invocation));});

  @Test
  public void isAvailable() {
    assertThat(browserNotAvailable.isAvailable()).isFalse();
    assertThat(browserMocked.isAvailable()).isTrue();
  }

  @Test
  public void nullUrl() {
    assertNotNull("url", browserNotAvailable::launch);
    assertNotNull("url", browserMocked::launch);
  }

  @Test
  public void nullListener() {
    assertNotNull("listener", browserNotAvailable::addListener);
    assertNotNull("listener", browserMocked::addListener);
  }

  @Test
  public void browserNotSupported() {

  }

}
