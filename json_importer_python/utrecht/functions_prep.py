import pandas as pd
import geojson
import functions as func


def fix_amersfoort_data(path, source_path):
    gemeente_name = str(path).split('/')[-1].split('.')[0]

    gjson = func.read_geo_file(geo_path=path)

    func.write_to_geojson(json_out=gjson, output_path=source_path, gemeente=gemeente_name)


def fix_debilt_data(path, source_path):
    gemeente_name = str(path).split('/')[-1].split('.')[0]

    gjson = func.read_geo_file(geo_path=path)

    func.write_to_geojson(json_out=gjson, output_path=source_path, gemeente=gemeente_name)


def fix_lopik_data(path, source_path):
    gemeente_name = str(path).split('/')[-1].split('.')[0]

    gjson = func.read_geo_file(geo_path=path)
    gjson['features'] = [x for x in gjson['features'] if x['geometry'] is not None]

    func.write_to_geojson(json_out=gjson, output_path=source_path, gemeente=gemeente_name)


def fix_delft_data(path, source_path):
    gemeente_name = str(path).split('/')[-1].split('.')[0]

    gjson = func.read_geo_file(geo_path=path)

    new_feature_list = []
    for feature in gjson['features']:
        parent_globalid = feature['properties']['plan_objectid']
        feature['properties']['parent_globalid'] = parent_globalid
        feature['properties']['status_planologisch_groep'] = feature['properties']['status']
        feature['properties']['created'] = feature['properties']['CreationDate']
        new_feature_list.append(feature)

    gjson['features'] = new_feature_list

    func.write_to_geojson(json_out=gjson, output_path=source_path, gemeente=gemeente_name)
