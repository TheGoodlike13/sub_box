package eu.goodlike.sub.box.app;

import com.google.api.client.http.HttpTransport;
import eu.goodlike.sub.box.app.launch.LinkLauncher;
import eu.goodlike.sub.box.app.launch.LinkLauncherListener;

public interface ApplicationProfile {

  ApplicationUi getUi();

  String getApplicationName();

  HttpTransport getHttpTransport();

  LinkLauncher getDefaultLauncher();

  LinkLauncher getBackupLauncher();

  LinkLauncherListener getLauncherListener();

}
