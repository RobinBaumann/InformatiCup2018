import psycopg2
import glob
import os
import pandas as pd
import sys
import vacation_and_holidays
import euro_dollar
import oil_prices
import json
import geojson
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


def import_oil_prices():
    con = create_connection()
    cursor = con.cursor()
    prices = oil_prices.get_oilprices()
    for i, p in prices.iterrows():
        cursor.execute('insert into oil_prices (date, price_brent, price_wti) values (%s, %s, %s)',
                       (p['date'], p['price_brent'], p['price_wti']))
    con.commit()
    cursor.close()
    con.close()


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


def import_dollar_per_euro():
    data = euro_dollar.load_euro_dollar_data()
    con = create_connection()
    cursor = con.cursor()
    for d in data:
        cursor.execute('insert into dollar_per_euro (date, value) values (%s, %s)',
                       (d[0], d[1]))

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


def collect_ways_for(con, rel_id):
    ways = set()
    rels = {rel_id}
    while rels:
        cursor = con.cursor()
        cursor.execute("""
        select member_id, member_type 
        from relation_members 
        where relation_id in %s
        """, (tuple(rels),))
        rels = set()
        for result in cursor.fetchall():
            if result[1] == 'R':
                rels.add(result[0])
            elif result[1] == 'W':
                ways.add(result[0])
            else:
                print(result[1])
        cursor.close()
    return ways


def update_autobahn():
    con = create_connection()
    cursor = con.cursor()
    cursor.execute("select rel_id from autobahn;")
    ids = [x[0] for x in cursor]
    i = 0
    for id in ids:
        i += 1
        print(str(id))
        print(str(i) + '/' + str(len(ids)))
        ways = collect_ways_for(con, id)
        cursor.execute("""
        update autobahn set geom = (select st_collect(linestring)
                                    from ways where id in %s)
                                    where rel_id = %s
        """, (tuple(ways),id))
    cursor.close()
    con.commit()
    con.close()


def import_blaender():
    files = glob.glob('../geojson/*.geojson')
    con = create_connection()
    cursor = con.cursor()
    for file in files:
        with open(file, 'r') as f:
            bland = geojson.loads(f.read())
            import_bland(cursor, bland)
    con.commit()
    cursor.close()
    con.close()


def import_bland(cursor, bland):
    cursor.execute("""
    select id from bundeslaender where rel_id = %s
    """, (int(bland.name), ))
    entry_id = cursor.fetchone()[0]
    geom_count = 0
    for feature in bland.features:
        geom_count += 1
        if feature.geometry.type != 'Polygon':
            print(bland.name + ' has geometry of type ' + feature.geometry.type)
            continue
        if geom_count == 1:
            update_poly(cursor, entry_id, feature.geometry, bland.crs)
        else:
            cursor.execute("""
            insert into bundeslaender (tags, rel_id, geom, geom_4839)
            (select tags, rel_id, geom, geom_4839 from bundeslaender where id = %s)
            RETURNING id
            """, (entry_id, ))
            id = cursor.fetchone()[0]
            update_poly(cursor, id, feature.geometry, bland.crs)


def update_poly(cursor, id, poly, crs):
    poly['crs'] = crs
    cursor.execute("""
            update bundeslaender set poly_4839 = ST_GeomFromGeoJSON(%s) where id = %s
            """, (geojson.dumps(poly), id))


if __name__ == '__main__':
    if len(sys.argv) == 1:
        import_input()
        import_vacations()
        import_holidays()
        import_oil_prices()
        import_dollar_per_euro()
        import_commuters()
        update_autobahn()
        import_blaender()
    elif len(sys.argv) == 2:
        if sys.argv[1] == 'input':
            import_input()
        elif sys.argv[1] == 'vacations':
            import_vacations()
        elif sys.argv[1] == 'holidays':
            import_holidays()
        elif sys.argv[1] == 'dollar_per_euro':
            import_dollar_per_euro()
        elif sys.argv[1] == 'oil_prices':
            import_oil_prices()
        elif sys.argv[1] == 'commuters':
            import_commuters()
        elif sys.argv[1] == 'autobahn':
            update_autobahn()
        elif sys.argv[1] == 'blaender':
            import_blaender()

