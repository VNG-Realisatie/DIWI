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

gemeente_df_dict = func.fix_maatwerk(paths=all_gemeente_geojson_paths, required_columns=const.required_input_columns)

for gemeente_name in gemeente_df_dict:
    #gemeente_name = 'Planregistratie_gemeente_Amersfoort_Verrijkt_6924747028786393809'
    df_in = gemeente_df_dict[gemeente_name]
    df_out = func.create_df_outs(df=df_in)

    df_out = func.fill_type_geometry(df_out=df_out, df_in=df_in)
    df_out = func.fill_projectgegevens(df_out=df_out, df_in=df_in, project_fasen=const.project_fasen)

    df_out = func.explode_huizenblokken_add_aantallen(df_out=df_out, df_in=df_in, mapping_values=mapping_values)

    # missing: grootte

    df_out = func.add_woningwaardes(df_out=df_out, df_in=df_in, koop_mapping=koop_mapping, huur_mapping=huur_mapping)

    # missing: fysiek_voorkomen

    # missing: grondpositie

    df_out = func.add_woningblok_gerealiseerd(df_out=df_out, df_in=df_in)

    df_out = func.add_status_to_mutatie(df_out=df_out, df_in=df_in)

    json_out = func.form_json_structure(df_out=df_out, df_in=df_in, geo_template=geo_template, required_input_columns=const.required_input_columns, required_output_columns=const.required_output_columns)

    func.write_to_geojson(json_out=json_out, output_path=const.output_path, gemeente=gemeente_name)
