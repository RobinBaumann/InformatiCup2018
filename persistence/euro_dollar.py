import pandas as pd

DATAFILE = "../Eingabedaten/DollarEuroKurs/dollar_per_euro_2013_10-2017.csv"

def load_euro_dollar_data(datafile=DATAFILE):
    dataframe = pd.read_csv(datafile, sep=",", header=None)

    return dataframe.as_matrix()


if __name__ == "__main__":
    load_euro_dollar_data()