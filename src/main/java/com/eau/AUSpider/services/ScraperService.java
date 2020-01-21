package com.eau.AUSpider.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class ScraperService {
    private static Logger logger = LoggerFactory.getLogger(ScraperService.class);

    @Value("${anime.ultima.root}")
    private String animaUltimaRoot;

    @PostConstruct
    private void setup() {
        WebDriverManager.chromedriver().setup();
    }

    public String getDownloadLink(String url) {
        String animeUltimaId = getAnimeUltimaId(url);
        logger.info("animeUltimaId = {}", animeUltimaId);

        String videoUrl = animaUltimaRoot + animeUltimaId;
        logger.info("videoUrl = {}", videoUrl);

        String googleStorageUrl = getGoogleStorageUrl(videoUrl);
        return googleStorageUrl;
    }

    private String getGoogleStorageUrl(String videoUrl) {
        String html = getHtmlFromSelenium(videoUrl, "storage.google");
        if (html == null) {
            return null;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select("video");
        String googleStorageLink = elements.get(0).attr("src");
        return googleStorageLink;
    }

    private String getAnimeUltimaId(String url) {
        String auengineUrl = getAuengineUrl(url);
        if (auengineUrl == null) {
            return null;
        }
        return auengineUrl.substring(auengineUrl.lastIndexOf("/") + 1);
    }

    private String getAuengineUrl(String url) {
        String html = getHtmlFromSelenium(url, "option");
        if (html == null) {
            return null;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select("option");
        for (Element element : elements) {
            if (element.text().toLowerCase().equals("subbed: auengine")) {
                return element.attr("value");
            }
        }
        return null;
    }

    private String getHtmlFromSelenium(String url, String searchString) {
        if (url == null) {
            return null;
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        options.addArguments("javascript.enabled=True");

        WebDriver driver = new ChromeDriver(options);

        driver.get(url);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(5000);
                String pageSource = driver.getPageSource();
                if (pageSource.contains(searchString)) {
                    driver.quit();
                    return pageSource;
                }
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
        }
        driver.quit();
        return null;
    }

}
