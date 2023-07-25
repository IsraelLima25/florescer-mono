package br.com.loja.florescer.exception;

import java.util.List;
import java.util.ArrayList;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResourceExceptionHandler {
	
    private MessageSource messageSource;

    public ResourceExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public List<CampoInvalido> handlerArgumentNotValid(MethodArgumentNotValidException exception) {

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        List<CampoInvalido> camposInvalidos = extrairErros(fieldErrors);
        return camposInvalidos;
    }
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({BusinessException.class})
    public String handlerBusinessException(BusinessException exception) {
        return exception.getMessage();
    }
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public CampoInvalido handlerNotFoundException(NotFoundException exception) {
        CampoInvalido campoInvalido = new CampoInvalido(exception.getCampo(), exception.getMensagem());
        return campoInvalido;
    }
	
    private List<CampoInvalido> extrairErros(List<FieldError> fieldErrors){
        List<CampoInvalido> camposInvalido = new ArrayList<>();
        fieldErrors.forEach(error -> {
            CampoInvalido fieldErro = new CampoInvalido(error.getField(),
                    messageSource.getMessage(error, LocaleContextHolder.getLocale()));
            camposInvalido.add(fieldErro);
        });
        return camposInvalido;
    }

}
