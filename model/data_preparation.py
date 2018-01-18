import numpy as np
from sqlalchemy import create_engine
import pandas as pd
from time import time

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

    x_train_adjusted = {}
    x_test_adjusted = {}
    y_train_adjusted = {}
    y_test_adjusted = {}

    for idx in ids:
        prices_for_id = prices.loc[prices['station_id'] == idx]
        train, test, ts_train, ts_test = train_test_split(prices_for_id['price'].as_matrix(),
                                                          prices_for_id["time_stamp"].as_matrix())
        test = np.log(test.reshape(-1, 1))
        train = np.log(train.reshape(-1, 1))
        x_train = train[0:-2]
        ts_train = ts_train[0:-2]
        y_train_adjusted[idx] = train[1:-1]
        x_test = test[0:-2]
        ts_test = ts_test[0:-2]
        y_test_adjusted[idx] = test[1:-1]
        x_train_adjusted[idx] = []
        x_test_adjusted[idx] = []
        vac = hol = dow = 0
        t1 = time()
        for i, p in enumerate(x_train):
            if ts_train[i].astype('datetime64[h]').tolist().time().hour % 24 == 0 or i == 0:
                vac, hol, dow = get_vacation_holiday_and_weekday(prices, ts_train[i])

            features = list()
            features.append(np.float(p[0]))
            features.append(ts_train[i])
            features.append(int(vac))
            features.append(int(hol))
            features.append(int(dow))
            features.append(idx)
            features.append(hash(stations.loc[stations['id'] == idx, 'brand'].as_matrix()[0]))
            features.append(encode_state(stations.loc[stations['id'] == idx, 'bland'].as_matrix()[0]))
            features.append(stations.loc[stations['id'] == idx, 'kreis'].as_matrix()[0])
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'abahn_id'].as_matrix()[0]))
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'bstr_id'].as_matrix()[0]))
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'sstr_id'].as_matrix()[0]))
            x_train_adjusted[idx].append(features)
        print(time() - t1)

        x_test_adjusted[idx] = []
        vac = hol = dow = 0
        t2 = time()
        for i, p in enumerate(x_test):
            if ts_test[i].astype('datetime64[h]').tolist().time().hour % 24 == 0 or i == 0:
                vac, hol, dow = get_vacation_holiday_and_weekday(prices, ts_test[i])
            features = list()
            features.append(np.float(p[0]))
            features.append(ts_test[i])
            features.append(int(vac))
            features.append(int(hol))
            features.append(int(dow))
            features.append(idx)
            features.append(hash(stations.loc[stations['id'] == idx, 'brand'].as_matrix()[0]))
            features.append(encode_state(stations.loc[stations['id'] == idx, 'bland'].as_matrix()[0]))
            features.append(stations.loc[stations['id'] == idx, 'kreis'].as_matrix()[0])
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'abahn_id'].as_matrix()[0]))
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'bstr_id'].as_matrix()[0]))
            features.append(~pd.isnull(stations.loc[stations['id'] == idx, 'sstr_id'].as_matrix()[0]))
            x_test_adjusted[idx].append(features)
        print(time() - t2)

    return x_train_adjusted, y_train_adjusted, x_test_adjusted, y_test_adjusted

def prepare_data_2(con, batch_size=100):
    active_stations = pd.read_sql_query("""
    select id 
    from stations
    where min_ts <= '2015-01-01'
      and max_ts >= '2017-09-18'""", con)

    ids = np.random.choice(active_stations['id'], batch_size)

    ids_param = ','.join([str(x) for x in ids])

    query = """
    select 
      p.price,
      p.year,
      p.month,
      p.day_of_month,
      p.day_of_week,
      p.hour_of_day,
      p.is_vacation::int,
      p.is_holiday::int,
      p.days_until_vacation,
      p.days_until_holiday,
      s.id as station_id,
      s.brand_no,
      s.bland_no,
      s.kreis,
      s.abahn_id,
      s.bstr_id,
      s.sstr_id
    from stations as s
    inner join prices_sampled as p on s.id = p.station_id
      and s.id in (%s)
      and p.time_stamp between '2013-10-01' and '2017-09-18'
    order by s.id, p.time_stamp
      """ % ids_param

    data = pd.read_sql_query(query, con)
    data.fillna(0)

    #data.to_csv("data/dataset.csv")

    return data


def train_test_split(series, time_stamps, train_amount=0.8):
    train_size = int(len(series) * train_amount)
    train, test, ts_train, ts_test = series[0:train_size], series[train_size:len(series)], \
                                     time_stamps[0:train_size], time_stamps[train_size:len(time_stamps)]
    return train, test, ts_train, ts_test


def get_vacation_holiday_and_weekday(prices, time_stamp):
    ts_price = prices[prices["time_stamp"] == time_stamp]
    return ts_price["is_vacation"].as_matrix()[0], ts_price["is_holiday"].as_matrix()[0], ts_price["day_of_week"].as_matrix()[0]


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
    t = time()
    res = prepare_data_2(con, 15)
    print(time() - t)
