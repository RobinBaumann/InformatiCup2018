package GasStation;

import java.time.LocalTime;

interface IStation {
    /**
     * TODO:
     * for later RESTFul Request to prediction service, needs GasStation-ID and time
     * @param t time parameter
     */
    void setPredictedCost(LocalTime t);
}
