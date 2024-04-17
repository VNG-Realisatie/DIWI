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

required_columns = [# basic
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

                    # maatwerk
                    'properties.projectgegevens.maatwerk_eigenschappen.opdrachtgever_type',
                    'properties.projectgegevens.maatwerk_eigenschappen.percentage_opp_buitenstedelijk',
                    'properties.projectgegevens.maatwerk_eigenschappen.provincie',
                    'properties.projectgegevens.maatwerk_eigenschappen.regio',
                    'properties.projectgegevens.maatwerk_eigenschappen.bestemmingsplan',
                    'properties.projectgegevens.maatwerk_eigenschappen.toelichting_knelpunten',
                    'properties.projectgegevens.maatwerk_eigenschappen.beoogd_woonmilieu_ABF5',
                    'properties.projectgegevens.maatwerk_eigenschappen.status_planologisch_groep',
                    'properties.projectgegevens.maatwerk_eigenschappen.opmerkingen_kwalitatief',
                    'properties.projectgegevens.maatwerk_eigenschappen.woondeal',
                    'properties.projectgegevens.maatwerk_eigenschappen.realiseerbaarheid',
                    'properties.projectgegevens.maatwerk_eigenschappen.projectfase',
                    'properties.projectgegevens.maatwerk_eigenschappen.projectleider',
                    'properties.projectgegevens.maatwerk_eigenschappen.opdrachtgever',
                    'properties.projectgegevens.maatwerk_eigenschappen.vertrouwelijkheid',

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
                    'properties.woning_blokken.locatie.buurt',

                    # maatwerk
                    'properties.woning_blokken.maatwerk_woningeigenschappen.category_type',
                    'properties.jaartal', 'properties.woning_blokken.waarde.laag',
                    'properties.bouw_gerealiseerd', 'properties.flexwoningen',
                    'properties.tijdelijke_woningen', 'properties.sloop_gerealiseerd'
]
