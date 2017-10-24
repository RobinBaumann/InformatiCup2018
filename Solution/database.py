import psycopg2
import glob
import os
import pandas as pd
import sys
import vacation_and_holidays
import json
from urllib.request import urlopen
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
    vacs = vacation_and_holidays.get_vacations()
    vacs.extend(vacation_and_holidays.scrape_2013_14())
    con = create_connection()
    cursor = con.cursor()
    for v in vacs:
        cursor.execute('insert into vacations (state, type, start_date, end_date) values (%s, %s, %s, %s)',
                       (v.state, v.vac_type, v.start_dt, v.end_dt))
    con.commit()
    cursor.close()
    con.close()


def import_holidays():
    hdays = vacation_and_holidays.get_holidays()
    con = create_connection()
    cursor = con.cursor()
    for h in hdays:
        cursor.execute('insert into holidays (state, name, date) values (%s, %s, %s)',
                       (h.state, h.name, h.date))
    con.commit()
    cursor.close()
    con.close()

def get_highways():
    return query("""select row_to_json(fc)::text
    from (select 'FeatureCollection' as type, array_to_json(array_agg(f)) as features
    from  
    (select 'Feature' as type, 
    (select l from (select ref as name) as l) as properties, 
    st_asgeojson(st_collect(linestring))::json as geometry 
    from ways where tags -> 'highway' = 'motorway' group by ref) as f) as fc;""")


def get_bundeslaender():
    return query("""
        select row_to_json(fc)::text
        from (select 'FeatureCollection' as type,
                  array_to_json(array_agg(f)) as features
              from (select 'Feature' as type,
                       (select l from (select r.tags -> 'name' as name) as l) as properties,
                       st_asgeojson(st_collect(w.linestring))::json as geometry
                   from relations as r
                       inner join relation_members as m on r.id = m.relation_id
                       inner join ways as w on m.member_id = w.id
                   where r.tags -> 'boundary' = 'administrative' and r.tags -> 'admin_level' = '4'
                   group by r.id) as f) as fc;
        """)


def get_kreise():
    return query("""
        select row_to_json(fc)::text
        from (select 'FeatureCollection' as type,
                  array_to_json(array_agg(f)) as features
              from (select 'Feature' as type,
                       (select l from (select r.tags -> 'name' as name) as l) as properties,
                       st_asgeojson(st_collect(w.linestring))::json as geometry
                   from relations as r
                       inner join relation_members as m on r.id = m.relation_id
                       inner join ways as w on m.member_id = w.id
                   where r.tags -> 'boundary' = 'administrative' and r.tags -> 'admin_level' = '6'
                   group by r.id) as f) as fc;
        """)


def get_bundesstrassen():
    return query("""select row_to_json(fc)::text
    from (select 'FeatureCollection' as type, array_to_json(array_agg(f)) as features
    from  
    (select 'Feature' as type, 
    st_asgeojson(linestring)::json as geometry 
    from ways where tags -> 'highway' = 'primary') as f) as fc;""")


def get_schnellstrassen():
    return query("""select row_to_json(fc)::text
    from (select 'FeatureCollection' as type, array_to_json(array_agg(f)) as features
    from  
    (select 'Feature' as type, 
    st_asgeojson(linestring)::json as geometry 
    from ways where tags -> 'highway' = 'trunk' and tags -> 'oneway' = 'yes') as f) as fc;""")


def get_all_streets():
    return query("""select row_to_json(fc)::text
    from (select 'FeatureCollection' as type, array_to_json(array_agg(f)) as features
    from  
    (select 'Feature' as type, 
    st_asgeojson(linestring)::json as geometry 
    from ways where tags -> 'highway' is not null) as f) as fc;""")


def query(query):
    con = create_connection()
    register(con)
    cursor = con.cursor()
    cursor.execute(query)
    result = cursor.fetchone()[0]
    con.commit()
    con.close()
    return result


def loadJson(url):
    response = urlopen(url)
    data = response.read()
    values = json.loads(data)
    return values


def getRegions():
    values = loadJson("https://geois.arbeitsagentur.de/arcgis/rest/services/Gebietsstrukturen/MapServer/3/query?f=json&where=valid_from%20%3C=%20CURRENT_DATE%20AND%20valid_to%20%3E=%20CURRENT_DATE&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=ID,region,OBJECTID,parentID&outSR=25832&callback=")
    regions = {}
    for x in values["features"]:
        regions[x["attributes"]["ID"]] = x["attributes"]["region"]
    return regions


def import_commuters():
    regions = getRegions()
    con = create_connection()
    cursor = con.cursor()
    for x in regions:
        print(x)
        from_region = loadJson("https://statistik.arbeitsagentur.de/PendlerData?type=ein&year_month=201606&regionInd="+x+"&view=renderPendler")
        to_region = loadJson("https://statistik.arbeitsagentur.de/PendlerData?type=aus&year_month=201606&regionInd="+x+"&view=renderPendler")
        name = regions[x]
        region = x
        cursor.execute("""
            insert into commuters (region, name, from_region, to_region)
            values (%s, %s, %s, %s);
        """, (region, name, json.dumps(from_region), json.dumps(to_region)))
        con.commit()
    cursor.close()
    con.close()


if __name__ == '__main__':
    if len(sys.argv) == 1:
        import_input()
        import_vacations()
        import_holidays()
        import_commuters()
    elif len(sys.argv) == 2:
        if sys.argv[1] == 'input':
            import_input()
        elif sys.argv[1] == 'vacations':
            import_vacations()
        elif sys.argv[1] == 'holidays':
            import_holidays()
        elif sys.argv[1] == 'commuters':
            import_commuters()

