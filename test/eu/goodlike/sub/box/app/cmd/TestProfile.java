package eu.goodlike.sub.box.app.cmd;

import com.google.api.client.http.HttpTransport;
import eu.goodlike.sub.box.app.ApplicationProfile;
import eu.goodlike.sub.box.app.ApplicationUi;
import eu.goodlike.sub.box.app.launch.LinkLauncher;
import eu.goodlike.sub.box.app.launch.LinkLauncherListener;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import okhttp3.HttpUrl;
import org.assertj.core.api.AbstractThrowableAssert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public final class TestProfile implements ApplicationProfile {

  private final ApplicationUi ui = Mockito.mock(ApplicationUi.class);
  private final LinkLauncher defaultLauncher = Mockito.mock(LinkLauncher.class);
  private final LinkLauncher fallbackLauncher = Mockito.mock(LinkLauncher.class);
  private final LinkLauncherListener launcherListener = Mockito.mock(LinkLauncherListener.class);

  public TestProfile() {
    initializeMocks();
  }

  public ApplicationUi ui() {
    return ui(1);
  }

  public ApplicationUi ui(int times) {
    return Mockito.verify(ui, times(times));
  }

  public void uiWarn(String description, Consumer<AbstractThrowableAssert<?, ?>> assertOnException) {
    ArgumentCaptor<YoutubeWarningException> exCaptor = ArgumentCaptor.forClass(YoutubeWarningException.class);

    ui().signalSubscribableWarning(eq(description), exCaptor.capture());

    assertOnException.accept(assertThat(exCaptor.getValue()));
  }

  public void launched(HttpUrl url, boolean usedBrowser) {
    launched(url, usedBrowser, !usedBrowser);
  }

  public void launched(HttpUrl url, boolean usedBrowser, boolean usedClipboard) {
    Mockito.verify(defaultLauncher, usedBrowser ? times(1) : never()).launch(url);
    Mockito.verify(fallbackLauncher, usedClipboard ? times(1) : never()).launch(url);
  }

  public void reset() {
    Mockito.reset(ui, defaultLauncher, fallbackLauncher, launcherListener);
    initializeMocks();
  }

  @Override
  public ApplicationUi getUi() {
    return ui;
  }

  @Override
  public String getApplicationName() {
    return "test_sub_box";
  }

  @Override
  public HttpTransport getHttpTransport() {
    return new MockHttpTransport(CmdApplication.class);
  }

  @Override
  public LinkLauncher getDefaultLauncher() {
    return defaultLauncher;
  }

  @Override
  public LinkLauncher getBackupLauncher() {
    return fallbackLauncher;
  }

  @Override
  public LinkLauncherListener getLauncherListener() {
    return launcherListener;
  }

  private void initializeMocks() {
    Mockito.when(defaultLauncher.isAvailable()).thenReturn(true);
    Mockito.when(fallbackLauncher.isAvailable()).thenReturn(true);

    Mockito.when(defaultLauncher.launch(any(HttpUrl.class))).thenReturn(true);
    Mockito.when(fallbackLauncher.launch(any(HttpUrl.class))).thenReturn(true);
  }

}
