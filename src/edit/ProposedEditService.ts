import { Injectable } from "chevronjs";
import { chevron } from "../chevron.js";
import { ProposedArtistEdit } from "../enrichment/artist/enricher/ArtistEnricher.js";
import { EditType } from "./ProposedEdit.js";

@Injectable(chevron)
class ProposedEditService {
    public stringifyProposedArtistEdit(
        proposedEdit: ProposedArtistEdit
    ): string {
        const prefix = `${proposedEdit.type}: '${proposedEdit.target.name}' ${proposedEdit.property}`;

        if (proposedEdit.type === EditType.CHANGE) {
            return `${prefix} ${JSON.stringify(
                proposedEdit.old
            )} -> ${JSON.stringify(proposedEdit.new)}`;
        }
        return `${prefix} ${JSON.stringify(proposedEdit.new)}`;
    }
}

export { ProposedEditService };
