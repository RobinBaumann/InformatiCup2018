import matplotlib.pyplot as plt
import os
import pandas as pd
import numpy as np

DATASET_PATH = "../Eingabedaten/"
GAS_PRICES = "../Eingabedaten/Benzinpreise/"

gas_stations_with_prices_id_list = [int(os.path.splitext(os.path.basename(os.path.join(GAS_PRICES, f)))[0]) for f in os.listdir(os.path.join(GAS_PRICES)) if os.path.isfile(os.path.join(GAS_PRICES, f))]

gas_stations = pd.DataFrame()

def load_gas_station_data(data_location=DATASET_PATH):
    gas_stations = os.path.join(data_location, "Tankstellen.csv")
    return pd.read_csv(gas_stations, sep=";", names=["id", "name", "brand", "street", "house_number", "zip_code", "city", "latitude", "longitude"])


def load_historic_prices(gas_station_id, data_location=DATASET_PATH):
    path = data_location + "Benzinpreise/{}.csv".format(gas_station_id)
    historic_prices = os.path.join(path)
    return pd.read_csv(historic_prices, sep=";", header=None)

def calculate_mean_prices(gas_stations_id_list):
    mean_prices = [load_historic_prices(x)[1].mean() for x in gas_stations_id_list]
    return mean_prices

gas_stations = load_gas_station_data()
clean_gas_stations = gas_stations[gas_stations['id'].isin(gas_stations_with_prices_id_list)]
clean_gas_stations.reset_index()
clean_gas_stations.head()

means = calculate_mean_prices(sorted(gas_stations_with_prices_id_list))

means_df = pd.DataFrame(means, index=sorted(gas_stations_with_prices_id_list))
data = pd.concat([clean_gas_stations, means_df], axis=1)
clean_gas_stations.head()

ax = gas_stations.plot(kind="scatter",
                       x="longitude",
                       y="latitude",
                       alpha=0.2,
                       figsize=(10,10),
                       c=0,
                       cmap=plt.get_cmap("jet"),
                       colorbar=True
                      )

ax.set_xlabel("Längengrad")
ax.set_ylabel("Breitengrad")