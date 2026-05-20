System Interactions is a mod that lets Minecraft interact with your system in a safe and controlled way.

Want to show a real system dialog? Send a Windows notification? Make one specific player question reality for a moment?  
System Interactions adds a small set of server-triggered desktop effects that can be used from commands, command blocks, datapacks, maps, or server-side logic.

**What can it do?**

- Show native system dialog windows
- Show Windows notifications
- Target specific players with Minecraft selectors
- Use `info`, `warning`, and `error` styles
- Work from commands, command blocks, and datapacks
- Create weird, immersive, or unexpected moments without custom client scripting

**Windows notification**:
![Toast example image](https://cdn.modrinth.com/data/cached_images/fd673a0ad0a40ecabbb6df87da6b788e43f519cb.png)

**Dialog**:
![Dialog example image](https://cdn.modrinth.com/data/cached_images/ccf49fb0b23f1527d751130d36b00071b025827a_0.webp)

**Commands**

System dialog:

```
/si dialog <targets> <type> <title> <message>
```

Windows notification:

```
/si toast <targets> <type> <title> <message>
```

**How it works?**

System Interactions does not let servers run arbitrary scripts or system commands.

On Windows, the mod uses PowerShell only as a bridge to call built-in Windows UI features, such as system message boxes. Windows notifications are displayed through a small bundled helper application, so they can appear with the correct app name.

Servers can only request two predefined effects - system dialogs and Windows notifications.

The mod only sends plain text values such as title, message, and icon type.
It does not provide any command for running PowerShell code, shutting down the device, reading files, downloading files, or launching arbitrary programs.

**Compatibility**

The mod has been properly tested on Windows 11.
Windows 10 support is expected, but not fully tested yet.

Windows notifications may not appear if notifications are disabled in system settings, Focus Assist / Do Not Disturb is enabled, or notifications from the helper application are blocked by Windows.
