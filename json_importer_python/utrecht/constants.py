from pathlib import Path

gemeente = 'utrecht'

project_path = Path().resolve()
source_folder = Path(project_path, "source_data/")
raw_folder = Path(project_path, 'raw_data/')
output_path = Path(project_path, 'output/')
geo_template_path = Path(project_path, "geojson_template.geojson")
mapping_values_path = Path(project_path, "mapping_values_to_pick.csv")

project_fasen = [
    # projectfasen
    '0. Concept',
    '1. Initiatief',
    '2. Definitie',
    '3. Ontwerp',
    '4. Voorbereiding',
    '5. Realisatie',
    '6. Nazorg',
]

required_input_columns = [
    "type",
    "geometry.type",
    "geometry.coordinates",

    "properties.globalid",
    "properties.parent_globalid",

    "properties.gemeente",
    "properties.plannaam",

    "properties.jaartal",
    "properties.jaar_start_project",
    "properties.created",
    "properties.oplevering_eerste",
    "properties.oplevering_laatste",

    "properties.meergezins_koop1",
    "properties.meergezins_koop2",
    "properties.meergezins_koop3",
    "properties.meergezins_koop4",
    "properties.meergezins_koop_onb",
    "properties.meergezins_huur1",
    "properties.meergezins_huur2",
    "properties.meergezins_huur3",
    "properties.meergezins_huur4",
    "properties.meergezins_huur_onb",
    "properties.meergezins_onbekend",
    "properties.eengezins_koop1",
    "properties.eengezins_koop2",
    "properties.eengezins_koop3",
    "properties.eengezins_koop4",
    "properties.eengezins_koop_onb",
    "properties.eengezins_huur1",
    "properties.eengezins_huur2",
    "properties.eengezins_huur3",
    "properties.eengezins_huur4",
    "properties.eengezins_huur_onb",
    "properties.eengezins_onbekend",
    "properties.onbekend_koop1",
    "properties.onbekend_koop2",
    "properties.onbekend_koop3",
    "properties.onbekend_koop4",
    "properties.onbekend_koop_onb",
    "properties.onbekend_huur1",
    "properties.onbekend_huur2",
    "properties.onbekend_huur3",
    "properties.onbekend_huur4",
    "properties.onbekend_huur_onb",
    "properties.onbekend_onbekend",
    "properties.sloop_meergezins_koop1",
    "properties.sloop_meergezins_koop2",
    "properties.sloop_meergezins_koop3",
    "properties.sloop_meergezins_koop4",
    "properties.sloop_meergezins_koop_onb",
    "properties.sloop_meergezins_huur1",
    "properties.sloop_meergezins_huur2",
    "properties.sloop_meergezins_huur3",
    "properties.sloop_meergezins_huur4",
    "properties.sloop_meergezins_huur_onb",
    "properties.sloop_meergezins_onbekend",
    "properties.sloop_eengezins_koop1",
    "properties.sloop_eengezins_koop2",
    "properties.sloop_eengezins_koop3",
    "properties.sloop_eengezins_koop4",
    "properties.sloop_eengezins_koop_onb",
    "properties.sloop_eengezins_huur1",
    "properties.sloop_eengezins_huur2",
    "properties.sloop_eengezins_huur3",
    "properties.sloop_eengezins_huur4",
    "properties.sloop_eengezins_huur_onb",
    "properties.sloop_eengezins_onbekend",
    "properties.sloop_onbekend_koop1",
    "properties.sloop_onbekend_koop2",
    "properties.sloop_onbekend_koop3",
    "properties.sloop_onbekend_koop4",
    "properties.sloop_onbekend_koop_onb",
    "properties.sloop_onbekend_huur1",
    "properties.sloop_onbekend_huur2",
    "properties.sloop_onbekend_huur3",
    "properties.sloop_onbekend_huur4",
    "properties.sloop_onbekend_huur_onb",
    "properties.sloop_onbekend_onbekend",

    "properties.sloop_gerealiseerd",
    "properties.bouw_gerealiseerd",

    "properties.plantype",
    "properties.opdrachtgever_type",
    "properties.opdrachtgever_naam",
    "properties.projectfase",
    "properties.status_planologisch",


]

required_output_columns = [  # basic
    'properties.parent_globalid', 'properties.globalid', 'type',

    # geometrie
    'geometry.type', 'geometry.coordinates',

    # basisgegegevens
    'properties.projectgegevens.basisgegevens.identificatie_nr',
    'properties.projectgegevens.basisgegevens.naam',

    # projectgegevens
    'properties.projectgegevens.projectgegevens.plan_soort',
    'properties.projectgegevens.projectgegevens.in_programmering',
    'properties.projectgegevens.projectgegevens.prioritering',
    'properties.projectgegevens.projectgegevens.rol_gemeente',

    # rollen
    'properties.projectgegevens.rollen.projectleider',
    'properties.projectgegevens.rollen.opdrachtgever',

    # projectduur
    'properties.projectgegevens.projectduur.start_project',
    'properties.projectgegevens.projectduur.eind_project',

    # projectfasen
    'properties.projectgegevens.projectfasen.0. Concept',
    'properties.projectgegevens.projectfasen.1. Initiatief',
    'properties.projectgegevens.projectfasen.2. Definitie',
    'properties.projectgegevens.projectfasen.3. Ontwerp',
    'properties.projectgegevens.projectfasen.4. Voorbereiding',
    'properties.projectgegevens.projectfasen.5. Realisatie',
    'properties.projectgegevens.projectfasen.6. Nazorg',

    # planologisch
    'properties.projectgegevens.planologische_planstatus.1A. Onherroepelijk',
    'properties.projectgegevens.planologische_planstatus.1B. Onherroepelijk, uitwerkingsplicht',
    'properties.projectgegevens.planologische_planstatus.2A. Vastgesteld',
    'properties.projectgegevens.planologische_planstatus.2C. Vastgesteld, wijzigingsbevoegdheid',
    'properties.projectgegevens.planologische_planstatus.3. In voorbereiding',
    'properties.projectgegevens.planologische_planstatus.4A. Visie',
    'properties.projectgegevens.planologische_planstatus.4B. Idee',
    "properties.projectgegevens.planologische_planstatus.Onbekend",

    # mutatie
    'properties.woning_blokken.mutatiegegevens.mutatie_type',
    'properties.woning_blokken.mutatiegegevens.eigendom_type',
    'properties.woning_blokken.mutatiegegevens.woning_type',
    'properties.woning_blokken.mutatiegegevens.contract_type',
    'properties.woning_blokken.mutatiegegevens.einddatum',
    'properties.woning_blokken.mutatiegegevens.aantal',

    # waarde
    'properties.woning_blokken.waarde.hoog',
    'properties.woning_blokken.locatie.gemeente',

    # locatie
    'properties.woning_blokken.locatie.gemeente',
    'properties.woning_blokken.locatie.wijk',
    'properties.woning_blokken.locatie.buurt'
]

