package eu.goodlike.sub.box.app.launch;

import eu.goodlike.test.mocks.OkHttpMocks;
import eu.goodlike.test.mocks.app.LinkLauncherListenerEventType;
import eu.goodlike.test.mocks.app.LinkLauncherListenerMock;
import okhttp3.HttpUrl;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.CLIPBOARD;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.app.LinkLauncherListenerEventType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class LinkPasteTest {

  private static final HttpUrl URL = OkHttpMocks.basicUrl();

  private final LinkLauncher clipboardNotAvailable = new LinkPaste(null);

  private final Clipboard clipboard = Mockito.mock(Clipboard.class);
  private final LinkLauncher clipboardMocked = new LinkPaste(clipboard);

  private final LinkLauncherListenerMock listener = new LinkLauncherListenerMock();

  @BeforeEach
  public void setup() {
    clipboardNotAvailable.addListener(listener);
    clipboardMocked.addListener(listener);
  }

  @Test
  public void isAvailable() {
    assertThat(clipboardNotAvailable.isAvailable()).isFalse();
    assertThat(clipboardMocked.isAvailable()).isTrue();
  }

  @Test
  public void nullUrl() {
    assertNotNull("url", clipboardNotAvailable::launch);
    assertNotNull("url", clipboardMocked::launch);
  }

  @Test
  public void nullListener() {
    assertNotNull("listener", clipboardNotAvailable::addListener);
    assertNotNull("listener", clipboardMocked::addListener);
  }

  @Test
  public void desktopCanSuccessfullyLaunchUrl() {
    clipboardNotAvailable.launch(URL);
    assertClipboardEvent(UNSUPPORTED, null);

    clipboardMocked.launch(URL);
    assertClipboardEvent(SUCCESS, null);
  }

  @Test
  public void desktopErrors() {
    assertAssociated(SUDDENLY_UNSUPPORTED, IllegalStateException.class);
    assertAssociated(OTHER_ERROR, RuntimeException.class);
  }

  private void assertAssociated(LinkLauncherListenerEventType associatedEventType, Class<? extends Throwable> exceptionClass) {
    Mockito.doThrow(exceptionClass).when(clipboard).setContents(any(Transferable.class), any(ClipboardOwner.class));

    clipboardMocked.launch(URL);

    assertClipboardEvent(associatedEventType, exceptionClass);
  }

  private void assertClipboardEvent(LinkLauncherListenerEventType eventType, Class<? extends Throwable> exceptionClass) {
    listener.assertEventMatches(
        eventType,
        CLIPBOARD,
        URL,
        exceptionClass == null
            ? AbstractAssert::isNull
            : e -> e.isInstanceOf(exceptionClass)
    );
  }

}
