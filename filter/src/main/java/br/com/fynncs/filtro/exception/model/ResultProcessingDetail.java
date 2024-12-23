package br.com.fynncs.filtro.exception.model;

public class ResultProcessingDetail {

    private String message;
    private String correction;
    private String associatedProperty;
    private String infoDeveloper;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public String getAssociatedProperty() {
        return associatedProperty;
    }

    public void setAssociatedProperty(String associatedProperty) {
        this.associatedProperty = associatedProperty;
    }

    public String getInfoDeveloper() {
        return infoDeveloper;
    }

    public void setInfoDeveloper(String infoDeveloper) {
        this.infoDeveloper = infoDeveloper;
    }
}
