
function validateURL(val){
    return val.substring(0, 7) === "http://"
        || val.substring(0, 8) === "https://"
        || val.substring(0, 6) === "ftp://";
}

var EditableLink = {

    /**
     *  Init element's stuff.
     *  STATIC
     */
    init: function(eContainer){
        eContainer.getImage = function(){ return this.getElementsByTagName('img')[0]; };
        eContainer.getInput = function(){ return this.getElementsByTagName('input')[0]; };
        eContainer.getInput().activate  = EditableLink.activate;
        eContainer.getInput().passivate = EditableLink.passivate;
        eContainer.getInput().onChangeAjaxHandler = function(){};

        // Initial state is passive.
        eContainer.getInput().passivate();
    },
    /**
     *  Sets the onChangeAjaxHandler to something what communicates with server.
     *  APPLIED TO input <div>.
     */
    ajaxify: function( callbackUrl ){
        this.onChangeAjaxHandler = function(){
            var xhr = createXMLHttpRequest();
            var eInput = this;
            xhr.onreadystatechange = function() {
                if( this.readyState !== 4 ) return;
                if( this.status === 200 ) {
                    eInput.value = this.responseText;
                }
                else {
                    eInput.className += " ajaxError";
                }
            };
            xhr.open('POST', callbackUrl, true);
            // xhr.overrideMimeType("application/x-www-form-urlencoded; charset=...");
            xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            xhr.send("val=" + eInput.value);
        }
    },
    /**
     *  APPLIED TO the <input>.
     */
    activate: function(){
        //$(this).removeClass('passive').addClass('active').focus();
        this.className = (this.className.replace('passive','') + ' active');
        //this.disabled = false;
        this.readOnly = false;
        this.active = true;
        this.focus();
        this.oldValue = this.value;
    },
    /**
     *  APPLIED TO the <input>.
     */
    passivate: function(){
        //$(this).removeClass('active').addClass('passive');
        this.className = (this.className.replace('active','').replace('passive','') + ' passive');
        //this.disabled = true;
        this.readOnly = true;
        if( ! this.active ) return;
        this.active = false;
        this.onChangeAjaxHandler();
    },
    /**
     *  APPLIED TO the <input>.
     */
    onclick: function(event){
        if( this.active ) return;
        if(validateURL(this.value))
            window.open(this.value, '', 'location=yes,menubar=no,resizable=yes,scrollbars=yes,status=yes,modal=true,alwaysRaised=yes');
    },
};


/**
 *  XHR factory.
 */
function createXMLHttpRequest() {
    if (typeof XMLHttpRequest != 'undefined') {
        return new XMLHttpRequest();
    }
    try {
        return new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) {
        }
    }
    return false;
}
