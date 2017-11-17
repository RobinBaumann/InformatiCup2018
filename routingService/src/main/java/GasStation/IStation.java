package GasStation;

import java.time.LocalTime;

interface IStation {
    /**
     * TODO:
     * for later RESTful Request to prediction service, needs GasStation-ID and time
     * @param t
     */
    void setPredictedCost(LocalTime t);
}
