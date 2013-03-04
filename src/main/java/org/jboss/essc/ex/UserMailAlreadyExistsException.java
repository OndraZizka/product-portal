package org.jboss.essc.ex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 *  @author Ondrej Zizka
 */
public class UserMailAlreadyExistsException extends ProductPortalException {
    public static final Logger log = LoggerFactory.getLogger(UserMailAlreadyExistsException.class);

    public UserMailAlreadyExistsException(String message) {
        super(message);
    }

    public UserMailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserMailAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}// class UserAlreadyExistsException
