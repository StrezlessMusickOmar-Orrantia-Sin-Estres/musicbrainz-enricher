package dev.rilling.musicbrainzenricher.api.musicbrainz;

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.regex.Pattern;

@Configuration
@ThreadSafe
class MusicbrainzApiConfiguration {

	private static final Pattern UNSUPPORTED_VERSION_CHARACTER_PATTERN = Pattern.compile("-");

	@Bean("musicbrainzWebService")
	@NotNull WebService createWebService(Environment environment) {
		String host = environment.getRequiredProperty("musicbrainz-enricher.host");
		String applicationName = environment.getRequiredProperty("musicbrainz-enricher.name");
		String applicationVersion = environment.getRequiredProperty("musicbrainz-enricher.version");
		String applicationContact = environment.getRequiredProperty("musicbrainz-enricher.contact");
		String username = environment.getRequiredProperty("musicbrainz-enricher.musicbrainz.username");
		String password = environment.getRequiredProperty("musicbrainz-enricher.musicbrainz.password");

		HttpClientWebServiceWs2 webService = new HttpClientWebServiceWs2(applicationName,
			applicationVersion,
			applicationContact);
		String client = getClient(applicationName, applicationVersion);
		webService.setClient(client);
		webService.setUsername(username);
		webService.setPassword(password);
		webService.setHost(host);
		return webService;
	}

	private @NotNull String getClient(@NotNull String applicationName, @NotNull String applicationVersion) {
		// See https://musicbrainz.org/doc/MusicBrainz_API
		String adaptedApplicationVersion = UNSUPPORTED_VERSION_CHARACTER_PATTERN.matcher(applicationVersion)
			.replaceAll("_");
		return "%s-%s".formatted(applicationName, adaptedApplicationVersion);
	}

}
