package nl.vng.diwi.services.export.gelderland;

import java.util.HashMap;
import java.util.Map;

import nl.vng.diwi.services.export.OwnershipCategory;

public class GelderlandConstants {

    public static enum DetailPlanningHeaders {
        GlobalID,
        Creator,
        Created,
        Editor,
        Edited,
        parent_globalid,
        gemeente,
        regio,
        vertrouwelijkheid,
        jaartal,

        meergezins_koop1,
        meergezins_koop2,
        meergezins_koop3,
        meergezins_koop4,
        meergezins_koop_onb,
        meergezins_huur1,
        meergezins_huur2,
        meergezins_huur3,
        meergezins_huur4,
        meergezins_huur_onb,
        meergezins_onbekend,

        eengezins_koop1,
        eengezins_koop2,
        eengezins_koop3,
        eengezins_koop4,
        eengezins_koop_onb,
        eengezins_huur1,
        eengezins_huur2,
        eengezins_huur3,
        eengezins_huur4,
        eengezins_huur_onb,
        eengezins_onbekend,

        onbekend_koop1,
        onbekend_koop2,
        onbekend_koop3,
        onbekend_koop4,
        onbekend_koop_onb,
        onbekend_huur1,
        onbekend_huur2,
        onbekend_huur3,
        onbekend_huur4,
        onbekend_huur_onb,
        onbekend_onbekend,

        bouw_gerealiseerd,

        sloop_meergezins_koop1,
        sloop_meergezins_koop2,
        sloop_meergezins_koop3,
        sloop_meergezins_koop4,
        sloop_meergezins_koop_onb,
        sloop_meergezins_huur1,
        sloop_meergezins_huur2,
        sloop_meergezins_huur3,
        sloop_meergezins_huur4,
        sloop_meergezins_huur_onb,
        sloop_meergezins_onbekend,

        sloop_eengezins_koop1,
        sloop_eengezins_koop2,
        sloop_eengezins_koop3,
        sloop_eengezins_koop4,
        sloop_eengezins_koop_onb,
        sloop_eengezins_huur1,
        sloop_eengezins_huur2,
        sloop_eengezins_huur3,
        sloop_eengezins_huur4,
        sloop_eengezins_huur_onb,
        sloop_eengezins_onbekend,

        sloop_onbekend_koop1,
        sloop_onbekend_koop2,
        sloop_onbekend_koop3,
        sloop_onbekend_koop4,
        sloop_onbekend_koop_onb,
        sloop_onbekend_huur1,
        sloop_onbekend_huur2,
        sloop_onbekend_huur3,
        sloop_onbekend_huur4,
        sloop_onbekend_huur_onb,
        sloop_onbekend_onbekend,
        sloop_gerealiseerd
    }
}
