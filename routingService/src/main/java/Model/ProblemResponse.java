package Model;

import java.text.MessageFormat;

// see https://tools.ietf.org/html/draft-nottingham-http-problem-06
public class ProblemResponse {
    String type;
    String title;
    int status;
    String detail;

    public ProblemResponse(String type, String title, int status, String detail) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public static ProblemResponse canNotParseParameter(String detail) {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/ParameterFormat",
                "Malformed request parameter.",
                 400,
                detail
        );
    }

    public static ProblemResponse stationNotFound(StationNotFoundException e) {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/StationNotFound",
                "Requested GasStation not found.",
                404,
                MessageFormat.format("The GasStation with id {0} could not be found.", e.getId())
        );
    }

    public static ProblemResponse internalError() {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/InternalError",
                "An internal error occurred.",
                500,
                "An internal error occurred, we are fixing it asap."
        );
    }

    public static ProblemResponse emptyRoute() {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/EmptyRoute",
                "A route must consist of at least two stops.",
                400,
                "A route must consist of at least two stops."
        );
    }

    public static ProblemResponse routePointsOutOfOrder(RoutePointsOutOfOrderException e) {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/RouteStopsOutOfOrder",
                "Some stops were out of order.",
                400,
                MessageFormat.format("The timestamps must be in the same order as the stops. " +
                        "Stop {0} was out of order", e.getI())
        );
    }

    public static ProblemResponse capacityException(CapacityException e) {
        return new ProblemResponse(
                "https://github.com/RobinBaumann/InformatiCup2018/CapacityException",
                "Capacity must be a positive Integer",
                400,
                MessageFormat.format("Capacity must be a positive Integer, but instead was {0}",
                        e.getCapacity())
        );
    }
}
