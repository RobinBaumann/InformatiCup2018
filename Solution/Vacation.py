from datetime import date

class Vacation(object):
    state = ""
    vac_type = ""
    start_dt = date.min
    end_dt = date.min


    def __init__(self, state, vac_type, start_dt, end_dt):
        self.state = state
        self.vac_type = vac_type
        self.start_dt = start_dt
        self.end_dt = end_dt

    def to_string(self):
        print("Bundesland: {}\n"
              "Ferien_Typ: {}\n"
              "\tAnfang: {}\n"
              "\tEnde: {}\n".format(self.state, self.vac_type, self.start_dt, self.end_dt))
