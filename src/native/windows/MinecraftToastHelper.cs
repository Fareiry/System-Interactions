using System;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.Drawing;

[assembly: AssemblyTitle("Minecraft")]
[assembly: AssemblyProduct("Minecraft")]
[assembly: AssemblyDescription("Minecraft")]
[assembly: AssemblyCompany("Mojang Studios")]
[assembly: AssemblyVersion("1.0.0.0")]
[assembly: AssemblyFileVersion("1.0.0.0")]

public static class MinecraftToastHelper {
    [STAThread]
    public static void Main(string[] args) {
        if (args.Length < 3) {
            return;
        }

        string title = Decode(args[0]);
        string message = Decode(args[1]);
        string iconName = Decode(args[2]);

        Application.EnableVisualStyles();
        Application.SetCompatibleTextRenderingDefault(false);

        ToolTipIcon balloonIcon = ToolTipIcon.Info;
        Icon systemIcon = SystemIcons.Information;

        if (iconName == "Error") {
            balloonIcon = ToolTipIcon.Error;
            systemIcon = SystemIcons.Error;
        }
        else if (iconName == "Warning") {
            balloonIcon = ToolTipIcon.Warning;
            systemIcon = SystemIcons.Warning;
        }

        using (NotifyIcon notify = new NotifyIcon()) {
            notify.Icon = systemIcon;
            notify.Text = "Minecraft";
            notify.BalloonTipTitle = title;
            notify.BalloonTipText = message;
            notify.BalloonTipIcon = balloonIcon;
            notify.Visible = true;
            notify.ShowBalloonTip(7000);

            DateTime end = DateTime.Now.AddSeconds(9);
            while (DateTime.Now < end) {
                Application.DoEvents();
                Thread.Sleep(100);
            }

            notify.Visible = false;
        }
    }

    private static string Decode(string base64) {
        return Encoding.UTF8.GetString(Convert.FromBase64String(base64));
    }
}
