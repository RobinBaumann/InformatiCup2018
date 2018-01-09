import folium
import database
import codecs

motorways = 'motorway.json'
schnellstr = 'schnellstr.json'
bundesstr = 'bundesstr.json'
laender = 'laender.json'
kreise = 'kreise.json'

def create_json():
    create_motorway()
    create_schnellstr()
    create_bundesstr()
    create_laender()
    create_kreise()


def create_motorway():
    data = database.get_highways()
    with codecs.open(motorways, 'w', 'utf-8') as f:
        f.write(data)


def create_schnellstr():
    data = database.get_schnellstrassen()
    with codecs.open(schnellstr, 'w', 'utf-8') as f:
        f.write(data)


def create_bundesstr():
    data = database.get_bundesstrassen()
    with codecs.open(bundesstr, 'w', 'utf-8') as f:
        f.write(data)


def create_laender():
    data = database.get_bundeslaender()
    with codecs.open(laender, 'w', 'utf-8') as f:
        f.write(data)

def create_kreise():
    data = database.get_kreise()
    with codecs.open(kreise, 'w', 'utf-8') as f:
        f. write(data)


def create_maps():
    create_map(motorways, 'autobahn')
    create_map(schnellstr, 'schnellstr')
    create_map(bundesstr, 'bundesstr')
    create_map(laender, 'laender')
    create_map(kreise, 'kreise')


def create_map(file, name):
    m = folium.Map(location=[47.5472753, 6.0245327])
    folium.GeoJson(file, name=name).add_to(m)
    folium.LayerControl().add_to(m)
    m.save(name + '_map.html')


if __name__ == '__main__':
    create_json()
    create_maps()

