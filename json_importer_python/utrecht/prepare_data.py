from pathlib import Path
import pandas as pd
import constants as const
import functions_prep as func

path_amersfoort = Path(const.raw_folder, 'Planregistratie_gemeente_Amersfoort_Verrijkt_6924747028786393809.geojson')
path_debilt = Path(const.raw_folder, 'Planregistratie_gemeente_DeBilt_Verrijkt_-7744626119962991092.geojson')
path_lopik = Path(const.raw_folder, 'Planregistratie_gemeente_Lopik_Verrijkt.geojson')
path_delft = Path(const.raw_folder, 'Planregistratie_wonen_PROD_gemeente_Delft_DashViewTable_-5500969873566723689.geojson')


func.fix_amersfoort_data(path=path_amersfoort, source_path=const.source_folder)
func.fix_debilt_data(path=path_debilt, source_path=const.source_folder)
func.fix_lopik_data(path=path_lopik, source_path=const.source_folder)
func.fix_delft_data(path=path_delft, source_path=const.source_folder)

