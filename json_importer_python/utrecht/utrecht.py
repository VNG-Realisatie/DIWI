from pathlib import Path
import geojson
import pandas as pd
import datetime
import numpy as np
import copy
from numpyencoder import NumpyEncoder
import functions as func
import constants as const

koop_mapping, huur_mapping, planologisch_dict, mapping_values = func.read_supportive_files(mapping_values=const.mapping_values_path)
geo_template = func.read_geo_file(geo_path=const.geo_template_path)

all_gemeente_geojson_paths = func.find_all_gemeentes_files_paths(path_source=const.source_folder)

dict_projects = {}
for geo_gemeente_path in all_gemeente_geojson_paths:
    geo_gemeente_path = '/home/wieger/Workspace/diwi/json_importer_python/utrecht/source_data/Planregistratie_gemeente_Amersfoort_Verrijkt_6924747028786393809.geojson'
    gemeente_name = str(geo_gemeente_path).split('/')[-1].split('.')[0]
    geo_gemeente = func.read_geo_file(geo_path=geo_gemeente_path)

    df_in = pd.json_normalize(geo_gemeente['features'])
    df_in.columns = [x.lower() for x in df_in.columns]
    df_out = func.create_df_outs(df=df_in)

    df_out = func.fill_type_geometry(df_out=df_out, df_in=df_in)
    df_out = func.fill_projectgegevens(df_out=df_out, df_in=df_in, project_fasen=const.project_fasen)

    df_out = func.explode_huizenblokken_add_aantallen(df_out=df_out, df_in=df_in, mapping_values=mapping_values)

    # missing: oplevering
    # missing: grootte

    df_out = func.add_woningwaardes(df_out=df_out, df_in=df_in, koop_mapping=koop_mapping, huur_mapping=huur_mapping)

    # missing: fysiek_voorkomen

    df_out = func.add_locatie(df_out=df_out, df_in=df_in)

    # missing: grondpositie

    df_out = func.add_woningblok_maatwerkeigenschappen(df_out=df_out, df_in=df_in)

    df_out = func.add_status_to_mutatie(df_out=df_out, df_in=df_in)

    json_out = func.form_json_structure(df_out=df_out, geo_template=geo_template, required_columns=const.required_columns)

    func.write_to_geojson(json_out=json_out, output_path=const.output_path, gemeente=gemeente_name)
