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
}
