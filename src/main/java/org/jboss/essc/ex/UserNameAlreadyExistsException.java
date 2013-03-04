package org.jboss.essc.ex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 *  @author Ondrej Zizka
 */
public class UserNameAlreadyExistsException extends ProductPortalException {
    public static final Logger log = LoggerFactory.getLogger(UserNameAlreadyExistsException.class);

    public UserNameAlreadyExistsException(String message) {
        super(message);
    }

    public UserNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNameAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}// class UserAlreadyExistsException
