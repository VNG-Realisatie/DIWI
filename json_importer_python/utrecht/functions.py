import copy

import pandas as pd
import numpy as np
import geojson
import itertools
from numpyencoder import NumpyEncoder
import datetime


def read_supportive_files(mapping_values):
    koop_mapping = pd.read_csv(f'koop.csv').replace({np.nan: None})
    huur_mapping = pd.read_csv(f'huur.csv').replace({np.nan: None})
    planologisch_mapping = pd.read_csv('mapping_planologisch.csv')
    planologisch_dict = dict(zip(planologisch_mapping['gemeente'], planologisch_mapping['diwi']))
    mapping_values = pd.read_csv(mapping_values)
    return koop_mapping, huur_mapping, planologisch_dict, mapping_values


def read_geo_file(geo_path):
    with open(geo_path) as f:
        geo_file = geojson.load(f)
    return geo_file


def find_all_gemeentes_files_paths(path_source):
    all_gemeente_geojson_paths = path_source.glob('*.geojson')
    return all_gemeente_geojson_paths


def create_df_outs(df):
    df_out = df[['properties.parent_globalid', 'properties.globalid']]
    df_out.columns = [x.lower() for x in df_out.columns]

    return df_out


def fill_type_geometry(df_out, df_in):
    df_out['type'] = df_in['type']
    df_out['geometry.type'] = df_in['geometry.type'].values
    df_out['geometry.coordinates'] = df_in['geometry.coordinates'].values
    return df_out


def add_basisgegevens(df_out, df_in, prefix):
    local_prefix = f'{prefix}.basisgegevens'

    unique_projects = pd.DataFrame(df_out[['properties.parent_globalid', 'properties.globalid']].groupby(by=['properties.parent_globalid'], as_index=False).first()['properties.parent_globalid'])
    unique_projects[f'{local_prefix}.identificatie_nr'] = range(len(unique_projects))

    df_out = pd.merge(left=df_out, right=unique_projects, on=['properties.parent_globalid'], how='left')
    df_out[f'{local_prefix}.naam'] = df_in['properties.plannaam']

    return df_out


def add_projectgegevens(df_out, df_in, prefix):
    local_prefix = f'{prefix}.projectgegevens'

    df_out[f'{local_prefix}.plan_soort'] = df_in['properties.plantype']
    df_out[f'{local_prefix}.in_programmering'] = None
    df_out[f'{local_prefix}.prioritering'] = None
    df_out[f'{local_prefix}.rol_gemeente'] = None
    df_out.loc[df_in['properties.opdrachtgever_type'] == 'Gemeente', f'{local_prefix}.rol_gemeente'] = 'Opdrachtgever'

    return df_out


def add_rollen(df_out, df_in, prefix):
    local_prefix = f'{prefix}.rollen'

    df_out[f'{local_prefix}.projectleider'] = None
    df_out[f'{local_prefix}.opdrachtgever'] = df_in['properties.opdrachtgever_naam']

    return df_out


def add_projectduur(df_out, df_in, prefix):
    local_prefix = f'{prefix}.projectduur'

    df_out[f'{local_prefix}.start_project'] = None
    df_out.loc[df_in['properties.jaar_start_project'].notna(), f'{local_prefix}.start_project'] = [f"{int(x)}-01-01" for x in df_in.loc[df_in['properties.jaar_start_project'].notna(), 'properties.jaar_start_project']]
    df_out.loc[df_in['properties.jaar_start_project'].isna(), f'{local_prefix}.start_project'] = [None for x in df_in.loc[df_in['properties.jaar_start_project'].isna(), 'properties.jaartal']]  # TODO: is this acceptable?

    df_out[f'{local_prefix}.eind_project'] = None
    df_out[f'{local_prefix}.eind_project'] = [f"{int(x)}-12-01" if type(x) != type(None) else None for x in df_in['properties.oplevering_laatste'].fillna(np.nan).replace({np.nan: None})]


    return df_out


def add_projectfasen(df_out, df_in, prefix):
    local_prefix = f'{prefix}.projectfasen'

    df_out[f'{local_prefix}.1_initiatieffase'] = None
    df_out.loc[df_in['properties.jaar_start_project'].notna(), f'{local_prefix}.1_initiatieffase'] = [f"{int(x)}-01-01" for x in df_in.loc[df_in['properties.jaar_start_project'].notna(), 'properties.jaar_start_project']]
    df_out.loc[df_in['properties.jaar_start_project'].isna(), f'{local_prefix}.1_initiatieffase'] = [None for x in df_in.loc[df_in['properties.jaar_start_project'].isna(), 'properties.jaartal']]  # TODO: is this acceptable?

    df_out[f'{local_prefix}.2_projectfase'] = None
    df_out[f'{local_prefix}.3_vergunningsfase'] = None
    df_out[f'{local_prefix}.4_realisatiefase'] = None

    df_out[f'{local_prefix}.5_opleverfase'] = None
    df_out.loc[df_in['properties.oplevering_eerste'].notna(), f'{local_prefix}.5_opleverfase'] = [f"{int(x)}-06-28" for x in df_in.loc[df_in['properties.oplevering_eerste'].notna(), 'properties.oplevering_eerste']]

    return df_out


def add_planologische_planstatus(df_out, df_in, prefix):
    local_prefix = f'{prefix}.planologische_planstatus'

    df_in['planologisch_datum'] = [f"{x.year}-{x.month}-{x.day}" for x in pd.to_datetime(df_in['properties.created'])]
    temp = df_in[['properties.globalid', 'properties.status_planologisch', 'planologisch_datum']].pivot(columns=['properties.status_planologisch'],
                                                                                                        index='properties.globalid').replace(np.nan, None)
    temp.columns = [f"{local_prefix}.{x[1]}" for x in temp.columns]
    temp = temp.reset_index()
    df_out = pd.merge(left=df_out, right=temp, on=['properties.globalid'], how='left')

    return df_out


def add_maatwerk_eigenschappen(df_out, df_in, prefix):
    local_prefix = f'{prefix}.maatwerk_eigenschappen'

    df_out[f'{local_prefix}.opdrachtgever_type'] = df_in['properties.opdrachtgever_type']

    df_out[f'{local_prefix}.percentage_opp_buitenstedelijk'] = df_in['properties.percentage_opp_buitenstedelijk'].fillna(0)

    df_out[f'{local_prefix}.provincie'] = df_in['properties.provincie']

    df_out[f'{local_prefix}.regio'] = df_in['properties.regio']
    df_out[f'{local_prefix}.bestemmingsplan'] = df_in['properties.bestemmingsplan']

    df_out[f'{local_prefix}.toelichting_knelpunten'] = df_in['properties.toelichting_knelpunten']
    df_out[f'{local_prefix}.beoogd_woonmilieu_ABF5'] = df_in['properties.beoogd_woonmilieu_abf5']

    df_out[f'{local_prefix}.status_planologisch_groep'] = df_in['properties.status_planologisch_groep']
    df_out[f'{local_prefix}.opmerkingen_kwalitatief'] = df_in['properties.opmerkingen_kwalitatief']

    df_out[f'{local_prefix}.woondeal'] = df_in['properties.woondeal']
    df_out[f'{local_prefix}.realiseerbaarheid'] = df_in['properties.realiseerbaarheid']

    df_out[f'{local_prefix}.projectfase'] = df_in['properties.projectfase']
    df_out[f'{local_prefix}.projectleider'] = None

    df_out[f'{local_prefix}.opdrachtgever'] = df_in['properties.opdrachtgever_naam']
    df_out[f'{local_prefix}.vertrouwelijkheid'] = df_in['properties.vertrouwelijkheid']


    return df_out


def fill_projectgegevens(df_out, df_in):
    prefix = 'properties.projectgegevens'

    df_out = add_basisgegevens(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectgegevens(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_rollen(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectduur(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectfasen(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_planologische_planstatus(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_maatwerk_eigenschappen(df_out=df_out, df_in=df_in, prefix=prefix)

    return df_out


def explode_huizenblokken_add_aantallen(df_out, df_in, mapping_values):
    """
    bouw x sloop
    eensgezin x meersgezins x onbekend
    koop_1,2,3,4,onbekend x huur_1,2,3,4, onbekend
    """

    # TODO: use the lowest-level fields for the conditions. e.g. "sloop_meergezins_onbekend"

    prefix = 'properties.woning_blokken'

    dimension_mutation = ['bouw', 'sloop']
    dimension_owner = ['koopwoning', 'particuliere_verhuurder', 'huur_woningcorporatie', 'onbekend']
    dimension_woning = ['eengezins_woning', 'meergezins_woning', 'onbekend']
    dimension_contract_type = ['huur', 'koop', 'onbekend']
    dimension_category = ['1', '2', '3', '4', 'onbekend']

    combinations = list(itertools.product(dimension_mutation, dimension_owner, dimension_woning, dimension_contract_type, dimension_category))
    combinations.append(('onbekend', 'onbekend', 'onbekend', 'onbekend', 'onbekend'))

    df_huizenblokken = df_out[['properties.globalid']]

    df_huizenblokken['combinations'] = None
    df_huizenblokken['combinations'] = df_huizenblokken.index.map(lambda x: combinations)
    df_huizenblokken = df_huizenblokken.explode(column='combinations')
    df_huizenblokken['mutation'] = [x[0] for x in df_huizenblokken['combinations']]
    df_huizenblokken['owner'] = [x[1] for x in df_huizenblokken['combinations']]
    df_huizenblokken['woning'] = [x[2] for x in df_huizenblokken['combinations']]
    df_huizenblokken['contract_type'] = [x[3] for x in df_huizenblokken['combinations']]
    df_huizenblokken['category'] = [x[4] for x in df_huizenblokken['combinations']]

    df_huizenblokken = pd.merge(left=df_huizenblokken, right=mapping_values, on=['mutation', 'owner', 'woning', 'contract_type', 'category'], how='inner')

    temp = df_in[['properties.globalid', 'properties.verhuurder_type'] + [f'properties.{x}' for x in mapping_values['column'].unique()]]
    temp = temp.set_index(['properties.globalid', 'properties.verhuurder_type']).stack().reset_index()
    temp = temp.rename({'level_2': 'column', 0: 'aantal', 'properties.verhuurder_type': 'owner'}, axis=1)
    temp['column'] = [x.split('properties.')[-1] for x in temp['column']]

    temp.loc[temp['column'].isin(mapping_values[mapping_values['owner'] == 'koopwoning']['column'].values), 'owner'] = 'koopwoning'
    temp.loc[temp['owner'] == 'Woningbouwcorporatie', 'owner'] = 'huur_woningcorporatie'
    temp.loc[(temp['owner'] != 'koopwoning') & (temp['owner'] != 'huur_woningcorporatie'), 'owner'] = 'onbekend'

    df_huizenblokken = pd.merge(left=df_huizenblokken, right=temp, on=['properties.globalid', 'owner', 'column'], how='left')
    df_huizenblokken = df_huizenblokken[(df_huizenblokken['aantal'] != 0) & (df_huizenblokken['aantal'].notna())]

    temp_jaartal = df_in[['properties.globalid', 'properties.jaartal']]
    temp_jaartal['properties.jaartal'] = [f'{int(x)}-12-31' if type(x) != type(None) else None for x in temp_jaartal['properties.jaartal'].fillna(np.nan).replace({np.nan: None})]

    df_huizenblokken = pd.merge(left=df_huizenblokken, right=temp_jaartal, on =['properties.globalid'], how='left')

    df_out = pd.merge(left=df_out, right=df_huizenblokken, on='properties.globalid')

    df_out['aantal'] = df_out['aantal'].abs()

    df_out = df_out.rename({'mutation': f'{prefix}.mutatiegegevens.mutatie_type',
                            'owner': f'{prefix}.mutatiegegevens.eigendom_type',
                            'woning': f'{prefix}.mutatiegegevens.woning_type',
                            'contract_type': f'{prefix}.mutatiegegevens.contract_type',
                            'category': f'{prefix}.maatwerk_woningeigenschappen.category_type',
                            'aantal': f'{prefix}.mutatiegegevens.aantal',
                            'properties.jaartal': f'{prefix}.mutatiegegevens.einddatum'}, axis=1)

    df_out = df_out.drop(['combinations', 'column'], axis=1)


    return df_out


def add_woningwaardes(df_out, df_in, koop_mapping, huur_mapping):

    df_out = pd.merge(left=df_out, right=df_in[['properties.globalid', 'properties.jaartal']], on=['properties.globalid'], how='left')

    koop_mapping['categorie'] = koop_mapping['categorie'].astype(str)
    huur_mapping['categorie'] = huur_mapping['categorie'].astype(str)

    df_koop = df_out[(df_out['properties.woning_blokken.mutatiegegevens.contract_type'] == 'koop') & (df_out['properties.woning_blokken.maatwerk_woningeigenschappen.category_type'] != 'onbekend')]
    df_huur = df_out[(df_out['properties.woning_blokken.mutatiegegevens.contract_type'] == 'huur') & (df_out['properties.woning_blokken.maatwerk_woningeigenschappen.category_type'] != 'onbekend')]
    df_onbekend = df_out[(df_out['properties.woning_blokken.mutatiegegevens.contract_type'] == 'onbekend') | (df_out['properties.woning_blokken.maatwerk_woningeigenschappen.category_type'] == 'onbekend')]

    df_koop['jaartal_temp'] = df_koop['properties.jaartal']
    df_koop.loc[df_koop['jaartal_temp'] < koop_mapping['jaar'].min(), 'jaartal_temp'] = koop_mapping['jaar'].min()
    df_koop.loc[df_koop['jaartal_temp'] > koop_mapping['jaar'].max(), 'jaartal_temp'] = koop_mapping['jaar'].max()

    df_huur['jaartal_temp'] = df_huur['properties.jaartal']
    df_huur.loc[df_huur['jaartal_temp'] < huur_mapping['jaar'].min(), 'jaartal_temp'] = huur_mapping['jaar'].min()
    df_huur.loc[df_huur['jaartal_temp'] > huur_mapping['jaar'].max(), 'jaartal_temp'] = huur_mapping['jaar'].max()

    df_koop = pd.merge(left=df_koop, right=koop_mapping, left_on=['jaartal_temp', 'properties.woning_blokken.maatwerk_woningeigenschappen.category_type'], right_on=['jaar', 'categorie'], how='left')
    df_huur = pd.merge(left=df_huur, right=huur_mapping, left_on=['jaartal_temp', 'properties.woning_blokken.maatwerk_woningeigenschappen.category_type'], right_on=['jaar', 'categorie'], how='left')

    df_out = pd.concat([df_koop, df_huur, df_onbekend])

    df_out = df_out.drop(['jaartal_temp', 'jaar', 'categorie'], axis=1)
    df_out = df_out.rename({'hoog': 'properties.woning_blokken.waarde.hoog', 'laag': 'properties.woning_blokken.waarde.laag'}, axis=1)


    return df_out


def add_locatie(df_out, df_in):

    df_out = pd.merge(left=df_out, right=df_in[['properties.globalid', 'properties.gemeente']], on=['properties.globalid'], how='left')

    df_out = df_out.rename({'properties.gemeente': 'properties.woning_blokken.locatie.gemeente'}, axis=1)

    return df_out


def add_woningblok_maatwerkeigenschappen(df_out, df_in):
    """

        woning_blok['maatwerk_woningeigenschappen']['bouw_gerealiseerd'] = True if old_project['properties']['bouw_gerealiseerd'] == 'ja' else False
        woning_blok['maatwerk_woningeigenschappen']['flexwoningen_aantal'] = old_project['properties']['flexwoningen']
        woning_blok['maatwerk_woningeigenschappen']['tijdelijke_woningen_aantal'] = old_project['properties']['tijdelijke_woningen']
        woning_blok['maatwerk_woningeigenschappen']['onbekendtype_huur_totaal'] = old_project['properties']['onbekendtype_huur_totaal']
        woning_blok['maatwerk_woningeigenschappen']['onbekendtype_koop_totaal'] = old_project['properties']['onbekendtype_koop_totaal']
    """

    df_in.loc[df_in['properties.bouw_gerealiseerd'] == 'Ja', 'properties.bouw_gerealiseerd'] = True
    df_in.loc[df_in['properties.bouw_gerealiseerd'] == 'Nee', 'properties.bouw_gerealiseerd'] = False
    df_in.loc[df_in['properties.sloop_gerealiseerd'] == 'Ja', 'properties.sloop_gerealiseerd'] = True
    df_in.loc[df_in['properties.sloop_gerealiseerd'] == '0', 'properties.sloop_gerealiseerd'] = False

    df_temp_koop = df_in[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.flexwoningen', 'properties.tijdelijke_woningen', 'properties.sloop_gerealiseerd']]
    df_temp_sloop = df_in[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.flexwoningen', 'properties.tijdelijke_woningen', 'properties.sloop_gerealiseerd']]

    df_temp_koop['properties.sloop_gerealiseerd'] = None
    df_temp_sloop['properties.bouw_gerealiseerd'] = None

    df_out_koop = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'bouw']
    df_out_sloop = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'sloop']
    df_out_onbekend = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'onbekend']

    df_out_koop = pd.merge(left=df_out_koop, right=df_temp_koop, on=['properties.globalid'], how='left')
    df_out_sloop = pd.merge(left=df_out_sloop, right=df_temp_sloop, on=['properties.globalid'], how='left')

    df_temp_koop = df_temp_koop[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.flexwoningen', 'properties.tijdelijke_woningen']]
    df_temp_sloop = df_temp_sloop[['properties.globalid', 'properties.sloop_gerealiseerd']]

    df_out_onbekend = pd.merge(left=df_out_onbekend, right=df_temp_koop, on=['properties.globalid'], how='left')
    df_out_onbekend = pd.merge(left=df_out_onbekend, right=df_temp_sloop, on=['properties.globalid'], how='left')
    
    df_out = pd.concat([df_out_koop, df_out_sloop, df_out_onbekend])

    return df_out.reset_index(drop=True)


def prepare_for_json(df, required_columns):
    df = df.fillna(np.nan).replace({np.nan: None})

    for column in required_columns:
        if column not in df.columns:
            df[column] = None

    return df


def form_json_structure(df_out, geo_template, required_columns):
    df_out = prepare_for_json(df=df_out, required_columns=required_columns)

    geo_out = dict(copy.deepcopy(geo_template))
    geo_project = dict(copy.deepcopy(geo_template['features'][0]))
    geo_woningblok = dict(copy.deepcopy(geo_template['features'][0]['properties']['woning_blokken'][0]))

    geo_out['features'] = []
    geo_project['properties']['woning_blokken'] = []

    for project_id in df_out['properties.parent_globalid'].unique():
        df_project = df_out[df_out['properties.parent_globalid'] == project_id]
        s_project = df_project.iloc[0]
        new_project = copy.deepcopy(geo_project)

        # geometry
        new_project['geometry']['type'] = s_project['geometry.type']
        new_project['geometry']['coordinates'] = s_project['geometry.coordinates']

        # projectgegevens
        # basisgegevens
        new_project['properties']['projectgegevens']['basisgegevens']['identificatie_nr'] = s_project['properties.projectgegevens.basisgegevens.identificatie_nr']
        new_project['properties']['projectgegevens']['basisgegevens']['naam'] = s_project['properties.projectgegevens.basisgegevens.naam']

        # projectgegevens
        new_project['properties']['projectgegevens']['projectgegevens']['plan_soort'] = s_project['properties.projectgegevens.projectgegevens.plan_soort']
        new_project['properties']['projectgegevens']['projectgegevens']['in_programmering'] = s_project['properties.projectgegevens.projectgegevens.in_programmering']
        new_project['properties']['projectgegevens']['projectgegevens']['prioritering'] = s_project['properties.projectgegevens.projectgegevens.prioritering']
        new_project['properties']['projectgegevens']['projectgegevens']['rol_gemeente'] = s_project['properties.projectgegevens.projectgegevens.rol_gemeente']
        new_project['properties']['projectgegevens']['projectgegevens']['status'] = s_project['properties.projectgegevens.projectgegevens.status']

        # rollen
        new_project['properties']['projectgegevens']['rollen']['projectleider'] = s_project['properties.projectgegevens.rollen.projectleider']
        new_project['properties']['projectgegevens']['rollen']['opdrachtgever'] = s_project['properties.projectgegevens.rollen.opdrachtgever']

        # projectduur
        new_project['properties']['projectgegevens']['projectduur']['start_project'] = s_project['properties.projectgegevens.projectduur.start_project']
        new_project['properties']['projectgegevens']['projectduur']['eind_project'] = s_project['properties.projectgegevens.projectduur.eind_project']

        # projectfasen
        new_project['properties']['projectgegevens']['projectfasen']['1_initiatieffase'] = s_project['properties.projectgegevens.projectfasen.1_initiatieffase']
        new_project['properties']['projectgegevens']['projectfasen']['2_projectfase'] = s_project['properties.projectgegevens.projectfasen.2_projectfase']
        new_project['properties']['projectgegevens']['projectfasen']['3_vergunningsfase'] = s_project['properties.projectgegevens.projectfasen.3_vergunningsfase']
        new_project['properties']['projectgegevens']['projectfasen']['4_realisatiefase'] = s_project['properties.projectgegevens.projectfasen.4_realisatiefase']
        new_project['properties']['projectgegevens']['projectfasen']['5_opleverfase'] = s_project['properties.projectgegevens.projectfasen.5_opleverfase']

        # planologische_planstatus
        new_project['properties']['projectgegevens']['planologische_planstatus']['1a_onherroepelijk'] = s_project['properties.projectgegevens.planologische_planstatus.1A. Onherroepelijk']
        new_project['properties']['projectgegevens']['planologische_planstatus']['1b_onherroepelijk_uitwerking_nodig'] = s_project['properties.projectgegevens.planologische_planstatus.1B. Onherroepelijk, uitwerkingsplicht']
        new_project['properties']['projectgegevens']['planologische_planstatus']['2a_vastgesteld'] = s_project['properties.projectgegevens.planologische_planstatus.2A. Vastgesteld']
        new_project['properties']['projectgegevens']['planologische_planstatus']['2c_vastgesteld_b&w_nodig'] = s_project['properties.projectgegevens.planologische_planstatus.2C. Vastgesteld, wijzigingsbevoegdheid']
        new_project['properties']['projectgegevens']['planologische_planstatus']['3_in_voorbereiding'] = s_project['properties.projectgegevens.planologische_planstatus.3. In voorbereiding']
        new_project['properties']['projectgegevens']['planologische_planstatus']['4a_niet_opgenomen_in_de_visie'] = s_project['properties.projectgegevens.planologische_planstatus.4A. Visie']
        new_project['properties']['projectgegevens']['planologische_planstatus']['4b_opgenomen_in_de_visie'] = s_project['properties.projectgegevens.planologische_planstatus.4B. Idee']

        # maatwerkeigenschappen
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['opdrachtgever_type'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.opdrachtgever_type']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['percentage_opp_buitenstedelijk'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.percentage_opp_buitenstedelijk']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['provincie'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.provincie']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['regio'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.regio']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['bestemmingsplan'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.bestemmingsplan']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['toelichting_knelpunten'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.toelichting_knelpunten']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['beoogd_woonmilieu_ABF5'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.beoogd_woonmilieu_ABF5']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['status_planologisch_groep'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.status_planologisch_groep']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['opmerkingen_kwalitatief'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.opmerkingen_kwalitatief']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['woondeal'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.woondeal']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['realiseerbaarheid'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.realiseerbaarheid']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['projectfase'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.projectfase']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['projectleider'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.projectleider']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['opdrachtgever'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.opdrachtgever']
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen']['vertrouwelijkheid'] = s_project['properties.projectgegevens.maatwerk_eigenschappen.vertrouwelijkheid']

        for index in df_project.index:
            s_woningblok = df_project.loc[index]
            new_woningblok = copy.deepcopy(geo_woningblok)

            # mutatiegegevens
            new_woningblok['mutatiegegevens']['mutatie_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.mutatie_type']
            new_woningblok['mutatiegegevens']['eigendom_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.eigendom_type']
            new_woningblok['mutatiegegevens']['woning_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.woning_type']
            new_woningblok['mutatiegegevens']['contract_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.contract_type']
            new_woningblok['mutatiegegevens']['aantal'] = s_woningblok['properties.woning_blokken.mutatiegegevens.aantal']
            new_woningblok['mutatiegegevens']['status'] = s_woningblok['properties.woning_blokken.mutatiegegevens.status']

            # einddatum
            new_woningblok['einddatum'] = s_woningblok['properties.woning_blokken.mutatiegegevens.einddatum']

            # waarde
            new_woningblok['waarde']['laag'] = s_woningblok['properties.woning_blokken.waarde.laag']
            new_woningblok['waarde']['hoog'] = s_woningblok['properties.woning_blokken.waarde.hoog']

            # locatie
            new_woningblok['locatie']['gemeente'] = s_woningblok['properties.woning_blokken.locatie.gemeente']

            # maatwerk_eigenschappen
            new_woningblok['maatwerk_woningeigenschappen']['category_type'] = s_woningblok['properties.woning_blokken.maatwerk_woningeigenschappen.category_type']
            new_woningblok['maatwerk_woningeigenschappen']['bouw_gerealiseerd'] = s_woningblok['properties.bouw_gerealiseerd']
            new_woningblok['maatwerk_woningeigenschappen']['sloop_gerealiseerd'] = s_woningblok['properties.sloop_gerealiseerd']
            new_woningblok['maatwerk_woningeigenschappen']['flexwoningen'] = s_woningblok['properties.flexwoningen']
            new_woningblok['maatwerk_woningeigenschappen']['tijdelijke_woningen'] = s_woningblok['properties.tijdelijke_woningen']

            new_project['properties']['woning_blokken'].append(new_woningblok)

        geo_out['features'].append(new_project)

    return geo_out


def write_to_geojson(json_out, gemeente):
    gemeente_naam = f'{gemeente}_prepared'
    with open(f"output/{gemeente}.geojson", 'w') as f:
        geojson.dump(json_out, f, cls=NumpyEncoder)


def add_status_to_mutatie(df_out, df_in):
    df_out['properties.projectgegevens.projectgegevens.status'] = None

    df_out.loc[pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) > datetime.datetime.now(), 'properties.projectgegevens.projectgegevens.status'] = 'nieuw, nog niet begonnen'
    df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) <= datetime.datetime.now()) &
               (pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) > datetime.datetime.now()),
    'properties.projectgegevens.projectgegevens.status'] = 'actief - loopt nog'

    df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) < datetime.datetime.now()), 'properties.projectgegevens.projectgegevens.status'] = 'afgerond'

    temp = df_out[['properties.parent_globalid', 'properties.woning_blokken.mutatiegegevens.mutatie_type', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']].drop_duplicates()
    temp = temp[['properties.parent_globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']].groupby('properties.parent_globalid').all().all(axis=1)

    temp = temp.reset_index().rename({0: 'project_gerealiseerd'}, axis=1)
    df_out = pd.merge(left=df_out, right=temp, on='properties.parent_globalid', how='left')

    df_out.loc[(df_out['properties.projectgegevens.projectgegevens.status'] == 'afgerond') & (df_out['project_gerealiseerd'] == False), 'properties.projectgegevens.projectgegevens.status'] = 'afgebroken'

    df_out['properties.woning_blokken.mutatiegegevens.status'] = None

    df_out.loc[df_out['properties.jaartal'] < datetime.datetime.now().year, 'properties.woning_blokken.mutatiegegevens.status'] = 'afgerond'

    temp = df_out[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']]
    temp = temp.set_index('properties.globalid').all(axis=1)
    temp = temp.reset_index().rename({0: 'woningblok_gerealiseerd'}, axis=1)

    df_out['woningblok_gerealiseerd'] = temp['woningblok_gerealiseerd']
    df_out.loc[(df_out['properties.woning_blokken.mutatiegegevens.status'] == 'afgerond') & (df_out['woningblok_gerealiseerd'] == False), 'properties.woning_blokken.mutatiegegevens.status'] = 'afgebroken'

    return df_out