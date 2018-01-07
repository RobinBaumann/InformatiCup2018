import numpy as np
from sqlalchemy import create_engine
import pandas as pd
import holidays


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
    prices = pd.read_sql_query(query, con, parse_dates=["time_stamp"])

    query = """select * from stations where id in (%s) """ % param
    stations = pd.read_sql_query(query, con)

    mask = prices['station_id'].isin(ids)
    prices = prices.loc[mask]

    prepared_data = {}
    for idx in ids:
        prices_for_id = prices.loc[prices['station_id'] == idx]
        train, test, ts_train, ts_test = train_test_split(prices_for_id['price'].as_matrix(), prices_for_id["time_stamp"].as_matrix())
        time_series = {"train": train, "test": test}
        prepared_data[idx] = {}
        prepared_data[idx]["time_series"] = time_series
        prepared_data[idx]["train_stamps"] = ts_train
        prepared_data[idx]["test_stamps"] = ts_test
        prepared_data[idx]["brand"] = stations.loc[stations['id'] == idx, 'brand'].as_matrix()[0]
        prepared_data[idx]["state"] = stations.loc[stations['id'] == idx, 'bland'].as_matrix()[0]
        prepared_data[idx]["county"] = stations.loc[stations['id'] == idx, 'kreis'].as_matrix()[0]
        prepared_data[idx]["abahn"] = ~np.isnan(stations.loc[stations['id'] == idx, 'abahn_id'].as_matrix()[0])
        prepared_data[idx]["bstr"] = ~np.isnan(stations.loc[stations['id'] == idx, 'bstr_id'].as_matrix()[0])
        prepared_data[idx]["sstr"] = ~np.isnan(stations.loc[stations['id'] == idx, 'sstr_id'].as_matrix()[0])

    for key in prepared_data.keys():
        prepared_data[key]["time_series"]["test"] = np.log(prepared_data[key]["time_series"]["test"].reshape(-1, 1))
        prepared_data[key]["time_series"]["train"] = np.log(prepared_data[key]["time_series"]["train"].reshape(-1, 1))

    x_train_adjusted = {}
    x_test_adjusted = {}
    y_train_adjusted = {}
    y_test_adjusted = {}

    for k in prepared_data.keys():
        x_train = prepared_data[k]["time_series"]["train"][0:-2]
        ts_train = prepared_data[k]["train_stamps"][0:-2]
        y_train_adjusted[k] = prepared_data[k]["time_series"]["train"][1:-1]
        x_test = prepared_data[k]["time_series"]["test"][0:-2]
        ts_test = prepared_data[k]["test_stamps"][0:-2]
        y_test_adjusted[k] = prepared_data[k]["time_series"]["test"][1:-1]
        x_train_adjusted[k] = []
        x_test_adjusted[k] = []
        for i, p in enumerate(x_train):
            vac, hol, dow = get_vacation_holiday_and_weekday(prices, ts_train[i])
            features = []
            features.append(np.float(p[0]))
            features.append(ts_train[i])
            features.append(int(vac))
            features.append(int(hol))
            features.append(int(dow))
            features.append(encode_state(prepared_data[k]["state"]))
            features.append(k)
            features.append(hash(prepared_data[k]["brand"]))
            features.append(prepared_data[k]["county"])
            features.append(int(prepared_data[k]["abahn"]))
            features.append(int(prepared_data[k]["bstr"]))
            features.append(int(prepared_data[k]["sstr"]))
            x_train_adjusted[k].append(features)

        x_test_adjusted[k] = []
        for i, p in enumerate(x_test):
            vac, hol, dow = get_vacation_holiday_and_weekday(prices, ts_test[i])
            features = []
            features.append(np.float(p[0]))
            features.append(ts_test[i])
            features.append(int(vac))
            features.append(int(hol))
            features.append(int(dow))
            features.append(k)
            features.append(hash(prepared_data[k]["brand"]))
            features.append(encode_state(prepared_data[k]["state"]))
            features.append(prepared_data[k]["county"])
            features.append(int(prepared_data[k]["abahn"]))
            features.append(int(prepared_data[k]["bstr"]))
            features.append(int(prepared_data[k]["sstr"]))
            x_test_adjusted[k].append(features)

    return x_train_adjusted, y_train_adjusted, x_test_adjusted, y_test_adjusted


def train_test_split(series, time_stamps, train_amount=0.8):
    train_size = int(len(series) * train_amount)
    train, test, ts_train, ts_test = series[0:train_size], series[train_size:len(series)], \
                                     time_stamps[0:train_size], time_stamps[train_size:len(time_stamps)]
    return train, test, ts_train, ts_test


def get_vacation_holiday_and_weekday(prices, time_stamp):
    ts_price = prices[prices["time_stamp"] == time_stamp]
    return ts_price["is_vacation"].as_matrix()[0], ts_price["is_holiday"].as_matrix()[0], ts_price["day_of_week"].as_matrix()[0]


def get_vacations(con):
    return pd.read_sql_query("""select * from vacations""", con)


def encode_state(data):
    states = {
     'Baden-Württemberg':1,
     'Bayern':2,
     'Berlin':3,
     'Brandenburg':4,
     'Bremen':5,
     'Hamburg':6,
     'Hessen':7,
     'Mecklenburg-Vorpommern':8,
     'Niedersachsen':9,
     'Nordrhein-Westfalen':10,
     'Rheinland-Pfalz':11,
     'Saarland':12,
     'Sachsen':13,
     'Sachsen-Anhalt':14,
     'Schleswig-Holstein':15,
     'Thüringen':16
    }
    return states[data]


def get_state_shortcut(state):
    states_short = {
        'Baden-Württemberg': 'BW',
        'Bayern': 'BY',
        'Berlin': 'BE',
        'Brandenburg': 'BB',
        'Bremen': 'HB',
        'Hamburg': 'HH',
        'Hessen': 'HE',
        'Mecklenburg-Vorpommern': 'MV',
        'Niedersachsen': 'NI',
        'Nordrhein-Westfalen': 'NW',
        'Rheinland-Pfalz': 'RP',
        'Saarland': 'SL',
        'Sachsen': 'SN',
        'Sachsen-Anhalt': 'ST',
        'Schleswig-Holstein': 'SH',
        'Thüringen': 'TH',
    }
    return states_short[state]

if __name__ == "__main__":
    con = create_connection()
    res = prepare_data(con)
    print(res)