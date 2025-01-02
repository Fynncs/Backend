package br.com.fynncs.filter.exception;

import br.com.fynncs.filter.exception.model.ResultProcessing;
import br.com.fynncs.filter.exception.model.ResultProcessingDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author miqueias.nadaluti
 */
public abstract class MapperGenericException {

    @ExceptionHandler({Exception.class})
    public static ResponseEntity<Object> errorResponse(Exception ex) {
        Response.StatusType type = getStatusType(ex);

        GenericEntity<ResultProcessing> errorMessageGeneric = genericEntity(type, ex);
        return ResponseEntity.status(type.getStatusCode()).body(errorMessageGeneric);
    }

    public static void errorResponse(HttpServletResponse response, Exception ex) throws IOException {
        Response.StatusType type = getStatusType(ex);
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(type.getStatusCode());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"status\": \"%s\", \"error\": \"%s\", \"trace\": \"%s\"}",
                type.getStatusCode(),
                type.toEnum(),
                mapper.writeValueAsString(genericEntity(type, ex).getEntity().getDatails())
        ));
    }

    private static GenericEntity<ResultProcessing> genericEntity(Response.StatusType type, Exception ex) {
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

        return new GenericEntity<ResultProcessing>(resultProcessing) {
        };
    }

    private static Response.StatusType getStatusType(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse().getStatusInfo();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }
}
