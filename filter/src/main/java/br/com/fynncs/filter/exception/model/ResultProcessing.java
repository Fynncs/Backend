package br.com.fynncs.filter.exception.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultProcessing {

    private String code;
    private String message;
    private String correction;
    private Date dateTime;
    private String status;
    private List<ResultProcessingDetail> datails;
    private Throwable cause;

    public ResultProcessing(String code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
        initialaizer();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultProcessingDetail> getDatails() {
        return datails;
    }

    public void setDatails(List<ResultProcessingDetail> datails) {
        this.datails = datails;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    private void initialaizer() {
        if (cause != null) {
            Throwable causeDatail = cause.getCause();
            addDatail(causeDatail);
        }
    }

    public void addDatail(Throwable causeDatail) {
        while (causeDatail != null) {
            addDatail(causeDatail.getMessage(), null,
                    null, (causeDatail.getStackTrace() != null &&
                            causeDatail.getStackTrace().length > 0 ?
                            causeDatail.getStackTrace()[0].getFileName() : "") + " - "
                            + (cause.getStackTrace() != null && cause.getStackTrace().length > 0 ?
                            cause.getStackTrace()[0].getClassName() + " - "
                                    + cause.getStackTrace()[0].getMethodName() + " - "
                                    + cause.getStackTrace()[0].getLineNumber() : ""));
            causeDatail = causeDatail.getCause();
        }
    }

    public void addDatail(String message, String correction,
                          String associatedProperty, String infoDeveloper) {
        ResultProcessingDetail datail
                = new ResultProcessingDetail();
        datail.setMessage(message);
        datail.setCorrection(correction);
        datail.setAssociatedProperty(associatedProperty);
        datail.setInfoDeveloper(infoDeveloper);
        if (datails == null) {
            datails = new ArrayList<>();
        }
        datails.add(datail);
    }

    @Override
    public String toString() {
        String retorno = status + " - " + code + " - " + message;
        if (datails != null && !datails.isEmpty()) {
            retorno = datails.stream().map((datail) -> "\n" +
                    datail.getMessage()).reduce(retorno, String::concat);
        }
        return retorno;
    }
}
