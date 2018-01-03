import numpy as np
from sqlalchemy import create_engine
import pandas as pd


def create_connection():
    host = 'localhost'
    port = '3333'
    return create_engine("postgresql://infocup@{}:{}/infocup".format(host, port))


def prepare_data(con, batch_size=20):
    # select stations between 2015-01-01 and 2017-09-18
    active_stations = pd.read_sql_query("""
    select id, min_ts, max_ts
    from stations
    where min_ts <= '2015-01-01'
    and max_ts >= '2017-09-18'""", con)

    # subsample 100 stations, because the full dataset is to large
    ids = np.random.choice(active_stations['id'], batch_size)

    param = ','.join([str(x) for x in ids])
    query = """select * from prices_sampled where station_id in (%s) 
    and time_stamp between '2015-01-01' and '2017-09-18'""" % param
    prices = pd.read_sql_query(query, con)

    query = """select * from stations where id in (%s) """ % param
    stations = pd.read_sql_query(query, con)

    mask = prices['station_id'].isin(ids)
    prices = prices.loc[mask]

    prepared_data = {}
    for idx in ids:
        prices_for_id = prices.loc[prices['station_id'] == idx]
        train, test, ts_train, ts_test = train_test_split(prices_for_id['price'].as_matrix(), prices_for_id["time_stamp"].as_matrix())
        time_series = {"train": train, "test": test}
        prepared_data[idx]["time_series"] = time_series
        prepared_data[idx]["train_stamps"] = ts_train
        prepared_data[idx]["test_stamps"] = ts_test
        prepared_data[idx]["brand"] = stations.loc[stations['id'] == idx, 'brand'].as_matrix()[0].lower()
        prepared_data[idx]["state"] = stations.loc[stations['id'] == idx, 'bland'].as_matrix()[0].lower()
        prepared_data[idx]["county"] = stations.loc[stations['id'] == idx, 'kreis'].as_matrix()[0].lower()
        prepared_data[idx]["abahn"] = ~np.isnan(stations.loc[stations['id'] == idx, 'abahn_id'].as_matrix()[0])
        prepared_data[idx]["bstr"] = ~np.isnan(stations.loc[stations['id'] == idx, 'bstr_id'].as_matrix()[0])
        prepared_data[idx]["sstr"] = ~np.isnan(stations.loc[stations['id'] == idx, 'sstr_id'].as_matrix()[0])

    return prepared_data


def train_test_split(series, time_stamps, train_amount=0.8):
    train_size = int(len(series) * train_amount)
    train, test, ts_train, ts_test = series[0:train_size], series[train_size:len(series)], \
                                     time_stamps[0:train_size], time_stamps[train_size:len(time_stamps)]
    return train, test, ts_train, ts_test

