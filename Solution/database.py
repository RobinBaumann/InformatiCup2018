import psycopg2
import glob
import os
import pandas as pd
from io import StringIO

con = psycopg2.connect("host='localhost' port='5432' dbname='infocup'")
cursor = con.cursor()
cursor.execute('truncate table stations cascade;')
f = open(r'../Eingabedaten/Tankstellen.csv', 'r')
cursor.copy_from(f, 'stations', sep=';')
f.close()
con.commit()
os.chdir('../Eingabedaten/Benzinpreise')
files = glob.glob('*.csv')
counter = 0
for price_file in files:
    station_id = int(os.path.splitext(price_file)[0])
    df = pd.read_csv(price_file, delimiter=';')
    df['station_id'] = station_id
    buf = StringIO()
    df.to_csv(buf, header=False, index=False)
    buf.seek(0)
    cursor.copy_from(buf, 'prices', sep=',', columns=('time_stamp', 'price', 'station_id'))
    con.commit()
    counter += 1
    if counter % 100 == 0:
        print(str(counter) + ' / ' + str(len(files)))
con.close()
print('done')
