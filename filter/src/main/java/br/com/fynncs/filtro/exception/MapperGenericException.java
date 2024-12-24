
package br.com.fynncs.filtro.exception;

import br.com.fynncs.filtro.exception.model.ResultProcessing;
import br.com.fynncs.filtro.exception.model.ResultProcessingDetail;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;

/**
 * @author miqueias.nadaluti
 */
public abstract class MapperGenericException {

    @ExceptionHandler({Exception.class})
    public static ResponseEntity<Object> errorResponse(Exception ex) {
        Response.StatusType type = getStatusType(ex);

        ResultProcessing resultProcessing
                = new ResultProcessing(String.valueOf(type.getStatusCode()),
                ex.getMessage(), ex);
        if (ex.getStackTrace() != null) {
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                if (resultProcessing.getDatails() == null) {
                    resultProcessing.setDatails(new ArrayList<>());
                }
                resultProcessing.getDatails().add(new ResultProcessingDetail() {
                    {
                        setInfoDeveloper("trace: " + stackTraceElement.toString());
                    }
                });
            }
        }

        GenericEntity<ResultProcessing> errorMessageGeneric
                = new GenericEntity<>(resultProcessing) {
        };
        return ResponseEntity.status(Integer.parseInt(resultProcessing.getCode())).body(errorMessageGeneric);
    }

    private static Response.StatusType getStatusType(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse().getStatusInfo();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }
}
