package org.jboss.essc.integ.trackers;

/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public interface IVersionNamesFilter {
    
    boolean isOK( String name );
    
    public static final IVersionNamesFilter DEFAULT = new IVersionNamesFilter() {

        @Override
        public boolean isOK(String name) {
            if( name == null )  return false;

            String nameLower = name.toLowerCase();

            if( name.contains("TBD") ) return false;
            if( nameLower.contains("unspecified") ) return false;
            if( nameLower.contains("no release") ) return false;
            if( nameLower.contains("future") ) return false;
            if( nameLower.contains("continuing") ) return false;

            return true;        
        }

    };
        
}// interface
