package fsciamdev;

public class Result {
    private String fileName;
    private boolean isCompleted;
    private String exceptionMessage;

    public Result(String fileName, boolean isCompleted, String exceptionMessage) {
        this.fileName = fileName;
        this.isCompleted = isCompleted;
        this.exceptionMessage = exceptionMessage;
    }
    public Result(String fileName, boolean isCompleted) {
        this.fileName = fileName;
        this.isCompleted = isCompleted;
        this.exceptionMessage = null;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
