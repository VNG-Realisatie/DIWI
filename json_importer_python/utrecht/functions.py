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
    df_out[f'{local_prefix}.naam'] = df_in['properties.plannaam'].fillna("UNKNOWN_NAME")

    return df_out


def add_projectgegevens(df_out, df_in, prefix):
    local_prefix = f'{prefix}.projectgegevens'

    df_out[f'{local_prefix}.plan_soort'] = df_in['properties.plantype'].str.upper().str.replace(' ', '_')
    df_out[f'{local_prefix}.in_programmering'] = None
    df_out[f'{local_prefix}.prioritering'] = None
    df_out[f'{local_prefix}.rol_gemeente'] = None
    df_out.loc[df_in['properties.opdrachtgever_type'] == 'Gemeente', f'{local_prefix}.rol_gemeente'] = 'Opdrachtgever'

    df_out[f'{local_prefix}.plan_soort'] = df_out[f'{local_prefix}.plan_soort'].replace('ONBEKEND', None)

    return df_out


def add_rollen(df_out, df_in, prefix):
    local_prefix = f'{prefix}.rollen'

    df_out[f'{local_prefix}.projectleider'] = None
    df_out[f'{local_prefix}.opdrachtgever'] = df_in['properties.opdrachtgever_naam']

    return df_out


def add_projectduur(df_out, df_in, prefix):



    local_prefix = f'{prefix}.projectduur'
    datetime_columns = ['properties.jaartal', 'properties.jaar_start_project', 'properties.oplevering_eerste', 'properties.oplevering_laatste']

    df_out[f'{local_prefix}.start_project'] = None
    df_out[f'{local_prefix}.start_project'] = df_in[datetime_columns].min(axis=1)

    df_out[f'{local_prefix}.eind_project'] = None
    df_out[f'{local_prefix}.eind_project'] = [f"{int(float(x.year))}-12-31" if type(x) != type(None) else None for x in df_in['properties.oplevering_laatste'].fillna(np.nan).replace({np.nan: None})]

    df_out_startdates = df_out[['properties.parent_globalid', f'{local_prefix}.start_project']].groupby(by=['properties.parent_globalid']).min().reset_index()
    df_out_enddates = df_out[['properties.parent_globalid', f'{local_prefix}.eind_project']].groupby(by=['properties.parent_globalid']).max().reset_index()

    df_out = pd.merge(left=df_out, right=df_out_startdates, on=['properties.parent_globalid'])
    df_out= pd.merge(left=df_out, right=df_out_enddates, on=['properties.parent_globalid'])

    df_out[f'{local_prefix}.start_project'] = df_out[f'{local_prefix}.start_project_y']
    df_out[f'{local_prefix}.eind_project'] = df_out[f'{local_prefix}.eind_project_y']

    return df_out


def add_projectfasen(df_out, df_in, prefix, project_fasen):
    # projectfasen

    #df_out = df_out[df_out['properties.parent_globalid'] == 726]
    #df_in = df_in[df_in['properties.parent_globalid'] == 726]

    local_prefix = f'{prefix}.projectfasen'

    df_out[f'{local_prefix}.0. Concept'] = None
    df_out[f'{local_prefix}.1. Initiatief'] = None
    df_out[f'{local_prefix}.2. Definitie'] = None
    df_out[f'{local_prefix}.3. Ontwerp'] = None
    df_out[f'{local_prefix}.4. Voorbereiding'] = None
    df_out[f'{local_prefix}.5. Realisatie'] = None
    df_out[f'{local_prefix}.6. Nazorg'] = None

    df_out = pd.merge(df_out, df_in[['properties.globalid', 'properties.jaar_start_project', 'properties.jaartal', 'properties.oplevering_eerste', 'properties.projectfase']], on='properties.globalid')

    df_out.loc[pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) > datetime.datetime.now(), 'properties.projectgegevens.projectduur.start_project'] = '2024-01-01'


    #global_ids_active = list(df_project_dates.loc[(df_project_dates[project_fasen[3:-1]].notna().any(axis=1)) & (pd.to_datetime(df_project_dates['properties.projectgegevens.projectduur.eind_project']) > datetime.datetime.now())].index)

    #df_out.loc[df_out['properties.globalid'].isin(global_ids_active), 'properties.projectgegevens.projectduur.start_project'] = '2024-01-01'

    df_in = pd.merge(left=df_in, right=df_out[['properties.globalid', 'properties.projectgegevens.projectduur.start_project']], on='properties.globalid')

    df_project_dates = df_in[['properties.globalid', 'properties.projectgegevens.projectduur.start_project', 'properties.projectfase']].pivot(index='properties.globalid', columns='properties.projectfase', values='properties.projectgegevens.projectduur.start_project')
    for column in project_fasen:
        if column not in df_project_dates.columns:
            df_project_dates[column] = None
    if '7. Afgerond' in df_project_dates.columns:
        df_project_dates['5. Realisatie'].fillna(df_project_dates['7. Afgerond'])

    df_project_dates = pd.merge(left=df_project_dates.reset_index(), right=df_out[['properties.globalid', 'properties.oplevering_eerste', 'properties.projectgegevens.projectduur.start_project', 'properties.projectgegevens.projectduur.eind_project']], on=['properties.globalid']).set_index('properties.globalid')

    indexes = list(df_project_dates[(df_project_dates[project_fasen].isna().all(axis=1))].index)
    df_out.loc[df_out['properties.globalid'].isin(indexes), f'{local_prefix}.0. Concept'] = df_out.loc[df_out['properties.globalid'].isin(indexes), 'properties.projectgegevens.projectduur.start_project']

    indexes = df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) < datetime.datetime.now()) & (pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) > datetime.datetime.now())].index
    df_out.loc[indexes, f'{local_prefix}.1. Initiatief'] = df_out.loc[indexes, 'properties.projectgegevens.projectduur.start_project']

    df_project_dates.loc[(df_project_dates['properties.oplevering_eerste'].notna()) & (df_project_dates['5. Realisatie'].isna()), '5. Realisatie'] = df_project_dates.loc[(df_project_dates['properties.oplevering_eerste'].notna()) & (df_project_dates['5. Realisatie'].isna()), 'properties.oplevering_eerste']

    indexes = df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) < datetime.datetime.now())].index
    df_out.loc[indexes, f'{local_prefix}.5. Realisatie'] = df_out.loc[indexes, 'properties.projectgegevens.projectduur.start_project']

    df_project_dates = df_project_dates.drop('properties.oplevering_eerste', axis=1)
    df_project_dates = df_project_dates.drop('properties.projectgegevens.projectduur.start_project', axis=1)
    df_project_dates = df_project_dates.drop('properties.projectgegevens.projectduur.eind_project', axis=1)

    df_project_dates = df_project_dates.dropna(how='all', axis=1)

    df_project_dates = df_project_dates[[column for column in df_project_dates.columns if column in project_fasen]]
    #df_project_dates = df_project_dates.drop('1. Initiatief', axis=1)

    for column in df_project_dates:
        df_project_dates[column] = pd.to_datetime(df_project_dates[column]).dt.strftime('%Y-%m-%d')

    df_project_dates.columns = [f'{local_prefix}.{column}' for column in df_project_dates.columns]

    df_out = pd.merge(left=df_out[[column for column in df_out.columns if column not in df_project_dates.columns]], right=df_project_dates, on='properties.globalid', how='left')

    project_fasen = ['properties.projectgegevens.projectfasen.0. Concept', 'properties.projectgegevens.projectfasen.1. Initiatief',
                     'properties.projectgegevens.projectfasen.2. Definitie', 'properties.projectgegevens.projectfasen.3. Ontwerp',
                     'properties.projectgegevens.projectfasen.4. Voorbereiding', 'properties.projectgegevens.projectfasen.5. Realisatie',
                     'properties.projectgegevens.projectfasen.6. Nazorg']

    temp = df_out[['properties.globalid'] + ['properties.projectgegevens.projectduur.start_project'] + project_fasen + [
        'properties.projectgegevens.projectduur.eind_project']]

    for column in temp.columns[1:]:
        temp.loc[:, column] = pd.to_datetime(temp[column], format='%Y-%m-%d')

    previous_columns = ['properties.projectgegevens.projectduur.start_project']

    #df_out = df_out[df_out['properties.globalid'] == '{D31B293A-862F-4CEA-8443-F8F62B229E7F}']
    for project_fase_column in project_fasen:
        wrong_indexes = temp[((temp[project_fase_column] <= temp[previous_columns].max(axis=1)) | (
            temp[project_fase_column] > temp['properties.projectgegevens.projectduur.eind_project'])) & (temp[project_fase_column].notna())].index

        temp.loc[wrong_indexes, project_fase_column] = temp.loc[wrong_indexes, previous_columns].max(axis=1) + datetime.timedelta(days=1)

        previous_columns.append(project_fase_column)

    for column in project_fasen:
        df_out[column] = pd.to_datetime(temp[column]).dt.strftime('%Y-%m-%d').astype(str)

    return df_out


def add_planologische_planstatus(df_out, df_in, prefix):
    local_prefix = f'{prefix}.planologische_planstatus'

    #df_in['planologisch_datum'] = df_in['properties.created']

    df_in = pd.merge(left=df_in, right=df_out[['properties.globalid', 'properties.projectgegevens.projectduur.start_project', 'properties.projectgegevens.projectduur.eind_project']], on=['properties.globalid'])
    df_in['planologisch_datum'] = df_in['properties.projectgegevens.projectduur.start_project']
    df_in.loc[pd.to_datetime(df_in['properties.projectgegevens.projectduur.eind_project']) < datetime.datetime.now(), 'planologisch_datum'] = df_in.loc[pd.to_datetime(df_in['properties.projectgegevens.projectduur.eind_project']) < datetime.datetime.now(), 'properties.projectgegevens.projectduur.start_project']

    temp = df_in[['properties.globalid', 'properties.status_planologisch', 'planologisch_datum']].pivot(columns=['properties.status_planologisch'],
                                                                                                        index='properties.globalid').replace(np.nan, None)
    temp.columns = [f"{local_prefix}.{x[1]}" for x in temp.columns]
    temp = temp.reset_index()
    df_out = pd.merge(left=df_out, right=temp, on=['properties.globalid'], how='left')

    #df_out = df_out[df_out['properties.parent_globalid'] == 734]
    #df_in = df_in[df_in['properties.parent_globalid'] == 734]

    return df_out


def fill_projectgegevens(df_out, df_in, project_fasen):
    prefix = 'properties.projectgegevens'

    df_out = add_basisgegevens(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectgegevens(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_rollen(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectduur(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_projectfasen(df_out=df_out, df_in=df_in, prefix=prefix, project_fasen=project_fasen)
    df_out = add_planologische_planstatus(df_out=df_out, df_in=df_in, prefix=prefix)
    df_out = add_locatie(df_out=df_out, df_in=df_in, prefix=prefix)

    return df_out


def explode_huizenblokken_add_aantallen(df_out, df_in, mapping_values):
    """
    bouw x sloop
    eensgezin x meersgezins x onbekend
    koop_1,2,3,4,onbekend x huur_1,2,3,4, onbekend
    """

    #df_out = df_out[df_out['properties.globalid'] == '4a1b3c86-2d75-4108-8a4e-a4a5ab228ef1']  # TODO: remove
    #df_in = df_in[df_in['properties.globalid'] == '4a1b3c86-2d75-4108-8a4e-a4a5ab228ef1']  # TODO: remove

    # TODO: use the lowest-level fields for the conditions. e.g. "sloop_meergezins_onbekend"

    prefix = 'properties.woning_blokken'

    dimension_mutation = ['bouw', 'sloop']
    dimension_owner = ['particuliere_verhuurder', 'huur_woningcorporatie', 'onbekend', 'koop']
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

    temp = df_in[['properties.globalid'] + [f'properties.{x}' for x in mapping_values['column'].unique()]]
    temp = temp.set_index(['properties.globalid']).stack().reset_index()
    temp = temp.rename({'level_1': 'column', 0: 'aantal'}, axis=1)
    woning_corporatie_columns = np.unique([x for x in temp['column'] if ('1' in x or '2' in x) and ('huur' in x)])
    particulier_columns = np.unique([x for x in temp['column'] if ('3' in x or '4' in x) and ('huur' in x)])
    temp['owner'] = 'onbekend'
    temp.loc[temp['column'].isin(woning_corporatie_columns), 'owner'] = 'huur_woningcorporatie'
    temp.loc[temp['column'].isin(particulier_columns), 'owner'] = 'particuliere_verhuurder'
    koop_columns = np.unique([x for x in temp['column'] if 'koop' in x])
    temp.loc[temp['column'].isin(koop_columns), 'owner'] = 'koop'
    temp['column'] = [x.split('properties.')[-1] for x in temp['column']]

    #temp.loc[temp['column'].isin(mapping_values[mapping_values['owner'] == 'koopwoning']['column'].values), 'owner'] = 'koopwoning'
    #temp.loc[temp['owner'] == 'Woningbouwcorporatie', 'owner'] = 'huur_woningcorporatie'
    #temp.loc[(temp['owner'] != 'koopwoning') & (temp['owner'] != 'huur_woningcorporatie'), 'owner'] = 'onbekend'

    df_huizenblokken = pd.merge(left=df_huizenblokken, right=temp, on=['properties.globalid', 'owner', 'column'], how='left')
    df_huizenblokken = df_huizenblokken[(df_huizenblokken['aantal'] != 0) & (df_huizenblokken['aantal'].notna())]

    temp_jaartal = df_in[['properties.globalid', 'properties.jaartal']]
    temp_jaartal['properties.jaartal'] = [f'{int(x.year)}-12-01' if type(x) != type(None) else None for x in temp_jaartal['properties.jaartal'].fillna(np.nan).replace({np.nan: None})]

    df_huizenblokken = pd.merge(left=df_huizenblokken, right=temp_jaartal, on =['properties.globalid'], how='left')

    df_out = pd.merge(left=df_out, right=df_huizenblokken, on='properties.globalid', how='left')

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

    df_koop['jaartal_temp'] = df_koop['properties.jaartal'].astype(int)
    df_koop.loc[df_koop['jaartal_temp'] < koop_mapping['jaar'].min(), 'jaartal_temp'] = koop_mapping['jaar'].min()
    df_koop.loc[df_koop['jaartal_temp'] > koop_mapping['jaar'].max(), 'jaartal_temp'] = koop_mapping['jaar'].max()

    df_huur['jaartal_temp'] = df_huur['properties.jaartal'].astype(int)
    df_huur.loc[df_huur['jaartal_temp'] < huur_mapping['jaar'].min(), 'jaartal_temp'] = huur_mapping['jaar'].min()
    df_huur.loc[df_huur['jaartal_temp'] > huur_mapping['jaar'].max(), 'jaartal_temp'] = huur_mapping['jaar'].max()

    df_koop = pd.merge(left=df_koop, right=koop_mapping, left_on=['jaartal_temp', 'properties.woning_blokken.maatwerk_woningeigenschappen.category_type'], right_on=['jaar', 'categorie'], how='left')
    df_huur = pd.merge(left=df_huur, right=huur_mapping, left_on=['jaartal_temp', 'properties.woning_blokken.maatwerk_woningeigenschappen.category_type'], right_on=['jaar', 'categorie'], how='left')

    df_out = pd.concat([df_koop, df_huur, df_onbekend])

    df_out = df_out.drop(['jaartal_temp', 'jaar', 'categorie'], axis=1)
    df_out = df_out.rename({'hoog': 'properties.woning_blokken.waarde.hoog', 'laag': 'properties.woning_blokken.waarde.laag'}, axis=1)

    df_out.loc[(df_out['properties.woning_blokken.waarde.laag'].isna()) & (df_out['properties.woning_blokken.waarde.hoog'].notna()), 'properties.woning_blokken.waarde.laag'] = 0

    return df_out


def add_locatie(df_out, df_in, prefix):
    local_prefix = f'{prefix}.locatie'

    df_out[f'{local_prefix}.gemeente'] = df_in['properties.gemeente']
    df_out[f'{local_prefix}.wijk'] = None
    df_out[f'{local_prefix}.buurt'] = None

    return df_out


def add_woningblok_gerealiseerd(df_out, df_in):

    df_in.loc[df_in['properties.bouw_gerealiseerd'] == 'Ja', 'properties.bouw_gerealiseerd'] = True
    df_in.loc[df_in['properties.bouw_gerealiseerd'] == 'Nee', 'properties.bouw_gerealiseerd'] = False
    df_in.loc[df_in['properties.sloop_gerealiseerd'] == 'Ja', 'properties.sloop_gerealiseerd'] = True
    df_in.loc[df_in['properties.sloop_gerealiseerd'] == '0', 'properties.sloop_gerealiseerd'] = False

    df_temp_koop = df_in[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']]
    df_temp_sloop = df_in[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']]

    df_temp_koop['properties.sloop_gerealiseerd'] = None
    df_temp_sloop['properties.bouw_gerealiseerd'] = None

    df_out_koop = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'bouw']
    df_out_sloop = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'sloop']
    df_out_onbekend = df_out[df_out['properties.woning_blokken.mutatiegegevens.mutatie_type'] == 'onbekend']

    df_out_koop = pd.merge(left=df_out_koop, right=df_temp_koop, on=['properties.globalid'], how='left')
    df_out_sloop = pd.merge(left=df_out_sloop, right=df_temp_sloop, on=['properties.globalid'], how='left')

    df_temp_koop = df_temp_koop[['properties.globalid', 'properties.bouw_gerealiseerd']]
    df_temp_sloop = df_temp_sloop[['properties.globalid', 'properties.sloop_gerealiseerd']]

    df_out_onbekend = pd.merge(left=df_out_onbekend, right=df_temp_koop, on=['properties.globalid'], how='left')
    df_out_onbekend = pd.merge(left=df_out_onbekend, right=df_temp_sloop, on=['properties.globalid'], how='left')

    df_out = pd.concat([df_out_koop, df_out_sloop, df_out_onbekend])

    return df_out.reset_index(drop=True)

def remove_wrong_maatwerk_columns(df, maatwerk_columns):
    maatwerk_columns = [x for x in maatwerk_columns if 'totaal' not in x.lower()]

    for column in maatwerk_columns:
        try:
            pd.to_numeric(df[column])
        except TypeError:
            continue
        except ValueError:
            continue

        if ('huur' in column.lower()) or ('koop' in column.lower()) or ('bouw' in column.lower()) or ('sloop' in column.lower()):
            maatwerk_columns = [x for x in maatwerk_columns if x != column]

    return maatwerk_columns


def find_project_maatwerk(df_in, required_input_columns):
    maatwerk_columns = [column for column in df_in.columns if column not in required_input_columns]
    df_maatwerk = df_in[['properties.parent_globalid'] + maatwerk_columns]
    project_maatwerk_columns = list(df_in[maatwerk_columns].columns[(df_maatwerk.groupby(by=['properties.parent_globalid']).nunique() <= 1).all()])

    project_maatwerk_columns = remove_wrong_maatwerk_columns(maatwerk_columns=project_maatwerk_columns, df=df_in)
    return project_maatwerk_columns


def find_woonblok_maatwerk(df_in, required_input_columns):
    maatwerk_columns = [column for column in df_in.columns if column not in required_input_columns]
    df_maatwerk = df_in[['properties.parent_globalid'] + maatwerk_columns]
    woningblok_maatwerk_columns = list(df_in[maatwerk_columns].columns[(df_maatwerk.groupby(by=['properties.parent_globalid']).nunique() > 1).any()])

    woningblok_maatwerk_columns = remove_wrong_maatwerk_columns(maatwerk_columns=woningblok_maatwerk_columns, df=df_in)

    return woningblok_maatwerk_columns


def prepare_for_json(df, required_output_columns):
    df = df.fillna(np.nan).replace({np.nan: None})

    for column in required_output_columns:
        if column not in df.columns:
            df[column] = None

    df['properties.woning_blokken.mutatiegegevens.mutatie_type'] = df['properties.woning_blokken.mutatiegegevens.mutatie_type'].replace({'bouw': 'CONSTRUCTION', 'sloop': 'DEMOLITION', 'onbekend': None})
    df['properties.woning_blokken.mutatiegegevens.woning_type'] = df['properties.woning_blokken.mutatiegegevens.woning_type'].replace({'eengezins_woning': 'EENGEZINSWONING', 'meergezins_woning': 'MEERGEZINSWONING', 'onbekend': None})
    df['properties.woning_blokken.mutatiegegevens.eigendom_type'] = df['properties.woning_blokken.mutatiegegevens.eigendom_type'].replace({'koopwoning': 'KOOPWONING', 'huur_woningcorporatie': 'HUURWONING_WONINGCORPORATIE', 'particuliere_verhuurder': 'HUURWONING_PARTICULIERE_VERHUURDER', 'onbekend': None})
    df['properties.woning_blokken.mutatiegegevens.contract_type'] = df['properties.woning_blokken.mutatiegegevens.contract_type'].replace({'koop': 'PURCHASE', 'huur': 'RENT', 'onbekend': None})
    return df


def add_project_maatwerk_json(new_project, s_project, project_maatwerk_columns):
    for column in project_maatwerk_columns:
        new_column = column.split('.')[-1]
        new_project['properties']['projectgegevens']['maatwerk_projecteigenschappen'][new_column] = s_project[column]
    return new_project


def add_woningblok_maatwerk_json(new_woningblok, s_woningblok, woningblok_maatwerk_columns):
    for column in woningblok_maatwerk_columns:
        new_column = column.split('.')[-1]
        new_woningblok['maatwerk_woningeigenschappen'][new_column] = s_woningblok[column]
    return new_woningblok


def add_maatwerk_values(df_out, df_in, project_maatwerk_columns, woningblok_maatwerk_columns):

    df_project_maatwerk = df_in[['properties.parent_globalid'] + project_maatwerk_columns].replace({np.nan: None})
    df_project_maatwerk = df_project_maatwerk.loc[df_project_maatwerk['properties.parent_globalid'].drop_duplicates().index]
    df_woningblok_maatwerk = df_in[['properties.globalid'] + woningblok_maatwerk_columns].replace({np.nan: None})
    df_woningblok_maatwerk = df_woningblok_maatwerk[df_woningblok_maatwerk['properties.globalid'].isin(df_out['properties.globalid'])]

    df_out = pd.merge(left=df_out, right=df_project_maatwerk, on=['properties.parent_globalid'], how='left')
    df_out = pd.merge(left=df_out, right=df_woningblok_maatwerk, on=['properties.globalid'], how='left')

    return df_out


def form_json_structure(df_out, df_in, geo_template, required_input_columns, required_output_columns):
    df_out = prepare_for_json(df=df_out, required_output_columns=required_output_columns)

    project_maatwerk_columns = find_project_maatwerk(df_in=df_in, required_input_columns=required_input_columns)
    woningblok_maatwerk_columns = find_woonblok_maatwerk(df_in=df_in, required_input_columns=required_input_columns)

    df_out = add_maatwerk_values(df_out=df_out, df_in=df_in, project_maatwerk_columns=project_maatwerk_columns, woningblok_maatwerk_columns=woningblok_maatwerk_columns)

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
        new_project['properties']['projectgegevens']['projectfasen']['_1_CONCEPT'] = s_project['properties.projectgegevens.projectfasen.0. Concept']
        new_project['properties']['projectgegevens']['projectfasen']['_2_INITIATIVE'] = s_project['properties.projectgegevens.projectfasen.1. Initiatief']
        new_project['properties']['projectgegevens']['projectfasen']['_3_DEFINITION'] = s_project['properties.projectgegevens.projectfasen.2. Definitie']
        new_project['properties']['projectgegevens']['projectfasen']['_4_DESIGN'] = s_project['properties.projectgegevens.projectfasen.3. Ontwerp']
        new_project['properties']['projectgegevens']['projectfasen']['_5_PREPARATION'] = s_project['properties.projectgegevens.projectfasen.4. Voorbereiding']
        new_project['properties']['projectgegevens']['projectfasen']['_6_REALIZATION'] = s_project['properties.projectgegevens.projectfasen.5. Realisatie']
        new_project['properties']['projectgegevens']['projectfasen']['_7_AFTERCARE'] = s_project['properties.projectgegevens.projectfasen.6. Nazorg']

        # planologische_planstatus
        new_project['properties']['projectgegevens']['planologische_planstatus']['_1A_ONHERROEPELIJK'] = s_project['properties.projectgegevens.planologische_planstatus.1A. Onherroepelijk']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG'] = s_project['properties.projectgegevens.planologische_planstatus.1B. Onherroepelijk, uitwerkingsplicht']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_1C_ONHERROEPELIJK_MET_BW_NODIG'] = None
        new_project['properties']['projectgegevens']['planologische_planstatus']['_2A_VASTGESTELD'] = s_project['properties.projectgegevens.planologische_planstatus.2A. Vastgesteld']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_2B_VASTGESTELD_MET_UITWERKING_NODIG'] = None
        new_project['properties']['projectgegevens']['planologische_planstatus']['_2C_VASTGESTELD_MET_BW_NODIG'] = s_project['properties.projectgegevens.planologische_planstatus.2C. Vastgesteld, wijzigingsbevoegdheid']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_3_IN_VOORBEREIDING'] = s_project['properties.projectgegevens.planologische_planstatus.3. In voorbereiding']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_4A_OPGENOMEN_IN_VISIE'] = s_project['properties.projectgegevens.planologische_planstatus.4A. Visie']
        new_project['properties']['projectgegevens']['planologische_planstatus']['_4B_NIET_OPGENOMEN_IN_VISIE'] = s_project['properties.projectgegevens.planologische_planstatus.4B. Idee']

        # locatie
        new_project['properties']['projectgegevens']['locatie']['gemeente'] = s_project['properties.projectgegevens.locatie.gemeente']
        new_project['properties']['projectgegevens']['locatie']['wijk'] = s_project['properties.projectgegevens.locatie.wijk']
        new_project['properties']['projectgegevens']['locatie']['buurt'] = s_project['properties.projectgegevens.locatie.buurt']

        new_project = add_project_maatwerk_json(new_project=new_project, s_project=s_project, project_maatwerk_columns=project_maatwerk_columns)

        for index in df_project.index:
            s_woningblok = df_project.loc[index]
            new_woningblok = copy.deepcopy(geo_woningblok)

            # name
            new_woningblok['name'] = s_woningblok['properties.globalid']

            # mutatiegegevens
            new_woningblok['mutatiegegevens']['mutatie_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.mutatie_type']
            if s_woningblok['properties.woning_blokken.mutatiegegevens.eigendom_type']:
                new_woningblok['mutatiegegevens']['eigendom_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.eigendom_type'].replace('koop', 'KOOPWONING')
            else:
                new_woningblok['mutatiegegevens']['eigendom_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.eigendom_type']

            new_woningblok['mutatiegegevens']['woning_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.woning_type']
            new_woningblok['mutatiegegevens']['aantal'] = s_woningblok['properties.woning_blokken.mutatiegegevens.aantal']

            # einddatum
            new_woningblok['einddatum'] = s_woningblok['properties.woning_blokken.mutatiegegevens.einddatum']

            # waarde
            if (s_woningblok['properties.woning_blokken.waarde.laag'] is not None) or (s_woningblok['properties.woning_blokken.waarde.hoog'] is not None):
                new_woningblok['waarde']['laag'] = s_woningblok['properties.woning_blokken.waarde.laag']
                new_woningblok['waarde']['hoog'] = s_woningblok['properties.woning_blokken.waarde.hoog']
            else:
                new_woningblok['waarde'] = None

            new_woningblok = add_woningblok_maatwerk_json(new_woningblok=new_woningblok, s_woningblok=s_woningblok, woningblok_maatwerk_columns=woningblok_maatwerk_columns)
            new_woningblok['maatwerk_woningeigenschappen']['category_type'] = s_woningblok['properties.woning_blokken.maatwerk_woningeigenschappen.category_type']
            new_woningblok['maatwerk_woningeigenschappen']['contract_type'] = s_woningblok['properties.woning_blokken.mutatiegegevens.contract_type']
            new_woningblok['maatwerk_woningeigenschappen']['status'] = s_woningblok['properties.woning_blokken.mutatiegegevens.status']
            new_woningblok['maatwerk_woningeigenschappen']['globalid'] = s_woningblok['properties.globalid']

            new_project['properties']['woning_blokken'].append(new_woningblok)

        geo_out['features'].append(new_project)

    return geo_out


def write_to_geojson(json_out, output_path, gemeente):
    gemeente_naam = f'{gemeente}_prepared'
    with open(f"{output_path}/{gemeente}.geojson", 'w') as f:
        geojson.dump(json_out, f, cls=NumpyEncoder)


def add_status_to_mutatie(df_out, df_in):
    df_out['properties.projectgegevens.projectgegevens.status'] = None

    df_out.loc[pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) > datetime.datetime.now(), 'properties.projectgegevens.projectgegevens.status'] = 'NEW'
    df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.start_project']) <= datetime.datetime.now()) &
               (pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) > datetime.datetime.now()),
    'properties.projectgegevens.projectgegevens.status'] = 'ACTIVE'

    df_out.loc[(pd.to_datetime(df_out['properties.projectgegevens.projectduur.eind_project']) < datetime.datetime.now()), 'properties.projectgegevens.projectgegevens.status'] = 'REALIZED'

    temp = df_out[['properties.parent_globalid', 'properties.woning_blokken.mutatiegegevens.mutatie_type', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']].drop_duplicates()
    temp = temp[['properties.parent_globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']].groupby('properties.parent_globalid').all().all(axis=1)

    temp = temp.reset_index().rename({0: 'project_gerealiseerd'}, axis=1)
    df_out = pd.merge(left=df_out, right=temp, on='properties.parent_globalid', how='left')

    #df_out.loc[(df_out['properties.projectgegevens.projectgegevens.status'] == 'REALIZED') & (df_out['project_gerealiseerd'] == False), 'properties.projectgegevens.projectgegevens.status'] = 'TERMINATED'

    df_out['properties.woning_blokken.mutatiegegevens.status'] = 'UNKNOWN'

    df_out.loc[df_out['properties.jaartal'].astype(int) < datetime.datetime.now().year, 'properties.woning_blokken.mutatiegegevens.status'] = 'REALIZED'

    temp = df_out[['properties.globalid', 'properties.bouw_gerealiseerd', 'properties.sloop_gerealiseerd']]
    temp = temp.set_index('properties.globalid').all(axis=1)
    temp = temp.reset_index().rename({0: 'woningblok_gerealiseerd'}, axis=1)

    df_out['woningblok_gerealiseerd'] = temp['woningblok_gerealiseerd']
    df_out.loc[(df_out['properties.woning_blokken.mutatiegegevens.status'] == 'REALIZED') & (df_out['woningblok_gerealiseerd'] == False), 'properties.woning_blokken.mutatiegegevens.status'] = 'TERMINATED'

    return df_out


def fix_maatwerk(paths, required_columns):
    #geo_gemeente_path = '/home/wieger/Workspace/diwi/json_importer_python/utrecht/source_data/Planregistratie_gemeente_Amersfoort_Verrijkt_6924747028786393809.geojson'
    df_out = pd.DataFrame()
    for geo_gemeente_path in paths:
        gemeente_name = str(geo_gemeente_path).split('/')[-1].split('.')[0]
        print(gemeente_name)
        geo_gemeente = read_geo_file(geo_path=geo_gemeente_path)

        df_in = pd.json_normalize(geo_gemeente['features'])
        df_in.columns = [x.lower() for x in df_in.columns]
        df_in['gemeente'] = gemeente_name
        df_out = pd.concat([df_out, df_in])

    gemeente_df_dict = {}

    for gemeente in df_out['gemeente'].unique():
        df_gemeente = df_out[df_out['gemeente'] == gemeente]
        empty_columns = df_gemeente.columns[df_gemeente.isna().all(axis=0)]
        columns_to_remove = [column for column in empty_columns if column not in required_columns]
        df_gemeente = df_gemeente[[column for column in df_gemeente.columns if column not in columns_to_remove]]

        for column in required_columns:
            if column not in df_out.columns:
                df_gemeente[column] = None
        df_gemeente = df_gemeente.drop('gemeente', axis=1)
        gemeente_df_dict[gemeente] = df_gemeente

    return gemeente_df_dict


def set_datetime_columns(df_in):
    df_in['properties.jaartal'] = df_in['properties.jaartal'].replace({1: np.nan, 2: np.nan, 0: np.nan, 3: np.nan})
    df_in['properties.jaartal'] = pd.to_datetime(df_in['properties.jaartal'].astype(float), format='%Y')

    df_in['properties.jaar_start_project'] = df_in['properties.jaar_start_project'].replace({1: np.nan, 2: np.nan, 0: np.nan, 3: np.nan})
    df_in['properties.jaar_start_project'] = pd.to_datetime(df_in['properties.jaar_start_project'].astype(float), format='%Y')

    df_in['properties.created'] = pd.to_datetime(df_in['properties.created'])
    df_in['properties.edited'] = pd.to_datetime(df_in['properties.edited'])

    df_in['properties.oplevering_eerste'] = pd.to_datetime(df_in['properties.oplevering_eerste'].astype(float), format='%Y')
    df_in['properties.oplevering_laatste'] = pd.to_datetime(df_in['properties.oplevering_laatste'].astype(float), format='%Y')

    return df_in


def set_datetimes_to_string(df_out, df_in):
    datetime_columns = list(df_out.select_dtypes(include=[np.datetime64]).columns)
    for column in datetime_columns:
        df_out[column] = df_out[column].dt.strftime('%Y-%m-%d').astype(str)

    datetime_columns = list(df_in.select_dtypes(include=[np.datetime64]).columns)
    for column in datetime_columns:
        df_in[column] = df_in[column].dt.strftime('%Y-%m-%d').astype(str)

    df_out.replace('nan', np.nan)

    for column in df_out.columns:
        df_out[column] = df_out[column].replace('nan', None)

    for column in df_in.columns:
        df_in[column] = df_in[column].replace('nan', None)

    return df_out, df_in
