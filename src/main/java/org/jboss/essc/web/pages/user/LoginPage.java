package org.jboss.essc.web.pages.user;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.jboss.essc.ex.UserMailAlreadyExistsException;
import org.jboss.essc.ex.UserNameAlreadyExistsException;
import org.jboss.essc.web.dao.UserDao;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.home.HomePage;
import org.jboss.essc.web.util.MailSender;
import org.jboss.essc.web.util.PicketBoxAuthPojo;
import org.picketbox.exceptions.PicketBoxProcessingException;
import org.picketbox.plugins.PicketBoxProcessor;
import org.slf4j.LoggerFactory;


/**
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class LoginPage extends BaseLayoutPage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoginPage.class);

    @Inject private transient UserDao userDao;
    @Inject private transient MailSender mailSender;
    
    
    // Components
    private Form<User> form;
    private FeedbackPanel feedback;

    // Data
    private User user = new User();
    

    
    public LoginPage(PageParameters params) {
        
        //String userName = params.get("user").toOptionalString();
        

        this.form = new Form<User>("form");
        this.form.setOutputMarkupId(true);
        
        // Feedback
        this.feedback = (FeedbackPanel) new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this.form)).setOutputMarkupId(true);
        this.add(this.feedback);
        //feedback.info("FB foo");
        //form.info("Form foo");

        // User, pass
        this.form.add(new TextField("user", new PropertyModel(this.user, "name")));
        this.form.add(new PasswordTextField("pass", new PropertyModel(this.user, "pass")).setRequired(false));
        
        // Login button
        final AjaxButton loginBtn = new AjaxButton("login") {
            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                target.add( feedback );
                
                if( "".equals(user.getName()) || "".equals(user.getPass()) ){
                    getPage().get("user").error("Please fill the username and password.");
                    return;
                }
                
                //checkLoginWithPicketBox();
                try {
                    //User user_ = userDao.loadUserIfPasswordMatches( user );
                    //if( !  LoginPage.this.getSession().authenticate( user ) )
                    if( !  LoginPage.this.getSession().signIn( user.getName(), user.getPass() ) )
                        throw new NoResultException("No such user.");
                    // TODO:
                    //if( userDao.isTempPassword( user.getName(), user.getPass() ) )
                    //    setResponsePage(UserChangePassword.class);
                    //else
                    setResponsePage(HomePage.class);
                }
                catch( NoResultException ex ){
                    //setResponsePage(HomePage.class);
                    error("Wrong password or non-existent user: " + user.getName() + " / " + user.getPass());
                    info( "To get forgotten password, fill in user name and/or email.");
                }
            }
        };
        this.form.add( loginBtn );
        
        
        // Mail
        this.form.add(new TextField("mail", new PropertyModel(this.user, "mail")).add( EmailAddressValidator.getInstance() ));

        // Register button
        final AjaxButton regisButton = new AjaxButton("regis"){
            @Override protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add( LoginPage.this.form );
                target.add( LoginPage.this.feedback );
                
                if( StringUtils.isBlank(user.getMail()) ){
                    getPage().get("user").error("Please fill the mail.");
                    return;
                }

                
                log.info("Registering user: " + user.toString());
                
                // Email already registered?
                if( userDao.userMailExists(user.getMail()) ){
                    // Reset password.
                    try{
                        resetPassword(user);
                        this.info("Password reset challenge was sent by mail.");
                    } catch (Exception ex){
                        this.error("Could not reset password: " + ex.getMessage() );
                    }
                }
                // New e-mail.
                else {
                    if( StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getPass()) ){
                        getForm().error("Username and password must be filled for registration.");
                        return;
                    }

                    try {
                        userDao.addUser(user);
                        setResponsePage(UserAccountPage.class);
                    }
                    catch( UserNameAlreadyExistsException ex){
                        this.error("User name '" + user.getName() + "' is already taken, please choose other.");
                    }
                    catch( UserMailAlreadyExistsException ex){
                        this.error("We already have registration for mail '" + user.getMail()+ "'. Lost password?");
                    }
                    catch( Exception ex){
                        log.error("Failed registering user: ", ex);
                        this.error("Failed registering user: " + ex.getMessage());
                    }
                }
            }
        };
        this.form.add( regisButton );

        // Show productization releases?
        //CheckBox showProd = new CheckBox("prod", new PropertyModel(this.user, "showProd"));
        //this.form.add( showProd );
        
        add(this.form);
    }


    /**
     *   Generates a temp password and sends it to user's mail.
     */
    private void resetPassword( final User userData ) throws Exception {
        User user = userDao.getUserByMail( userData.getMail() );
        String pass = RandomStringUtils.randomAlphanumeric(8);
        user.setPassTemp(DigestUtils.md5Hex( pass ) );
        user = userDao.update(user);
        
        mailSender.sendMail( user.getMail(), "Product portal password reset", 
                "User name:          " + user.getName() + "\n" +
                "Temporary password: " + pass + "\n"
        );
    }
    
    @Schedule(hour = "6")
    public void eraseTempPasswords(){
        userDao.eraseTempPasswords();
    }
    
    
    
    public User getUser() { return user; }
    public void setUser( User user ) { this.user = user; }

    
    
    
    /**
     *   Test of PicketBox login approach. Doesn't work - says 'Invalid'.
     */
    private void checkLoginWithPicketBox() {
        try {
            //ServletContext sc = (ServletContext) getRequest().getContainerRequest();

            PicketBoxProcessor processor = new PicketBoxProcessor();
            processor.setSecurityInfo("admin", "aaa");
            processor.process( new PicketBoxAuthPojo() );

            /*Principal admin = new SimplePrincipal("admin");
            processor.getCallerPrincipal().equals( admin );
            Subject callerSubject = processor.getCallerSubject(); // Null?
            callerSubject.getPrincipals().contains(admin);
            RoleGroup callerRoles = processor.getCallerRoles();
            */

            feedback.info( "Logged succesfully." );
            error("Foo");
        }
        catch( LoginException ex ) {
            feedback.error( "Login error: " + ex.getMessage() );
            error("Foo");
            //throw new RuntimeException(ex);
        }
        catch( PicketBoxProcessingException ex ) {
            Logger.getLogger( LoginPage.class.getName() ).log( Level.SEVERE, null, ex );
            feedback.error( "Login error: " + ex.getMessage() );
            error("Foo");
            //throw new RuntimeException(ex);
        }
    }
    
    
}// class
