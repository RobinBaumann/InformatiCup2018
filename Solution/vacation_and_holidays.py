from icalendar import Calendar
import os
from Vacation import Vacation
import urllib3
import re
from bs4 import BeautifulSoup
from datetime import date
from Holiday import Holiday
import holidays

VACATION_FILES = "../Eingabedaten/Ferien/"

files = [os.path.join(VACATION_FILES, f) for f in os.listdir(os.path.join(VACATION_FILES)) if
         os.path.isfile(os.path.join(VACATION_FILES, f))]
states = {
    'BW': 'Baden-Württemberg',
    'BY': 'Bayern',
    'BE': 'Berlin',
    'BB': 'Brandenburg',
    'HB': 'Bremen',
    'HH': 'Hamburg',
    'HE': 'Hessen',
    'MV': 'Mecklenburg-Vorpommern',
    'NI': 'Niedersachsen',
    'NW': 'Nordrhein-Westfalen',
    'RP': 'Rheinland-Pfalz',
    'SL': 'Saarland',
    'SN': 'Sachsen',
    'ST': 'Sachsen-Anhalt',
    'SH': 'Schleswig-Holstein',
    'TH': 'Thüringen',
}
vacation_types = [
    'Winterferien',
    'Osterferien',
    'Pfingstferien',
    'Sommerferien',
    'Herbstferien',
    'Weihnachtsferien',
]
range_regex = re.compile('(\d\d)\.(\d\d). - (\d\d)\.(\d\d)\.')


def scrape_2013_14():
    years = [2012, 2013, 2014]
    vacs = []
    http = urllib3.PoolManager()
    for year in years:
        vacs.extend(scrape_year(year, http))
    return vacs


def scrape_year(year, http):
    vacs = []
    response = http.request('GET', f'http://www.schulferien.org/Schulferien_nach_Jahren/{year}/schulferien_{year}.html')
    soup = BeautifulSoup(response.data, 'html.parser')
    table = soup.find('table', class_='sf_table')
    rows = table.tbody.find_all('tr')
    for i, state in enumerate(states):
        row = rows[i]
        for vac_type in vacation_types:
            cell = row.find('td', attrs={'data-header': vac_type})
            match = range_regex.match(cell.text.strip())
            if match:
                end_year = year + 1 if vac_type == 'Weihnachtsferien' else year
                start = date(year, int(match.group(2)), int(match.group(1)))
                end = date(end_year, int(match.group(4)), int(match.group(3)))
                vac = Vacation(
                    states[state],
                    vac_type,
                    start,
                    end)
                vacs.append(vac)
    return vacs


def create_vacation_objects(file):
    f = open(file, 'rb')
    cal = Calendar.from_ical(f.read())
    ev0 = cal.walk("vevent")
    vacations = []
    for event in ev0:
        state, vac_type = split_summary(event.get('summary'))
        start_date = event.get('dtstart').dt
        end_date = event.get('dtend').dt
        vacation = Vacation(state, vac_type, start_date, end_date)
        vacations.append(vacation)
        vacation.to_string()

    return vacations


def get_holidays():
    hdays = []
    for key, value in states.items():
        for date, name in sorted(holidays.DE(prov=key, years=range(2013, 2019)).items()):
            h = Holiday(date, value, name)
            h.to_string()
            hdays.append(Holiday(date, value, name))

    return hdays


def split_summary(summary):
    splitted = summary.split(' ')
    state = splitted[2]
    vac_type = splitted[0]
    return state, vac_type


def get_vacations():
    vacations = []
    for f in files:
        file_vac = create_vacation_objects(f)
        for v in file_vac:
            vacations.append(v)
    return vacations

if __name__ == "__main__":
    get_vacations()
    scrape_2013_14()
    hds = get_holidays()
    print(len(hds))