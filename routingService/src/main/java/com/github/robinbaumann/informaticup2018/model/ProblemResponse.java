package com.github.robinbaumann.informaticup2018.model;

import java.text.MessageFormat;

// see https://tools.ietf.org/html/draft-nottingham-http-problem-06
public class ProblemResponse {
    private String type;
    private String title;
    private int status;
    private String detail;

    public final static String PARAMETER_FORMAT =
            "https://github.com/RobinBaumann/InformatiCup2018/ParameterFormat";
    public final static String STATION_NOT_FOUND =
            "https://github.com/RobinBaumann/InformatiCup2018/StationNotFound";
    public final static String INTERNAL_ERROR =
            "https://github.com/RobinBaumann/InformatiCup2018/InternalError";
    public final static String EMPTY_ROUTE =
            "https://github.com/RobinBaumann/InformatiCup2018/EmptyRoute";
    public final static String STOPS_OUT_OF_ORDER =
            "https://github.com/RobinBaumann/InformatiCup2018/RouteStopsOutOfOrder";
    public final static String CAPACITY_INVALID =
            "https://github.com/RobinBaumann/InformatiCup2018/CapacityException";
    public final static String STATION_NOT_REACHABLE =
            "https://github.com/RobinBaumann/InformatiCup2018/StationNotReachable";

    public ProblemResponse(String type, String title, int status, String detail) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public static ProblemResponse canNotParseParameter(String detail) {
        return new ProblemResponse(
                PARAMETER_FORMAT,
                "Malformed request parameter.",
                400,
                detail
        );
    }

    public static ProblemResponse stationNotFound(StationNotFoundException e) {
        return new ProblemResponse(
                STATION_NOT_FOUND,
                "Requested GasStation not found.",
                404,
                MessageFormat.format("The GasStation with id {0} could not be found.", e.getId())
        );
    }

    public static ProblemResponse internalError() {
        return new ProblemResponse(
                INTERNAL_ERROR,
                "An internal error occurred.",
                500,
                "An internal error occurred, we are fixing it asap."
        );
    }

    public static ProblemResponse emptyRoute() {
        return new ProblemResponse(
                EMPTY_ROUTE,
                "A route must consist of at least two stops.",
                400,
                "A route must consist of at least two stops."
        );
    }

    public static ProblemResponse routePointsOutOfOrder(RoutePointsOutOfOrderException e) {
        return new ProblemResponse(
                STOPS_OUT_OF_ORDER,
                "Some stops were out of order.",
                400,
                MessageFormat.format("The timestamps must be in the same order as the stops. " +
                        "Stop {0} was out of order", e.getI())
        );
    }

    public static ProblemResponse capacityException(CapacityException e) {
        return new ProblemResponse(
                CAPACITY_INVALID,
                "Capacity must be a positive Integer",
                400,
                MessageFormat.format("Capacity must be a positive Integer, but instead was {0}",
                        e.getCapacity())
        );
    }

    public static ProblemResponse stationNotReachable() {
        return new ProblemResponse(
                STATION_NOT_REACHABLE,
                "Capacity is not high enough.",
                400,
                "The given capacity is not high enough to reach all stations."
        );
    }
}
