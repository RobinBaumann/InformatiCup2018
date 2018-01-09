import requests
from time import sleep, time
import os
import psycopg2
from json import dump


baseurl = 'http://nominatim.openstreetmap.org/search'
headers = {
    'user-agent': 'infocupcheck/1.0 (Script to check lat/lon for gas stations for the contest InformatiCup https://gi.de/informaticup/ - We are participants.)'
}
common_params = {
    'format': 'json',
    'countrycodes': 'de',
    'email': 'carlo.goetz@gmail.com',
    'polygon_geojson': '1',
    'addressdetails': '1'
}

def create_connection():
    host = os.environ['INFOCUP_PGHOST']
    port = os.environ['INFOCUP_PGPORT']
    return psycopg2.connect(f"host='{host}' port='{port}' dbname='infocup' user='infocup'")


def query_params(street, house_num, city, postalcode):
    result = common_params.copy()
    result['street'] = str(house_num) + ' ' + street
    result['city'] = city
    result['postalcode'] = postalcode
    return result


def query_db():
    con = create_connection()
    cursor = con.cursor()
    cursor.execute("""
    select street, house_number, zip_code, city, id
    from stations where min_ts is not null""")
    results = cursor.fetchall()
    cursor.close()
    con.close()
    return results


def make_requests(data):
    results = {}
    errors = {}
    i = 0
    total = len(data)
    for station in data:
        if i % 100 == 0:
            print(str(i) + ' / ' + str(total))
        params = query_params(station[0], station[1], station[3], station[2])
        start = time() * 1000
        result = requests.get(baseurl, headers=headers, params=params)
        if result.status_code == requests.codes.ok:
            payload = result.json()
            results[str(station[4])] = payload
        else:
            payload = result.text
            errors[str(station[4])] = payload
        end = time() * 1000
        delta = 1100 - (end - start)
        if delta > 0:
            sleep(float('.' + str(delta).replace('.', '')))
        i += 1
    return (results, errors)


def save(results):
    with open('results.json', 'w') as f:
        dump(results[0], f)
    with open('errors.json', 'w') as f:
        dump(results[1], f)


def main():
    data = query_db()
    results = make_requests(data)
    save(results)


if __name__ == '__main__':
    main()