"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MusicbrainzDatabaseService_1 = require("./api/musicbrainz/MusicbrainzDatabaseService");
const chevron_1 = require("./chevron");
const ArtistEnrichmentService_1 = require("./enrichment/ArtistEnrichmentService");
const logger_1 = require("./logger");
const logger = logger_1.rootLogger.child({ target: "main" });
chevron_1.chevron.registerInjectable({
    // MusicBrainz bot account username & password (optional)
    botAccount: {
        username: "unmasked_bot",
        password: process.env.MUSICBRAINZ_PASSWORD
    },
    // API base URL, default: 'https://musicbrainz.org' (optional)
    baseUrl: "https://musicbrainz.org/",
    appName: "data-enrichtment-bot",
    appVersion: "0.1.0",
    // Your e-mail address, required for submitting ISRCs
    appContactInfo: "contact@rilling.dev"
}, { name: "musicbrainzConfig" });
const musicbrainzDatabaseService = chevron_1.chevron.getInjectableInstance(MusicbrainzDatabaseService_1.MusicbrainzDatabaseService);
const artistEnrichmentService = chevron_1.chevron.getInjectableInstance(ArtistEnrichmentService_1.ArtistEnrichmentService);
// ArtistEnrichmentService
//     .enrich("488adff4-0b5c-41c9-aa8c-570aeae15737")
//     .catch(logger.error);
musicbrainzDatabaseService
    .search({
    type: "person"
})
    .then(result => {
    for (const artist of result.artists) {
        artistEnrichmentService.enrich(artist.id).catch(logger.error);
    }
})
    .catch(logger.error);
