package gui;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import kussmaulUtils.CalebKussmaul;
import kussmaulUtils.ImageTools;
import kussmaulUtils.Program;
import kussmaulUtils.UpdateFrame;

@SuppressWarnings("restriction")
public class GIFKR {

	private static final String currentVersion = "2.0.3";
	//private static String[] restartArgs;

	public static void main(String[] args) {
		//restartArgs = args;

		SwingUtilities.invokeLater(() -> {

			try {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}

			String latestVersion = CalebKussmaul.getLatestVersion(Program.GIFKR2);
			String status;
			boolean outdated = false;

			if(CalebKussmaul.isOnline()) {
				if(currentVersion.startsWith("DEV") || latestVersion.equals(currentVersion)) {
					status = "";
				}
				else {
					outdated = true;
					status = " (OUTDATED)";
				}
			}
			else
				status = " (offline)";

			CreditsFrame cf = new CreditsFrame();
			cf.setVisible(GIFKRPrefs.showCreditsFrame());
			
			JFrame f = new JFrame("GIFKR v" + currentVersion + status);
			f.getRootPane().putClientProperty("apple.awt.fullscreenable", true);

			BufferedImage icon = ImageTools.getResourceImage("icon.png");

			f.setIconImage(icon);

			f.setSize(new Dimension(1200, 600));

			MainPanel mp = new MainPanel();
			f.setJMenuBar(mp.createMenuBar());
			f.setContentPane(mp);

			//f.pack();
			f.setLocationRelativeTo(null);
			f.setVisible(true);
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			if(outdated) {
				UpdateFrame uFrame = new UpdateFrame(Program.GIFKR2, currentVersion);
				uFrame.setLocationRelativeTo(f);
				uFrame.setVisible(true);
			}
		});
	}

//	public static void restartApplication() {
//		StringBuilder cmd = new StringBuilder();
//		cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
//		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
//			cmd.append(jvmArg + " ");
//		}
//		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
//		cmd.append(GIFKR.class.getName()).append(" ");
//		for (String arg : restartArgs) {
//			cmd.append(arg).append(" ");
//		}
//		try {
//			System.out.println(cmd);
//			Runtime.getRuntime().exec(cmd.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.exit(0);
//	}
}
