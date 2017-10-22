from icalendar import Calendar
import os
from Vacation import Vacation
VACATION_FILES = "../Eingabedaten/Ferien/"

files = [os.path.join(VACATION_FILES, f) for f in os.listdir(os.path.join(VACATION_FILES)) if os.path.isfile(os.path.join(VACATION_FILES, f))]

def create_py_objects(file):
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


def split_summary(summary):
    splitted = summary.split(' ')
    state = splitted[2]
    vac_type = splitted[0]
    return state, vac_type


def get_vacations():
    vacations = []
    for f in files:
        file_vac = create_py_objects(f)
        for v in file_vac:
            vacations.append(v)
    return vacations


if __name__ == "__main__":
    get_vacations()