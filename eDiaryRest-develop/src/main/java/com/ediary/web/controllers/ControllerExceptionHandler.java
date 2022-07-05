package com.ediary.web.controllers;

import com.ediary.exceptions.NoAccessException;
import com.ediary.exceptions.NotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@Profile("default")
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ModelAndView handleNotFoundException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errors/notFoundError");
        modelAndView.addObject("exception", ex);
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NoAccessException.class)
    public ModelAndView handleLoginException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errors/noLoginError");
        modelAndView.addObject("exception", ex);
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errors/noAccessError");
        modelAndView.addObject("exception", ex);
        return modelAndView;
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errors/internalServerError");
        modelAndView.addObject("exception", ex);
        return modelAndView;
    }

}
