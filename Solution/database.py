import psycopg2
import glob
import os
import pandas as pd
import sys
import vacation_and_holidays
from io import StringIO
from postgis.psycopg import register


def create_connection():
    host = os.environ['INFOCUP_PGHOST']
    port = os.environ['INFOCUP_PGPORT']
    return psycopg2.connect(f"host='{host}' port='{port}' dbname='infocup' user='infocup'")


def import_input():
    con = create_connection()
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


def import_vacations():
    vacations = vacation_and_holidays.get_vacations()
    con = create_connection()
    cursor = con.cursor()
    for v in vacations:
        cursor.execute('insert into vacations (state, type, start_date, end_date) values (%s, %s, %s, %s)',
                       (v.state, v.vac_type, v.start_dt, v.end_dt))
    con.commit()
    cursor.close()
    con.close()


def get_highways():
    con = create_connection()
    register(con)
    cursor = con.cursor()
    cursor.execute("""select row_to_json(fc)::text
    from (select 'FeatureCollection' as type, array_to_json(array_agg(f)) as features
    from  
    (select 'Feature' as type, 
    (select l from (select ref as name) as l) as properties, 
    st_asgeojson(st_collect(linestring))::json as geometry 
    from ways where tags -> 'highway' = 'motorway' group by ref) as f) as fc;""")
    result = cursor.fetchone()
    con.commit()
    con.close()
    return result


if __name__ == '__main__':
    if len(sys.argv) == 1:
        import_input()
        import_vacations()
    elif len(sys.argv) == 2:
        if sys.argv[1] == 'input':
            import_input()
        elif sys.argv[1] == 'vacations':
            import_vacations()

