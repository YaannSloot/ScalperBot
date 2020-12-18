package com.iansloat.scalperbot;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iansloat.scalperbot.entities.jobs.BestbuyJob;
import com.iansloat.scalperbot.entities.jobs.Job;
import com.iansloat.scalperbot.entities.products.Product;

import net.miginfocom.swing.MigLayout;

public class ScalperBot {

	private static final Logger logger = LoggerFactory.getLogger(ScalperBot.class);

	public static List<FirefoxDriver> driverCache = new ArrayList<>();

	private static JFrame frmScalperBot;
	private static JTextField textField;

	private static Job bestbuy;
	private static Clip clip;
	private static AudioInputStream audioInputStream;
	private static String webHookUrl = "";
	private static String webHookMsg = "";
	private static BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

	public static FirefoxDriver getNewBrowserInstance() {
		FirefoxProfile profile = new ProfilesIni().getProfile("selenium");
		if (profile == null) {
			try {
				logger.info("Creating firefox selenium profile...");
				Runtime.getRuntime().exec(new FirefoxBinary().toJson() + " -CreateProfile selenium");
				Thread.sleep(2000);
				profile = new ProfilesIni().getProfile("selenium");
				if (profile == null) {
					logger.error("Failed to create selenium profile");
					Thread.sleep(3000);
					System.exit(-1);
				}
			} catch (IOException | InterruptedException e) {
				return null;
			}
		}
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		System.setProperty("webdriver.gecko.driver", ".\\geckodriver.exe");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		if (profile != null)
			firefoxOptions.setProfile(profile);
		LoggingPreferences pref = new LoggingPreferences();
		pref.enable(LogType.BROWSER, Level.OFF);
		pref.enable(LogType.CLIENT, Level.OFF);
		pref.enable(LogType.DRIVER, Level.OFF);
		pref.enable(LogType.PERFORMANCE, Level.OFF);
		pref.enable(LogType.PROFILER, Level.OFF);
		pref.enable(LogType.SERVER, Level.OFF);
		firefoxOptions.setCapability(CapabilityType.LOGGING_PREFS, pref);
		FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		driverCache.add(driver);
		return driver;
	}

	private static void displayTray() throws AWTException {
		// Obtain only one instance of the SystemTray object
		SystemTray tray = SystemTray.getSystemTray();

		// If the icon is a file
		Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
		// Alternative (if the icon is on the classpath):
		// Image image =
		// Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

		TrayIcon trayIcon = new TrayIcon(image, "Stock checker");
		// Let the system resize the image if needed
		trayIcon.setImageAutoSize(true);
		// Set tooltip text for the tray icon
		trayIcon.setToolTip("Stock alert");
		tray.add(trayIcon);
		trayIcon.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						frmScalperBot.toFront();
					}

				});
			}

		});
		trayIcon.displayMessage("STOCK FOUND!!!!!", "WEE WOO WEE WOO", MessageType.INFO);
	}

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				driverCache.forEach(d -> d.quit());
			}
		});

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		frmScalperBot = new JFrame();
		frmScalperBot.setTitle("Scalper bot");
		frmScalperBot.setBounds(100, 100, 894, 599);
		frmScalperBot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmScalperBot.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(null);
		frmScalperBot.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new MigLayout("", "[grow][]", "[]"));

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !textField.getText().equals("")) {
					inputQueue.add(textField.getText());
					textField.setText("");
				}
			}
		});
		panel.add(textField, "cell 0 0,growx");
		textField.setColumns(10);

		JButton Enter = new JButton("Enter");
		Enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textField.getText().equals("")) {
					inputQueue.add(textField.getText());
					textField.setText("");
				}
			}
		});
		panel.add(Enter, "cell 1 0");

		JPanel panel_1 = new JPanel();
		frmScalperBot.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "cell 0 0,grow");

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		Enter.setEnabled(false);
		textField.setEditable(false);

		MessageConsole mc = new MessageConsole(textArea);

		mc.redirectOut();
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);

		frmScalperBot.setVisible(true);

		System.out.println("Please wait. Bot is starting up...");
		File webHook = new File("webhook.txt");
		if (webHook.exists()) {
			try {
				LineIterator iterator = FileUtils.lineIterator(webHook, "UTF-8");
				webHookUrl = iterator.nextLine();
				try {
					new URL(webHookUrl);
					webHookMsg = iterator.nextLine();
				} catch (MalformedURLException e) {
					logger.error("Webhook url is invalid. Ignoring...");
					webHookUrl = "";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!webHookUrl.equals("")) {
			logger.info("Webhook url set to " + webHookUrl);
			logger.info("Webhook message set to " + webHookMsg);
		}
		bestbuy = new BestbuyJob();
		boolean doLoop = true;
		List<Product> products;
		while (doLoop) {
			System.out.print("What would you like to search for: ");
			Enter.setEnabled(true);
			textField.setEditable(true);
			String search = "";
			try {
				search = inputQueue.take();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
				System.exit(-1);
			}
			System.out.println(search);
			Enter.setEnabled(false);
			textField.setEditable(false);
			products = bestbuy.getStore().searchForProducts(search);
			if (products != null) {
				if (products.size() > 0) {
					boolean stop = false;
					while (!stop) {
						System.out.println("Please type the number of your selection:");
						for (int i = 0; i < products.size(); i++) {
							System.out.println((i + 1) + ". " + products.get(i).getProductName());
						}
						System.out.println((products.size() + 1) + ". Print current queued products");
						System.out.println((products.size() + 2) + ". Reprint search results");
						System.out.println((products.size() + 3) + ". Start job");
						System.out.println((products.size() + 4) + ". Queue all and start job");
						boolean subLoop = true;
						while (subLoop) {
							System.out.print('>');
							Enter.setEnabled(true);
							textField.setEditable(true);
							String choiceStr = "";
							try {
								choiceStr = inputQueue.take();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
								System.exit(-1);
							}
							System.out.println(choiceStr);
							Enter.setEnabled(false);
							textField.setEditable(false);
							try {
								int choice = Integer.parseInt(choiceStr);
								if (choice > 0 && choice <= products.size() + 4) {
									if (choice == products.size() + 1) {
										for (int i = 0; i < bestbuy.getPendingProducts().size(); i++) {
											System.out.println(bestbuy.getPendingProducts().get(i).getProductName());
										}
									} else if (choice == products.size() + 2) {
										for (int i = 0; i < products.size(); i++) {
											System.out.println((i + 1) + ". " + products.get(i).getProductName());
										}
									} else if (choice == products.size() + 3) {
										if (bestbuy.getPendingProducts().size() > 0) {
											subLoop = false;
											stop = true;
											doLoop = false;
										} else {
											System.out
													.println("You do not have enough products queued to start the job");
											subLoop = false;
										}
									} else if (choice == products.size() + 4) {
										for (int i = 0; i < products.size(); i++) {
											bestbuy.addProduct(products.get(i));
										}
										subLoop = false;
										stop = true;
										doLoop = false;
									} else {
										bestbuy.addProduct(products.get(choice - 1));
										products.remove(choice - 1);
										subLoop = false;
									}
								}
							} catch (Exception e) {
								System.out.println("Please input a NUMBER");
							}
						}
					}
				} else
					System.out.println("No products found");
			} else
				System.out.println("No products found");
		}
		bestbuy.startJob();
		bestbuy.waitForComplete();
		Queue<Product> results = bestbuy.getCheckoutQueue();
		String launchArgs = "";
		while (!results.isEmpty()) {
			launchArgs += results.poll().getSourceURL() + ' ';
		}
		try {
			Runtime.getRuntime().exec(new FirefoxBinary().toJson() + ' ' + launchArgs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File alarm = new File("alarm.wav");
		if (alarm.exists()) {
			try {
				audioInputStream = AudioSystem.getAudioInputStream(alarm);
				clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(1.0f);
				new Thread(() -> {
					clip.start();
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				}).start();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("alarm.wav not found so no sound was played");
		}
		if (!webHookUrl.equals("")) {
			new Thread(() -> {
				HttpClient client = HttpClients.createDefault();
				HttpPost postRequest = new HttpPost(webHookUrl);
				postRequest.addHeader("Content-Type", "application/json");
				JSONObject msgBody = new JSONObject();
				msgBody.put("content", webHookMsg);
				try {
					postRequest.setEntity(new StringEntity(msgBody.toString()));
					HttpResponse response = client.execute(postRequest);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						System.out.println(entity.getContent().readAllBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		} else {
			logger.info("No webhook set so post request was skipped");
		}
		frmScalperBot.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (audioInputStream != null)
					try {
						audioInputStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				if (clip != null)
					clip.stop();
			}
		});
		try {
			displayTray();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		bestbuy.shutdownJob();
		System.out.println("STOCK HAS BEEN FOUND!!!!!");
		System.out.println("You can close this window to end your suffering...");
	}

}
