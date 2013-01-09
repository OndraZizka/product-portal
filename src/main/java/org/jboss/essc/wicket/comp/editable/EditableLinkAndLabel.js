
function validateURL(val){
    return val.substring(0, 7) === "http://"
        || val.substring(0, 8) === "https://"
        || val.substring(0, 6) === "ftp://";
}

var EditableLink = {
    /**
     *  Init element's stuff.
     */
    init: function(){
        this.onChangeAjaxHandler = function(){};
        this.passivate();
    },
    /**
     *  Sets the onChangeAjaxHandler to something what communicates with server.
     */
    ajaxify: function(){
        this.onChangeAjaxHandler = function(){
            var xhr = createXMLHttpRequest();
            var that = this;
            xhr.onreadystatechange = function() {
                if( this.readyState !== 4 ) return;
                if( this.status === 200 ) {
                    that.value = this.responseText;
                }
                else {
                    that.className += " ajaxError";
                }
            };
            xhr.open('POST', '${callbackUrl}', true);
            // xhr.overrideMimeType("application/x-www-form-urlencoded; charset=...");
            xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            xhr.send("val=" + this.value);
        }
    },
    activate: function(){
        //$(this).removeClass('passive').addClass('active').focus();
        this.className = (this.className.replace('passive','') + ' active');
        this.active = true;
        this.readOnly = false;
        //this.disabled = false;
        this.focus();
        this.oldValue = this.value;
    },
    passivate: function(){
        //$(this).removeClass('active').addClass('passive');
        this.className = (this.className.replace('active','') + ' passive');
        this.active = false;
        this.readOnly = true;
        //this.disabled = true;
        this.onChangeAjaxHandler();
    },
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