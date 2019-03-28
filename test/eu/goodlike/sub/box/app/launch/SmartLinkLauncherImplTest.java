package eu.goodlike.sub.box.app.launch;

import com.google.common.collect.ImmutableList;
import eu.goodlike.test.mocks.OkHttpMocks;
import eu.goodlike.test.mocks.app.LinkLauncherListenerMock;
import okhttp3.HttpUrl;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.OTHER;
import static eu.goodlike.test.mocks.app.LinkLauncherListenerEventType.UNSUPPORTED;
import static org.assertj.core.api.Assertions.assertThat;

public class SmartLinkLauncherImplTest {

  private static final HttpUrl URL = OkHttpMocks.basicUrl();

  private final LinkLauncher unavailable = new LinkBrowser(null);

  private final LinkLauncher launcher1 = Mockito.mock(LinkLauncher.class);
  private final LinkLauncher launcher2 = Mockito.mock(LinkLauncher.class);
  private final LinkLauncher launcher3 = Mockito.mock(LinkLauncher.class);

  private final LinkLauncherListenerMock listener = new LinkLauncherListenerMock();

  private SmartLinkLauncher launcher;

  @BeforeEach
  public void setup() {
    Mockito.when(launcher1.isAvailable()).thenReturn(true);
    Mockito.when(launcher2.isAvailable()).thenReturn(true);
    Mockito.when(launcher3.isAvailable()).thenReturn(true);

    launcher = new SmartLinkLauncherImpl(launcher1, launcher2, launcher3);
    launcher.addListener(listener);
  }

  @Test
  public void unavailableLaunchers() {
    List<SmartLinkLauncher> unavailableLaunchers = ImmutableList.of(
        new SmartLinkLauncherImpl(null),
        new SmartLinkLauncherImpl(unavailable),

        new SmartLinkLauncherImpl(null, (LinkLauncher[])null),
        new SmartLinkLauncherImpl(unavailable, (LinkLauncher[])null),

        new SmartLinkLauncherImpl(null, (LinkLauncher)null),
        new SmartLinkLauncherImpl(unavailable, (LinkLauncher)null),
        new SmartLinkLauncherImpl(null, unavailable),
        new SmartLinkLauncherImpl(unavailable, unavailable),

        new SmartLinkLauncherImpl(null, null, null),
        new SmartLinkLauncherImpl(unavailable, null, null),
        new SmartLinkLauncherImpl(null, unavailable, null),
        new SmartLinkLauncherImpl(null, null, unavailable),
        new SmartLinkLauncherImpl(unavailable, unavailable, null),
        new SmartLinkLauncherImpl(unavailable, null, unavailable),
        new SmartLinkLauncherImpl(null, unavailable, unavailable),
        new SmartLinkLauncherImpl(unavailable, unavailable, unavailable)
    );

    for (SmartLinkLauncher launcher : unavailableLaunchers) {
      assertThat(launcher.isAvailable()).isFalse();

      launcher.addListener(listener);
      launcher.launch(URL);
      listener.assertEventMatches(UNSUPPORTED, OTHER, URL, AbstractAssert::isNull);
    }
  }

  @Test
  public void availableLaunchersDefault() {
    launcher.launch(URL);

    Mockito.verify(launcher1).launch(URL);
  }

  @Test
  public void availableLaunchersAdjustOnce() {
    launcher.nextDefaultLauncher();

    launcher.launch(URL);

    Mockito.verify(launcher2).launch(URL);
  }

  @Test
  public void availableLaunchersAdjustTwice() {
    launcher.nextDefaultLauncher();
    launcher.nextDefaultLauncher();

    launcher.launch(URL);

    Mockito.verify(launcher3).launch(URL);
  }

  @Test
  public void availableLaunchersAdjustThrice() {
    launcher.nextDefaultLauncher();
    launcher.nextDefaultLauncher();
    launcher.nextDefaultLauncher();

    launcher.launch(URL);

    Mockito.verify(launcher1).launch(URL);
  }

}
