package org.example.exception;

import org.example.dtos.ErrorDto;

public class MessengerException extends RuntimeException
{
    private ErrorDto errorDto;

    public MessengerException(ErrorDto errorDto)
    {
        this.errorDto = errorDto;
    }

    public MessengerException(Throwable cause, ErrorDto errorDto)
    {
        super(cause);
        this.errorDto = errorDto;
    }

    public ErrorDto getErrorDto()
    {
        return errorDto;
    }

    public void setErrorDto(ErrorDto errorDto)
    {
        this.errorDto = errorDto;
    }

}
