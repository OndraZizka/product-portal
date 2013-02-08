package org.jboss.essc.ex;

/**
 *
 * @author Ondrej Zizka
 */
public class ProductPortalException extends Exception{

    public ProductPortalException(String message) {
        super(message);
    }

    public ProductPortalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductPortalException(Throwable cause) {
        super(cause);
    }

    public ProductPortalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    

}
