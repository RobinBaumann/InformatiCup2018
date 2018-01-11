package Model;

// see https://tools.ietf.org/html/draft-nottingham-http-problem-06
public abstract class ProblemResponse {
    String type;
    String title;
    int status;
    String detail;
}
