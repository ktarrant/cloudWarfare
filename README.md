# cloudWarfare
Bouncing and spiking

# Dev Setup

I use Android Studio for my development. I clone the repo, and import it as an existing project into Android Studio.

In order to get the Desktop configuration working:

1. Create a DesktopLauncher configuration.
To do this, open the Project pane, right click on the "desktop > java > com.ktarrant.cloudWarfare.desktop" module,
and select the "Run DesktopL... main()" option with the green "Play" symbol. This Run will FAIL.
2. You should see "DesktopLauncher" at the top of the screen now as the current Run configuration. Click on it and
press "Save 'DesktopLauncher' Configuration"
3. In the DesktopLauncher configuration screen, set the working directory to "<project_root>/android/assets".
This will allow the Desktop configuration find the assets.
