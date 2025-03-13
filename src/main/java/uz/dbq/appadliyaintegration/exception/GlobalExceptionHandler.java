package uz.dbq.appadliyaintegration.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.dbq.appadliyaintegration.payload.response.ApiResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Retrieves the field names in the order they are declared in the DTO.
     */
    private List<String> getFieldOrder(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Object target = ex.getBindingResult().getTarget();
        List<String> fieldOrder = target != null ? getFieldOrder(target.getClass()) : null;

        List<FieldError> fieldErrors = new ArrayList<>(ex.getBindingResult().getFieldErrors());

        if (fieldOrder != null) {
            fieldErrors.sort(Comparator.comparingInt(fe -> fieldOrder.indexOf(fe.getField())));
        }

        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiResponse response = new ApiResponse();
        response.setMessage(errorMessage);
        response.setSuccess(false);
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

