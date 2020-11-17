package org.felixrilling.musicbrainzenricher.release;

import org.felixrilling.musicbrainzenricher.api.ScrapingService;
import org.felixrilling.musicbrainzenricher.genre.GenreMatcherService;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.musicbrainz.model.RelationWs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses web scraping because having to create an apple account just to fetch music data is overkill.
 */
// https://musicbrainz.org/release/5bcb2971-fdea-4543-baf2-dd41d8b9a3cd
// https://music.apple.com/us/album/1383304609
@Service
public class AppleMusicReleaseEnricher implements GenreReleaseEnricher {

    private static final Logger logger = LoggerFactory.getLogger(AppleMusicReleaseEnricher.class);

    private static final Pattern HOST_REGEX = Pattern.compile("(?:itunes|music)\\.apple\\.com");
    private static final Evaluator TAG_QUERY = QueryParser.parse(".product-meta");
    // Value format: "Genre · Year"
    private static final Pattern META_REGEX = Pattern.compile("(?<genre>.+).·.\\d+");

    private final GenreMatcherService genreMatcherService;
    private final ScrapingService scrapingService;

    AppleMusicReleaseEnricher(GenreMatcherService genreMatcherService, ScrapingService scrapingService) {
        this.genreMatcherService = genreMatcherService;
        this.scrapingService = scrapingService;
    }

    @Override
    public @NotNull Set<String> fetchGenres(@NotNull String relationUrl) {
        Optional<Document> document = scrapingService.load(relationUrl);
        if (document.isEmpty()) {
            return Set.of();
        }

        // We can only process genres if they are in english.
        if (!hasLocaleLanguage(document.get(), Locale.ENGLISH)) {
            logger.debug("Skipping '{}' because the locale is not supported.", relationUrl);
            return Set.of();
        }

        return genreMatcherService.match(extractTags(document.get()));
    }

    private @NotNull Set<String> extractTags(@NotNull Document document) {
        String metaText = document.select(TAG_QUERY).text();

        Matcher matcher = META_REGEX.matcher(metaText);
        if (!matcher.matches()) {
            logger.warn("Could not match meta text. This might be because we were redirected.");
            return Set.of();
        }
        return Set.of(matcher.group("genre"));
    }

    private boolean hasLocaleLanguage(@NotNull Document document, @NotNull Locale locale) {
        String parsedLocale = document.getElementsByTag("html").attr("lang");

        // We manually extract just the language in order to not have to deal with different locale representations
        // (es-mx in HTML vs es_MX in Java).
        String parsedLanguage;
        if (parsedLocale.contains("-")) {
            parsedLanguage = parsedLocale.substring(0, parsedLocale.indexOf("-"));
        } else {
            parsedLanguage = parsedLocale;
        }

        return parsedLanguage.equals(locale.getLanguage());
    }

    @Override
    public boolean relationFits(@NotNull RelationWs2 relationWs2) {
        if (!"http://musicbrainz.org/ns/rel-2.0#url".equals(relationWs2.getTargetType())) {
            return false;
        }
        URL url;
        try {
            url = new URL(relationWs2.getTargetId());
        } catch (MalformedURLException e) {
            logger.warn("Could not parse as URL: '{}'.", relationWs2.getTargetId());
            return false;
        }
        return HOST_REGEX.matcher(url.getHost()).matches();
    }
}
