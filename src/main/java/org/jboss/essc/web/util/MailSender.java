package org.jboss.essc.web.util;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailSender {
    private static final Logger log = LoggerFactory.getLogger(MailSender.class);
    
    // Default - probably localhost.
    @Resource(mappedName="java:jboss/mail/Default")
    private Session mailSessionDefault;
    
    // Needs VPN
    @Resource(mappedName="java:jboss/mail/RedHat")
    private Session mailSessionRedHat;

    // If everything else fails (devel purposes)
    @Resource(mappedName="java:jboss/mail/Seznam")
    private Session mailSessionSeznam;
    
    private String mailFrom    = "essc-list@redhat.com";
    private String mailReplyTo = "essc-list@redhat.com";
    private String mailContentType = "text/plain";
    private String mailEncoding    = "utf-8";
    


    /**
    * Sends a mail to specified address.
    */
    public void sendMail( String sMailTo, String sSubject, String sMailText ) throws Exception {

        log.info("Sending mail to " + sMailTo + " - " + StringUtils.abbreviate(sSubject, 30));
        
        // Localhost
        try {
            sendMail_( mailSessionDefault, this.mailFrom, sMailTo, sSubject, sMailText);
        }
        catch( MessagingException ex ){
            log.warn("Error sending mail over localhost SMTP: " + ex.getMessage());
            // Red Hat
            try {
                sendMail_( mailSessionRedHat, this.mailFrom, sMailTo, sSubject, sMailText);
            }
            catch( MessagingException ex2 ){
                log.warn("Error sending mail over RedHat SMTP: " + ex2.getMessage());
                // Seznam
                try {
                    sendMail_( mailSessionSeznam, MAIL_FROM_SEZNAM, sMailTo, sSubject, sMailText);
                }
                catch( MessagingException ex3 ){
                    log.error("Error sending mail: " + ex3.getMessage());
                    throw new Exception( "Error sending mail over failover SMTP: " + ex3.getMessage(), ex3 );
                }
            }
        }

    }// sendMail()
    private static final String MAIL_FROM_SEZNAM = "zizka@seznam.cz";
    
    
    private void sendMail_(Session mailSessionSeznam, String mailFrom, String sMailTo, String sSubject, String sMailText) throws MessagingException {
        try {
            MimeMessage message = new MimeMessage( mailSessionSeznam );
            message.setFrom( new InternetAddress( mailFrom ) );
            message.setReplyTo( new Address[]{new InternetAddress( this.mailReplyTo )} );
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( sMailTo ) );
            message.setHeader( "Content-Type", this.mailContentType + "; charset=\"" + this.mailEncoding + "\"");
            message.setSubject( sSubject );
            message.setText( sMailText );
            Transport.send( message );
        }
        catch( MessagingException ex ){
            throw ex;
            //log.error("Error sending mail: " + ex.getMessage(), ex);
            //throw new Exception( "Error sending mail: " + ex.getMessage(), ex );
        }

    }// sendMail_()

    
}// class
