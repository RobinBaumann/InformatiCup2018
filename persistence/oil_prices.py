import pandas as pd
import os

FILE_LOCATION = os.path.join('../Eingabedaten/Oelpreise/')

def read_csv(file=FILE_LOCATION):
    f_brent = os.path.join(FILE_LOCATION, 'brent.csv')
    f_wti = os.path.join(FILE_LOCATION, 'wti.csv')
    df_brent = pd.read_csv(f_brent, sep=';', header=0, decimal=',',
                           names=['date', 'start', 'high', 'low', 'price_brent', 'volume'], parse_dates=['date'])
    df_wti = pd.read_csv(f_wti, sep=';', header=0, decimal=',',
                           names=['date_wti', 'start', 'high', 'low', 'price_wti', 'volume'], parse_dates=['date_wti'])

    return df_brent, df_wti


def get_oilprices():
    df_brent, df_wti = read_csv(FILE_LOCATION)
    df_brent.drop(['start', 'high', 'low', 'volume'], axis=1, inplace=True)
    df_wti.drop(['start', 'high', 'low', 'volume'], axis=1, inplace=True)
    result = pd.concat([df_brent, df_wti], axis=1, join='inner')
    result.drop(['date_wti'], axis=1, inplace=True)
    return result


if __name__ == "__main__":
    get_oilprices()