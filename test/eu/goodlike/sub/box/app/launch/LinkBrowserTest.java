package eu.goodlike.sub.box.app.launch;

import eu.goodlike.test.mocks.OkHttpMocks;
import eu.goodlike.test.mocks.app.LinkLauncherListenerEventType;
import eu.goodlike.test.mocks.app.LinkLauncherListenerMock;
import okhttp3.HttpUrl;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.BROWSER;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class LinkBrowserTest {

  private static final HttpUrl URL = OkHttpMocks.basicUrl();

  private final LinkLauncher browserNotAvailable = new LinkBrowser(null);

  private final Desktop desktop = Mockito.mock(Desktop.class);
  private final LinkLauncher browserMocked = new LinkBrowser(desktop);

  private final LinkLauncherListenerMock listener = new LinkLauncherListenerMock();

  @BeforeEach
  public void setup() {
    browserNotAvailable.addListener(listener);
    browserMocked.addListener(listener);
  }

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
  public void desktopCanSuccessfullyLaunchUrl() {
    browserNotAvailable.launch(URL);
    assertBrowserEvent(LinkLauncherListenerEventType.UNSUPPORTED, null);

    browserMocked.launch(URL);
    assertBrowserEvent(LinkLauncherListenerEventType.SUCCESS, null);
  }

  @Test
  public void desktopErrors() throws IOException {
    assertAssociated(LinkLauncherListenerEventType.SUDDENLY_UNSUPPORTED, UnsupportedOperationException.class);
    assertAssociated(LinkLauncherListenerEventType.SUDDENLY_UNSUPPORTED, IOException.class);
    assertAssociated(LinkLauncherListenerEventType.MISSING_PERMISSION, SecurityException.class);
    assertAssociated(LinkLauncherListenerEventType.ISSUE_WITH_URL, IllegalArgumentException.class);
    assertAssociated(LinkLauncherListenerEventType.OTHER_ERROR, RuntimeException.class);
  }

  private void assertAssociated(LinkLauncherListenerEventType associatedEventType, Class<? extends Throwable> exceptionClass) throws IOException {
    Mockito.doThrow(exceptionClass).when(desktop).browse(any(URI.class));

    browserMocked.launch(URL);

    assertBrowserEvent(associatedEventType, exceptionClass);
  }

  private void assertBrowserEvent(LinkLauncherListenerEventType eventType, Class<? extends Throwable> exceptionClass) {
    listener.assertEventMatches(
        eventType,
        BROWSER,
        URL,
        exceptionClass == null
            ? AbstractAssert::isNull
            : e -> e.isInstanceOf(exceptionClass)
    );
  }

}
