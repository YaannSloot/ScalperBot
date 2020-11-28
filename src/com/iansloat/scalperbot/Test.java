package com.iansloat.scalperbot;

import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;

import com.iansloat.scalperbot.entities.jobs.BestbuyJob;
import com.iansloat.scalperbot.entities.jobs.Job;
import com.iansloat.scalperbot.entities.products.Product;

public class Test {

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (FirefoxDriver d : ScalperBot.driverCache) {
					d.close();
				}
			}
		});

		// Product radeonCard = new
		// BestbuyProduct("https://www.bestbuy.com/site/xfx-amd-radeon-rx-6800xt-16gb-gddr6-pci-express-4-0-gaming-graphics-card-black/6441226.p?skuId=6441226");

		// System.out.println(radeonCard.getProductImage());

		/*
		 * try { FileUtils.copyURLToFile(new URL(radeonCard.getProductImage()), new
		 * File("image.jpg")); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */

		Job bestbuyJob = new BestbuyJob();

		List<Product> searchResults = bestbuyJob.getStore().searchForProducts("sony playstation 5 console");
		
		if (searchResults.size() > 0) {
			searchResults.forEach(j -> bestbuyJob.addProduct(j));
			bestbuyJob.startJob();
		} else
			System.out.println("no products");

		/*
		 * List<Future<?>> tasks = new ArrayList<>();
		 * 
		 * for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
		 * tasks.add(ForkJoinPool.commonPool().submit(() -> { while (true) {
		 * System.out.println(radeonCard.isAvailable() ? "Stock available" :
		 * "Stock not available"); } })); }
		 * 
		 * for(Future<?> task : tasks) { try { task.get(); } catch (InterruptedException
		 * | ExecutionException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */

		/*
		 * FirefoxBinary firefoxBinary = new FirefoxBinary(); FirefoxProfile profile =
		 * new ProfilesIni().getProfile("selenium"); if(profile == null) { try {
		 * System.out.println("Please create a new firefox profile named \"selenium\"");
		 * Process profileCreator = Runtime.getRuntime().
		 * exec("C:\\Program Files\\Mozilla Firefox\\firefox.exe -p");
		 * System.out.println(profileCreator.getInputStream().read()); profile = new
		 * ProfilesIni().getProfile("selenium"); } catch (IOException e) {
		 * e.printStackTrace(); } } try { Process profileCreator = Runtime.getRuntime().
		 * exec("C:\\Program Files\\Mozilla Firefox\\firefox.exe -P selenium");
		 * System.out.println(profileCreator.getInputStream().read());
		 * Thread.sleep(1000); } catch (IOException | InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * //firefoxBinary.addCommandLineOptions("--headless");
		 * System.setProperty("webdriver.gecko.driver", ".\\geckodriver.exe");
		 * System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
		 * FirefoxOptions firefoxOptions = new FirefoxOptions();
		 * firefoxOptions.setBinary(firefoxBinary); if (profile != null)
		 * firefoxOptions.setProfile(profile); LoggingPreferences pref = new
		 * LoggingPreferences(); pref.enable(LogType.BROWSER, Level.OFF);
		 * pref.enable(LogType.CLIENT, Level.OFF); pref.enable(LogType.DRIVER,
		 * Level.OFF); pref.enable(LogType.PERFORMANCE, Level.OFF);
		 * pref.enable(LogType.PROFILER, Level.OFF); pref.enable(LogType.SERVER,
		 * Level.OFF); firefoxOptions.setCapability(CapabilityType.LOGGING_PREFS, pref);
		 * FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		 */

	}

}
