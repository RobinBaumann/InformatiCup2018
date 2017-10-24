from datetime import date
import holidays


class Holiday(object):
    date = ""
    state = ""
    name = ""

    def __init__(self, date, state, name):
        self.date = date
        self.state = state
        self.name = name


    def __str__(self):
        return "Datum: {} ; Bundesland: {} ; Feiertag: {}".format(self.date, self.state, self.name)

    def to_string(self):
        print(self)