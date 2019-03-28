package eu.goodlike.test.mocks.app;

import eu.goodlike.sub.box.app.launch.LinkLauncherListener;
import eu.goodlike.sub.box.app.launch.LinkLauncherType;
import okhttp3.HttpUrl;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;

import java.util.function.Consumer;

import static eu.goodlike.test.mocks.app.LinkLauncherListenerEventType.*;

public final class LinkLauncherListenerMock implements LinkLauncherListener {

  private LinkLauncherListenerEventType eventType;
  private LinkLauncherType launcherType;
  private HttpUrl url;
  private Exception e;

  public void assertEventMatches(LinkLauncherListenerEventType expectedEventType,
                                 LinkLauncherType expectedLauncherType,
                                 HttpUrl expectedUrl,
                                 Consumer<ThrowableAssert> expectedExceptionAssert) {
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(eventType).isEqualTo(expectedEventType);
    softly.assertThat(launcherType).isEqualTo(expectedLauncherType);
    softly.assertThat(url).isEqualTo(expectedUrl);
    expectedExceptionAssert.accept(softly.assertThat(e));
    softly.assertAll();
  }

  @Override
  public void onSuccess(LinkLauncherType type, HttpUrl url) {
    set(SUCCESS, type, url, null);
  }

  @Override
  public void onUnsupported(LinkLauncherType type, HttpUrl url) {
    set(UNSUPPORTED, type, url, null);
  }

  @Override
  public void onSuddenlyUnsupported(LinkLauncherType type, HttpUrl url, Exception e) {
    set(SUDDENLY_UNSUPPORTED, type, url, e);
  }

  @Override
  public void onMissingPermission(LinkLauncherType type, HttpUrl url, Exception e) {
    set(MISSING_PERMISSION, type, url, e);
  }

  @Override
  public void onIssueWithUrl(LinkLauncherType type, HttpUrl url, Exception e) {
    set(ISSUE_WITH_URL, type, url, e);
  }

  @Override
  public void onOtherError(LinkLauncherType type, HttpUrl url, Exception e) {
    set(OTHER_ERROR, type, url, e);
  }

  private void set(LinkLauncherListenerEventType eventType, LinkLauncherType launcherType, HttpUrl url, Exception e) {
    this.eventType = eventType;
    this.launcherType = launcherType;
    this.url = url;
    this.e = e;
  }

}
