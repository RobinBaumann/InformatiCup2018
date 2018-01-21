import argparse

import datetime
from dateutil import parser
import requests
from json import dumps, loads

def parse_route(file):
    result = {}
    with open(file, 'r') as f:
        content = f.readlines()
    result['capacity'] = int(content[0])
    result['routePoints'] = []
    for i in range(1, len(content)):
        parts = content[i].split(';')
        result['routePoints'].append({
            'stationId': int(parts[1]),
            'timestamp': parser.parse(parts[0])
        })
    return result


def parse_pred(file):
    result = {}
    with open(file, 'r') as f:
        content = f.readlines()
    result['predictionRequests'] = []
    for line in content:
        parts = line.split(';')
        result['predictionRequests'].append({
            'momentKnownPrices': parser.parse(parts[0]),
            'momentPrediction': parser.parse(parts[1]),
            'stationId': int(parts[2])
        })
    return result


def parse_file(file, type):
    if type == 'route':
        return parse_route(file)
    if type == 'pred':
        return parse_pred(file)


def route(host, type):
    if type == 'route':
        return host + '/api/simpleRoute'
    if type == 'pred':
        return host + '/api/pricePredictions'


def write_output(data, type, file):
    if type == 'route':
        write_route(data, file)
    if type == 'pred':
        write_pred(data, file)


def write_pred(data, file):
    with open(file, 'w') as f:
        for p in data['predictions']:
            f.write(f"{format_dt(p['momentKnownPrices'])};{format_dt(p['momentPrediction'])};{str(p['station']['id'])};{str(p['price'])}\n")


def write_route(data, file):
    with open(file, 'w') as f:
        for s in data['stops']:
            f.write(f"{format_dt(s['timestamp'])};{str(s['station']['id'])};{str(s['amount'])}\n")


def format_dt(dt):
    return dt.strftime('%Y-%m-%d %H:%M:%S%z')[:-2]


def converter(o):
    if isinstance(o, datetime.datetime):
        return o.__str__().replace(' ', 'T')


def date_hook(json):
    for (key, value) in json.items():
        try:
            parts = value.rsplit(':', 1)
            newVal = ''.join(parts)
            json[key] = datetime.datetime.strptime(newVal, '%Y-%m-%dT%H:%M:%S%z')
        except:
            pass
    return json


def main():
    parser = argparse.ArgumentParser(description='send a CSV to the backend and write result ot CSV')
    parser.add_argument('--input', help='input CSV file to process', required=True)
    parser.add_argument('--output', help='output filename', required=True)
    parser.add_argument('--host', help='host with port that hosts the api without trailing /', required=True)
    parser.add_argument('--type', help='type of input CSV file (route or pred)', required=True)
    args = vars(parser.parse_args())
    payload = parse_file(args['input'], args['type'])
    r = requests.post(route(args['host'], args['type']), data=dumps(payload, default=converter))
    write_output(loads(r.text, object_hook=date_hook), args['type'], args['output'])


if __name__ == '__main__':
    main()